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
    public int dtc;
    public String loginMethod;
    public String stripeCustomerId;
    public String ephemeralKey;
    List<String> purchasedExperiences;
    UserPreferences userPreferences;
    List<String> dateId;
    public UserStatsModel avgDateStats;

    public UserModel() {
        /**Public no arg constructor needed*/
    }

    public UserModel(String id, String username, String name, String email, Timestamp dateOfBirth, HashMap<String, String> avatar, String status, List<String> dateId, UserStatsModel statistics, String stripeCustomerId) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.avatar = avatar;
        this.status = status;
        this.dateId = dateId;
        this.avgDateStats = statistics;
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

    public UserStatsModel getAvgDateStats() {
        return avgDateStats;
    }

    public String getStripeCustomerId() {
        return stripeCustomerId;
    }

    public String getEphemeralKey() {
        return ephemeralKey;
    }

    public int getDtc() {
        return dtc;
    }

    public void setDtc(int dtc) {
        this.dtc = dtc;
    }

    public void setAvgDateStats(UserStatsModel avgDateStats) {
        this.avgDateStats = avgDateStats;
    }
}
