package com.datenight_immersia_ltd.network.api;

public class PostUser {
    boolean success;
    String message;
    User data; //@SerializedName("data") doesn't work, if not var = user

    public PostUser(boolean success, String message, User data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return data;
    }
}
