package com.dhanush.tweetapp.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("likes")
public class Like {
    @Id
    private ObjectId id;
    @Indexed(unique = false)
    private ObjectId likedTweet;
    private String likedBy;
}
