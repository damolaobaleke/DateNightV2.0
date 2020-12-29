package com.datenight_immersia_ltd.modelfirestore.User;


import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.util.HashMap;
import java.util.List;

public class UserModel {
    public String id;
    public String username;
    public String name; //fullName
    public String email;
    public Timestamp dateOfBirth; //Date equivalent in firestore
    public HashMap<String, String> avatar; //avatar = string in db .glb file :: android image resource takes int value  >>String,String || String, Integer<<
    public String status; //BASIC || PREMIUM USER
    public String search; //as firestore query is case sensitive-- this field is used to store the lowercase of the username, and search string would be converted to lower case
    int dtc;
    String loginMethod;
    public String stripeCustomerId;///
    List<String> purchasedExperiences;
    List<String> dateId; // String[]
    public UserStatsModel statistics;

    public UserModel() {
        /**Public no arg constructor needed*/
    }

    public UserModel(String username, String name, String email, Timestamp dateOfBirth, HashMap<String, String> avatar, String status, List<String> dateId, UserStatsModel statistics, String stripeCustomerId, String search) {
        //this.id = id;
        this.username = username;
        this.name = name;
        this.search = search;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.avatar = avatar;
        this.status = status;
        this.dateId = dateId;
        this.statistics = statistics;
        this.stripeCustomerId = stripeCustomerId;
    }

    /**Constructor for invite user activity- majorly to get the avatar image*/
//    public UserModel(String username, String name, String email, Timestamp dateOfBirth, Integer avatarHead, String status, Object dateId, Object statistics, String stripeCustomerId, String search) {
//      //set instance variables to local variables
//    }


    public void setId(String id) {
        this.id = id;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Timestamp getDateOfBirth() {
        return dateOfBirth;
    }

    public HashMap<String, String> getAvatar() {
        return avatar;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getDateId() {
        return dateId;
    }

    public UserStatsModel getStatistics() {
        return statistics;
    }

    public String getStripeCustomerId() {
        return stripeCustomerId;
    }

}
