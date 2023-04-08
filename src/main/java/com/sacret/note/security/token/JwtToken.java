package com.sacret.note.security.token;


import java.io.Serializable;

public record JwtToken(String token) implements Serializable {

}
