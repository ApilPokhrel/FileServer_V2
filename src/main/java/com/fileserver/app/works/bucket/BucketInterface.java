package com.fileserver.app.works.bucket;


import com.fileserver.app.works.file.FileSchema;
import com.fileserver.app.works.network.RequestSchema;
import com.fileserver.app.works.user.UserSchema;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BucketInterface {

   public BucketSchema create(BucketSchema bucket) throws Exception;

    BucketSchema save(BucketSchema bucketSchema);

    BucketSchema update(BucketSchema bucketSchema);

    BucketSchema findOne(String field, String value);

    List<BucketSchema> findAll(int limit, long skip);

    BucketSchema findOneAndUpdate(String field, String value, String updateField, Object updateValue, String type);

    BucketSchema addUrl(BucketSchema bucketSchema, String url);

    BucketSchema removeUrl(BucketSchema bucketSchema, String url);

 BucketSchema findOneAndSet(String find_field, String find_value, String set_field, String set_value, String set_value_type);

 BucketSchema updateStat(BucketSchema bucketSchema, int stat);

 BucketSchema updateFile_Count(BucketSchema bucketSchema, int file_count);

 BucketSchema updateGap(BucketSchema bucketSchema, int gap);

 BucketSchema addFile(BucketSchema bucketSchema, FileSchema fileSchema);

 BucketSchema set_size_used(BucketSchema bucketSchema, int size_used);

    BucketSchema findFile(String bucketName, String fileName);

    BucketSchema removeOwner(BucketSchema bucketSchema, String owner);

    BucketSchema addOwner(BucketSchema bucketSchema, String owner);

    BucketSchema removeFile(BucketSchema bucketSchema, FileSchema fileSchema);

    BucketSchema addBackupFile(BucketSchema bucketSchema, String fileId);

    BucketSchema removeBackupFile(BucketSchema bucketSchema, String fileId);

    BucketSchema setRequest(BucketSchema bucketSchema, RequestSchema requestSchema);
}
