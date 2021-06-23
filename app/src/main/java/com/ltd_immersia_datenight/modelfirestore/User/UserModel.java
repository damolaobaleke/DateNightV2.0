package com.ltd_immersia_datenight.modelfirestore.User;


import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class UserModel implements Serializable {
    private String id;
    private String username;
    public String name; //fullName
    public String email;
    public Timestamp dob; //Date equivalent in fire store
    public HashMap<String, String> avatar; //avatar = string in db .glb file :: android image resource takes int value  >>String,String || String, Integer<<
    public String status; //BASIC || PREMIUM USER
    public int dtc;
    public String loginMethod;
    public String stripeCustomerId;
    public String ephemeralKey;
    private Timestamp dateCreated;
    private boolean onBoarded ;
    private String fcmToken;
    List<String> purchasedExperiences;
    UserPreferences userPreferences;
    List<String> dateId;
    public UserStatsModel avgDateStats;

    public UserModel() {
        /**Public no arg constructor needed*/
    }

    public UserModel(String id, String username, String name, String email, Timestamp dob, HashMap<String, String> avatar, String status, String loginMethod, List<String> dateId, UserStatsModel statistics, List<String> purchasedExperiences, String stripeCustomerId, Timestamp dateCreated, boolean onBoarded, String fcmToken) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.dob= dob;
        this.avatar = avatar;
        this.status = status;
        this.loginMethod = loginMethod;
        this.dateId = dateId;
        this.purchasedExperiences = purchasedExperiences;
        this.avgDateStats = statistics;
        this.stripeCustomerId = stripeCustomerId;
        this.dateCreated = dateCreated;
        this.onBoarded = onBoarded;
        this.fcmToken = fcmToken;
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

    public Timestamp getDob() {
        return dob;
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

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public boolean isOnBoarded() {
        return onBoarded;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setDtc(int dtc) {
        this.dtc = dtc;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void setAvgDateStats(UserStatsModel avgDateStats) {
        this.avgDateStats = avgDateStats;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDob(Timestamp dob) {
        this.dob = dob;
    }

    public void setAvatar(HashMap<String, String> avatar) {
        this.avatar = avatar;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLoginMethod() {
        return loginMethod;
    }

    public void setLoginMethod(String loginMethod) {
        this.loginMethod = loginMethod;
    }

    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }

    public void setEphemeralKey(String ephemeralKey) {
        this.ephemeralKey = ephemeralKey;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setOnBoarded(boolean onBoarded) {
        this.onBoarded = onBoarded;
    }

    public List<String> getPurchasedExperiences() {
        return purchasedExperiences;
    }

    public void setPurchasedExperiences(List<String> purchasedExperiences) {
        this.purchasedExperiences = purchasedExperiences;
    }

    public UserPreferences getUserPreferences() {
        return userPreferences;
    }

    public void setUserPreferences(
            UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
    }

    public void setDateId(List<String> dateId) {
        this.dateId = dateId;
    }
}
