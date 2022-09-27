package com.dhanush.tweetapp.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("tokens")
public class Token {
    @Id
    private ObjectId id;
    @Indexed
    private final String username;
    @Indexed
    private final String token;
}
