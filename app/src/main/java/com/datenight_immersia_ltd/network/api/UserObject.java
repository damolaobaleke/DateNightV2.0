package com.datenight_immersia_ltd.network.api;

import java.util.List;

public class UserObject {
    boolean success;
    String message;
    List<User> data;

    public UserObject(boolean success, String message, List<User> data) {
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

    public List<User> geUser() {
        return data;
    }
}
