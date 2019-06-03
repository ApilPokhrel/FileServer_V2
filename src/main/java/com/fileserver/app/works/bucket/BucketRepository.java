package com.fileserver.app.works.bucket;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BucketRepository extends MongoRepository<BucketSchema, String> {
}
