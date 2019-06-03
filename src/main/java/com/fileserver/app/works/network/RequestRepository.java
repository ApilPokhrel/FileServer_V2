package com.fileserver.app.works.network;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RequestRepository extends MongoRepository<RequestSchema, String> {
}
