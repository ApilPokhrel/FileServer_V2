package com.fileserver.app.works.file;


import com.fileserver.app.config.Variables;
import com.fileserver.app.handler.DateAndTime;
import com.fileserver.app.works.bucket.BucketInterface;
import com.fileserver.app.works.bucket.BucketSchema;
import com.fileserver.app.works.network.RequestInterface;
import com.fileserver.app.works.network.RequestSchema;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Service
public class FileController {

    private BucketInterface bucketInterface;
    private FileInterface fileInterface;
    private DateAndTime dateAndTime;
    private RequestInterface requestInterface;

    @Autowired
    public FileController(BucketInterface bucketInterface,
                          FileInterface fileInterface,
                          DateAndTime dateAndTime,
                          RequestInterface requestInterface){
        this.bucketInterface = bucketInterface;
        this.fileInterface = fileInterface;
        this.dateAndTime = dateAndTime;
        this.requestInterface = requestInterface;
    }

    public FileController(){
        super();
    }

    Variables variables = new Variables();

    public BucketSchema AuthorizeKey(String key, String bucketName) throws Exception {
        BucketSchema bucketSchema = null;
        try {
            bucketSchema = bucketInterface.findOne("name", bucketName);
            if (bucketSchema == null) {
                throw new Exception("Unauthorized Bucket");
            }
            boolean exist = false;
            for (String userKey : bucketSchema.getOwners()) {
                if (userKey.equals(key)){
                    exist = true;
                }
            }

            if(!exist){
                throw new Exception("Unauthorized Bucket Key");
            }
        }catch (Exception ex){
            throw  new Exception(ex.getLocalizedMessage());
        }

        return bucketSchema;
    }


    public BucketSchema validateUrl(String url) throws Exception {
        BucketSchema bucketSchema = bucketInterface.findOne("urls", url);
        if(bucketSchema == null){
            throw new Exception("bucket not found");
        }

        return bucketSchema;
    }


    public int[] evaluateSaturation(BucketSchema bucketSchema, MultipartFile file, String fileName, String uploadedFrom) throws Exception {
        int stat = bucketSchema.getSaturation().getStat();
        int file_count = bucketSchema.getSaturation().getFile_count_stat() < 1 ? 0 : bucketSchema.getSaturation().getFile_count_stat();

        if((file_count + 1) < bucketSchema.getSaturation().getGap()){
                this.upload(bucketSchema, file, fileName, stat, uploadedFrom);

            file_count = file_count + 1;
            bucketInterface.updateFile_Count(bucketSchema, file_count);
        } else{
            stat = stat +1;
            File folder = new File(variables.SERVER_FOLDER+bucketSchema.getName()+"/"+stat);//Bucket Folder
            try{
                folder.mkdir();
            }catch(SecurityException ex){
                throw new Exception(ex.getMessage());
            }
            this.upload(bucketSchema, file, fileName, stat, uploadedFrom);
            bucketInterface.updateStat(bucketSchema, stat);
            file_count = 1;
            bucketInterface.updateFile_Count(bucketSchema, file_count);

        }
        return new int[]{stat, file_count};
    }

    public void upload(BucketSchema bucketSchema, MultipartFile file, String fileName, int stat, String uploadedFrom) throws IOException {

        FileSchema fileSchema = new FileSchema();
        fileSchema.setName(fileName);
        fileSchema.setSat(stat);
        fileSchema.setType(this.getFileType(file)[0]);
        fileSchema.setSize((int) file.getSize());
        fileSchema.setExt(this.getFileType(file)[1]);
        fileSchema.setUploadedFrom(uploadedFrom);

        fileSchema.setUploadedAt(dateAndTime.isoTimeNow());
        fileSchema = fileInterface.create(fileSchema);

        bucketInterface.addFile(bucketSchema, fileSchema);

        String uploadPath = variables.SERVER_FOLDER+bucketSchema.getName()+"/"+stat+"/"+fileName+"."+this.getFileType(file)[1];
        InputStream in = file.getInputStream();
        Files.copy(in, Paths.get(uploadPath), StandardCopyOption.REPLACE_EXISTING);
        bucketInterface.set_size_used(bucketSchema, (int) (bucketSchema.getSize_used()+file.getSize()));//in bytes

    }

    public void validateFileType(BucketSchema bucketSchema, String file_type) throws Exception {
           boolean exist = false;
           if(file_type.equalsIgnoreCase("application") || file_type.equalsIgnoreCase("text")){
               file_type = "document";
           }
           for(String type : bucketSchema.getAllowed_file_type()){
              if(type.equalsIgnoreCase(file_type)) exist = true;
           }

           if(!exist){
               throw  new Exception("file type "+file_type+" not supported");
           }

    }

    public void validateMethod(BucketSchema bucketSchema, String method) throws Exception {
        System.out.println("inside method");
        boolean exist = false;
        for(String m : bucketSchema.getAllowed_methods()){
           if(m.equalsIgnoreCase(method)) exist = true;
        }

        if(!exist){
            throw new Exception("method "+method+" not supported");
        }
    }


    public  void validateBucketThreshold(BucketSchema bucketSchema, long threshold) throws Exception {
         if(Math.abs(bucketSchema.getThreshold()) < ((bucketSchema.getSize_used() + threshold)/ 1073741824)){ //in bytes
             throw new Exception("bucket size exceeded");
         }
    }

    public String[] getFileType(MultipartFile file){
       String mime = file.getContentType();
       String[] contexts = mime.split("/");
       return contexts;
    }


    public BucketSchema deleteFileById(String bucketId, String fileId) throws Exception {
        BucketSchema bucketSchema = bucketInterface.findOne("_id", bucketId);
        FileSchema fileSchema = fileInterface.findOne("_id", fileId);
        if(bucketSchema == null || fileSchema == null){
            throw new Exception("File Not Found");
        }
        bucketInterface.removeFile(bucketSchema, fileSchema);
        bucketInterface.addBackupFile(bucketSchema, fileSchema.getId());
        bucketInterface.set_size_used(bucketSchema, bucketSchema.getSize_used() - fileSchema.getSize());
        return bucketSchema;
    }

    public BucketSchema deleteFileByName(String bucketName, String fileName) throws Exception {
        BucketSchema bucketSchema = bucketInterface.findOne("name", bucketName);
        FileSchema fileSchema = fileInterface.findOne("name", fileName);
        if(bucketSchema == null || fileSchema == null){
            throw new Exception("File Not Found");
        }
        bucketInterface.removeFile(bucketSchema, fileSchema);
        bucketInterface.addBackupFile(bucketSchema, fileSchema.getId());
        bucketInterface.set_size_used(bucketSchema, bucketSchema.getSize_used() - fileSchema.getSize());
        return bucketSchema;
    }

    public RequestSchema setRequest(BucketSchema bucketSchema, HttpServletRequest request){
        RequestSchema requestSchema = new RequestSchema();

            requestSchema.setClientIp((request.getHeader("X-FORWARDED-FOR") != null) ? request.getHeader("X-FORWARDED-FOR") : request.getRemoteAddr());
            requestSchema.setMethod(request.getMethod());
            requestSchema.setUri(request.getRequestURI());
            requestSchema.setReferrer(request.getHeader("referer"));
            requestSchema.setTime(dateAndTime.getTimeOnly());
            requestSchema.setDate(dateAndTime.getDateOnly());
            requestSchema.setHost(request.getRemoteHost());

            UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        try {
            Browser browser = userAgent.getBrowser();

            //or
            String browserName = browser.getGroup().getName();
            requestSchema.setOS(userAgent.getOperatingSystem().getName());
            Version browserVersion = userAgent.getBrowserVersion();
            requestSchema.setBrowser(browserName);
            requestSchema.setBrowserVersion(browserVersion.getVersion());
        }catch (Exception ex){

        }
            requestSchema = requestInterface.add(requestSchema);
            bucketInterface.setRequest(bucketSchema, requestSchema);

        return requestSchema;
    }

    public void evaluateFilename(BucketSchema bucketSchema, String fileName){
        FileSchema fileSchema = fileInterface.findOne("name", fileName);
    }


    public String mimeCheck(String type, String mime){
        String m = mime;
       if(type.equalsIgnoreCase("video")){
         if(mime.equalsIgnoreCase("x-ms-wmv")){
             m = "x-ms-asf";
         }
       }
       if(type.equalsIgnoreCase("image")){

       }

        if(type.equalsIgnoreCase("document")){
           m = "application";
        }
       return m;
    }





}
