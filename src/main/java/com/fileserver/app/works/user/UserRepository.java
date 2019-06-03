package com.fileserver.app.works.user;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<UserSchema, String>{
    UserSchema findByToken(String token);
}
