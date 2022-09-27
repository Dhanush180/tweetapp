package com.dhanush.tweetapp.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dhanush.tweetapp.model.Token;

import java.util.Optional;

public interface TokenRepository extends MongoRepository<Token, ObjectId> {
    Optional<Token> findByToken(String token);

    void deleteAllByUsername(String username);

    void deleteByToken(String token);
}
