package com.datenight_immersia_ltd.modelfirestore.User;


import com.datenight_immersia_ltd.modelfirestore.Date.DateModel;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.List;

public class UserModel {
    public String id;
    public String username;
    public String firstName;
    public String lastName;
    //add full name field
    public String email;
    public Timestamp dateOfBirth; //Date equivalent firestore
    public URL avatar;
    public Boolean status;
    public String userdateId;
    public String stripeCustomerId;///
    List<DateModel> dates; //column--date_id
    public UserStatsModel statistics;

    public UserModel(){
        /**Public no arg constructor needed*/
    }

    public UserModel(String username, String firstName, String lastName, String email, Timestamp dateOfBirth, URL avatar, Boolean status, List<DateModel> dates, String userdateId,UserStatsModel statistics, String stripeCustomerId) {
        //this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.avatar = avatar;
        this.status = status;
        this.dates = dates;
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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Timestamp getDateOfBirth() {
        return dateOfBirth;
    }

    public URL getAvatar() {
        return avatar;
    }

    public Boolean getStatus() {
        return status;
    }

    public List<DateModel> getDates() {
        return dates;
    }

    public String getUserdateId() {
        return userdateId;
    }

    public UserStatsModel getStatistics() {
        return statistics;
    }

    public String getStripeCustomerId(){
        return  stripeCustomerId;
    }
}
