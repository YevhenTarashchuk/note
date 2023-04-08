package com.sacret.note.constant;

public class ExceptionConstant {

    public static final String USER_EXISTS = "User with login: %s already exists";
    public static final String USER_NOT_FOUND = "User with %s: %s is not found";
    public static final String POST_NOT_FOUND = "Post with id: %s is not found";
    public static final String INVALID_CREDENTIALS = "Invalid login or password";
    public static final String INVALID_TOKEN = "Invalid JWT token";
    public static final String LOGIN = "login";
    public static final String ID = "id";



    private ExceptionConstant() {
    }
}
