package com.datenight_immersia_ltd.modelfirestore.User;


import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.util.List;

public class UserModel {
    public String id;
    public String username;
    public String fullName;
    public String email;
    public Timestamp dateOfBirth; //Date equivalent in firestore
    public int avatar; //avatar = string in db .glb file :: android image resource takes int value
    public String status; //BASIC || PREMIUM USER
    public String userdateId;
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

    public UserModel(String username, String fullname, String email, Timestamp dateOfBirth, int avatar, String status, List<String> dateId, String userdateId, UserStatsModel statistics, String stripeCustomerId, String search) {
        //this.id = id;
        this.username = username;
        this.fullName = fullname;
        this.search = search;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.avatar = avatar;
        this.status = status;
        this.dateId = dateId;
        this.userdateId = userdateId;
        this.statistics = statistics;
        this.stripeCustomerId = stripeCustomerId;
    }


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
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public Timestamp getDateOfBirth() {
        return dateOfBirth;
    }

    public int getAvatar() {
        return avatar;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getDates() {
        return dateId;
    }

    public String getUserdateId() {
        return userdateId;
    }

    public UserStatsModel getStatistics() {
        return statistics;
    }

    public String getStripeCustomerId() {
        return stripeCustomerId;
    }

    public String getLowCaseUsername() {
        return search;
    }
}
