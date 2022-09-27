package com.dhanush.tweetapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dhanush.tweetapp.model.Tweet;


public interface TweetRepository extends MongoRepository<Tweet, String> {
    Page<Tweet> findByUsernameOrderByPostTimeDesc(String username, Pageable pageable);
    Page<Tweet> findAll(Pageable pageable);
}
