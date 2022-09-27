package com.dhanush.tweetapp.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Document(collection = "tweets")
@Data
@NoArgsConstructor
public class Tweet {
    @Id
    @JsonSerialize(using= ToStringSerializer.class)
    private ObjectId id;
    @Length(max=144,message = "Tweet cannot exceed 144 characters")
    @NotBlank(message = "Tweet cannot be Empty")
    private String tweet;
    private long postTime;
    private long likes;
    @Indexed(unique = false)
    private String username;
    @JsonSerialize(using= ToStringSerializer.class)
    private List<ObjectId> replies;
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId repliedTo;
    private List<@Length(max=50,message = "Tweet-Tag cannot exceed 50 character") String> tweetTag;
    private boolean isLikedByUser;
    private String repliedToMessage;
}
