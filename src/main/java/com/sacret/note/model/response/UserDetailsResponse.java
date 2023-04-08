package com.sacret.note.model.response;

import lombok.Getter;
import lombok.Setter;
import com.sacret.note.model.enumeration.Role;

@Getter
@Setter
public class UserDetailsResponse {
    private String userId;
    private String password;
    private Role role;
}
