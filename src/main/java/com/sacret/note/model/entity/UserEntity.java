package com.sacret.note.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import com.sacret.note.model.enumeration.Role;

@Getter
@Setter
@ToString
@Document(collection = "user")
public class UserEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String login;
    private String password;
    private Role role;
}
