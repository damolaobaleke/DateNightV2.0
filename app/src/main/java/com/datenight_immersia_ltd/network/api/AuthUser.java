package com.datenight_immersia_ltd.network.api;


public class AuthUser {

    //String _id;
    boolean authenticated;
    String token;
    User user;

    public AuthUser(boolean authenticated, String token,User user) {
        this.authenticated = authenticated;
        this.token = token;
        this.user = user;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public User getUser() {
        return user;
    }

}