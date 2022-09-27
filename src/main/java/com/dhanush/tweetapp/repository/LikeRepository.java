package com.dhanush.tweetapp.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dhanush.tweetapp.model.Like;


public interface LikeRepository extends MongoRepository<Like, ObjectId> {
    Long countByLikedTweet(ObjectId id);
    void deleteByLikedTweetAndLikedBy(ObjectId id,String username);

    Boolean existsByLikedTweetAndLikedBy(ObjectId id,String username);
    void deleteAllByLikedTweet(ObjectId id);
}
