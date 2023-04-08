package com.sacret.note.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Document(collection = "post_like")
public class PostLikeEntity {

    @Id
    private String id;

    @Indexed
    @Field(name = "post_id")
    private String postId;

    @Indexed
    @Field(name = "user_id")
    private String userId;
}
