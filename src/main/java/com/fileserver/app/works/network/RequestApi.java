package com.fileserver.app.works.network;

import com.fileserver.app.handler.Authenticate;
import com.fileserver.app.handler.Unauthorized;
import com.fileserver.app.works.bucket.BucketInterface;
import com.fileserver.app.works.bucket.BucketSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/request")
public class RequestApi {

    private Authenticate auth;
    private RequestInterface requestInterface;
    private BucketInterface bucketInterface;

    @Autowired
    public RequestApi(Authenticate auth,
                      RequestInterface requestInterface,
                      BucketInterface bucketInterface){
       this.auth = auth;
       this.requestInterface = requestInterface;
       this.bucketInterface = bucketInterface;
    }


    @GetMapping("/bucket/{bucketId}")
    public ResponseEntity getUserRequest(@PathVariable("bucketId") String bucketId) throws IOException, Unauthorized {
        auth.AuthorizeApi();
        BucketSchema bucketSchema = bucketInterface.findOne("_id", bucketId);
        if(bucketSchema == null){
            return ResponseEntity.status(400).body("No Bucket Found");
        }
        List<RequestSchema> requestSchemas = requestInterface.getAllById(bucketSchema.getRequests());
        return ResponseEntity.ok(requestSchemas);
    }
}
