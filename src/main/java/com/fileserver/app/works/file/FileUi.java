package com.fileserver.app.works.file;


import com.fileserver.app.config.Variables;
import com.fileserver.app.handler.Authenticate;
import com.fileserver.app.handler.Unauthorized;
import com.fileserver.app.works.bucket.BucketInterface;
import com.fileserver.app.works.bucket.BucketSchema;
import com.fileserver.app.works.settings.PlanController;
import com.fileserver.app.works.user.UserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

@Controller
@RequestMapping("/file")
public class FileUi {

    private Authenticate auth;
    private BucketInterface bucketInterface;
    private FileController fileController;
    private FileInterface fileInterface;
    private PlanController planController;
    private UserController userController;

    @Autowired
    public FileUi(Authenticate auth,
                  BucketInterface bucketInterface,
                  FileController fileController,
                  FileInterface fileInterface,
                  PlanController planController,
                  UserController userController
                  ){
      this.auth = auth;
      this.bucketInterface = bucketInterface;
      this.fileController = fileController;
      this.fileInterface = fileInterface;
      this.planController = planController;
      this.userController = userController;
    }
    Variables variables = new Variables();
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/{url}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity uploadWithUrl(@PathVariable("url") String signUrl,
                                        @RequestParam("file") MultipartFile file,
                                        HttpServletRequest request){

        BucketSchema bucketSchema = null;
        try {
            bucketSchema = fileController.validateUrl(signUrl);
            if(file.isEmpty()) {
                return ResponseEntity.status(400).body("file not found");
            }
//            planController.validatePlan(auth.getUser(),
//                    "post", userController.getTotalSize(auth.getUser()),
//                    userController.getTotalRequest(auth.getUser()));
            fileController.validateMethod(bucketSchema, "post");
            fileController.validateFileType(bucketSchema, fileController.getFileType(file)[0]);
            fileController.validateBucketThreshold(bucketSchema, file.getSize());
            fileController.evaluateSaturation(bucketSchema, file, signUrl.split("-")[2], request.getServerName());
            fileController.setRequest(bucketSchema, request);
            bucketInterface.removeUrl(bucketSchema, signUrl);
        }catch (Exception ex){
            return ResponseEntity.status(400).body(ex.getMessage());
        }
        return ResponseEntity.ok(true);
    }


    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity uploadLocal(@RequestParam("bucketName") String bucketName,
                                      @RequestParam("file") MultipartFile[] files,
                                      HttpServletRequest request) throws IOException, Unauthorized {
        auth.AuthorizeApi();
        BucketSchema bucketSchema = null;
        try {
             bucketSchema = bucketInterface.findOne("name", bucketName);
             if(bucketSchema.getStatus().equalsIgnoreCase("inactive")){
                 return ResponseEntity.status(400).body("Your bucket is under black list");
             }
            if (files[0].isEmpty()) {
                return ResponseEntity.status(400).body("file not found");
            }
            if (bucketSchema == null) {
                return ResponseEntity.status(400).body("bucket not found");
            }
//            planController.validatePlan(auth.getUser(),
//                    "post", userController.getTotalSize(auth.getUser()),
//                    userController.getTotalRequest(auth.getUser()));
            for(MultipartFile file : files) {
                fileController.validateMethod(bucketSchema, "post");
                fileController.validateFileType(bucketSchema, fileController.getFileType(file)[0]);
                fileController.validateBucketThreshold(bucketSchema, file.getSize());
                String filename = file.getOriginalFilename().split("\\.")[0];
                fileController.evaluateSaturation(bucketSchema, file, filename, "local");
                fileController.setRequest(bucketSchema, request);
            }
        }catch (Exception ex){
            return ResponseEntity.status(400).body(ex);
        }
        return ResponseEntity.ok(true);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/{bucket}/{file}", method = RequestMethod.GET)
    public void get(@PathVariable("bucket") String bucketName,
                    @PathVariable("file") String fileName,
                    @RequestParam(value = "download", required = false) String download,
                    HttpServletResponse response,
                    HttpServletRequest request) throws IOException {
        BucketSchema bucketSchema = bucketInterface.findFile(bucketName, "file_id");
        if(bucketSchema == null){
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
        }

        if(bucketSchema.getStatus().equalsIgnoreCase("inactive")){
            response.sendError(HttpServletResponse.SC_CONFLICT); // 409.
            return;
        }

        FileSchema fileSchema = bucketSchema.getFile(fileName);
        if(fileSchema == null){
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
        }
        try {
            fileController.validateMethod(bucketSchema, "get");
        }catch (Exception ex){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED); // 403.
            return;
        }

        String folderPath = variables.SERVER_FOLDER+bucketSchema.getName()+"/"+bucketSchema.getSaturation().getStat()+"/"+fileSchema.getName()+"."+fileSchema.getExt();

        String contentType = fileSchema.getType()+"/"+fileController.mimeCheck(fileSchema.getType(), fileSchema.getExt());
        fileName = fileSchema.getName()+"."+fileSchema.getExt();
        File file = new File(folderPath);
        // Write to output stream
        BufferedInputStream input = null;
        BufferedOutputStream output = null;
          final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
        response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setContentType(contentType);
        response.setHeader("Content-Length", String.valueOf(file.length()));
        response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
        if(download != null && download.equalsIgnoreCase("yes")) {
         response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");  //to make downloadable
        }
        try {
            // Open streams.
            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

            // Write file contents to response.
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        } finally {
            fileController.setRequest(bucketSchema, request);
            input.close();
            output.close();
        }
      return;

    }


    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/{bucket}/{file}/png", method = RequestMethod.GET)
    public ResponseEntity get2(@PathVariable("bucket") String bucketName,
                    @PathVariable("file") String fileName,
                    HttpServletResponse response,
                    HttpServletRequest request) throws IOException {


        BucketSchema bucketSchema = bucketInterface.findFile(bucketName, "file_id");
        if(bucketSchema == null){
            return ResponseEntity.status(400).body("Bucket Not Found");

        }

        FileSchema fileSchema = bucketSchema.getFile(fileName);
        if(fileSchema == null){
            return ResponseEntity.status(400).body("File Not Found");
        }

        String folderPath = variables.SERVER_FOLDER+bucketSchema.getName()+"/"+bucketSchema.getSaturation().getStat()+"/"+fileSchema.getName()+"."+fileSchema.getExt();
        String contentType = fileSchema.getType()+"/"+fileSchema.getExt();
        System.out.println(folderPath);
        File file = new File(folderPath);

        InputStream inputStream = new FileInputStream(file);
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(Files.size(Paths.get(folderPath)));
        fileController.setRequest(bucketSchema, request);
        return ResponseEntity.status(200)
                .contentType(MediaType.IMAGE_PNG)
                .body(inputStreamResource);

    }
}
