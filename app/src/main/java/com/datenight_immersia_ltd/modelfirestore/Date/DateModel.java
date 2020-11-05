package com.datenight_immersia_ltd.modelfirestore.Date;

import com.datenight_immersia_ltd.modelfirestore.User.UserModel;

import java.net.URL;
import java.util.Date;

public class DateModel {
    String id;
    String password; //library for encryption crypto random 8 bit value
    //List<UserModel> participants -- update
    UserModel dateCreator;
    UserModel participant1; //reference to userEntity or Model, association
    UserModel participant2;
    Date dateDuration;
    Date dateTime;
    Boolean dateStatus; //completed
    URL inviteLink;

    public DateModel(String id, String password, UserModel dateCreator, UserModel participant1, UserModel participant2, Date dateDuration, Date dateTime, Boolean dateStatus, URL inviteLink) {
        this.id = id;
        this.password = password;
        this.dateCreator = dateCreator;
        this.participant1 = participant1;
        this.participant2 = participant2;
        this.dateDuration = dateDuration;
        this.dateTime = dateTime;
        this.dateStatus = dateStatus;
        this.inviteLink = inviteLink;
    }

    public String setId(String id) {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public UserModel getDateCreator() {
        return dateCreator;
    }

    public UserModel getParticipant1() {
        return participant1;
    }

    public UserModel getParticipant2() {
        return participant2;
    }

    public Date getDateDuration() {
        return dateDuration;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public Boolean getDateStatus() {
        return dateStatus;
    }

    public URL getInviteLink() {
        return inviteLink;
    }
}
