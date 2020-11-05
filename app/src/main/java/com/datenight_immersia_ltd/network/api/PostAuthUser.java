package com.datenight_immersia_ltd.network.api;

public class PostAuthUser {
    //boolean success;
    String message;
    AuthUser data; //@SerializedName("data") doesn't work, if not var = user

    public PostAuthUser(String message, AuthUser data) {
//        this.success = success;
        this.message = message;
        this.data = data;
    }

//    public boolean isSuccess() {
//        return success;
//    }
//
    public String getMessage() {
        return message;
    }

    public AuthUser getUserData() {
        return data;
    }
}
