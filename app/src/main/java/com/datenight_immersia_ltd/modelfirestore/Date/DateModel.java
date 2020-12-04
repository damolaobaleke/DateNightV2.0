package com.datenight_immersia_ltd.modelfirestore.Date;

import com.datenight_immersia_ltd.modelfirestore.User.UserModel;
import com.google.firebase.Timestamp;

import java.net.URL;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateModel {
    String id;
    String password; //library for encryption crypto random 8 bit value
    //List<UserModel> participants -- update
    String dateCreator;
    String dateInvitee;
    String dateInviteeId;
    HashMap<String, String> participants;  //id: fullName
    Timestamp dateDuration;  //Date equivalent in java
    String dateStatus; //completed || Ongoing
    HashMap<String, HashMap<String, Integer>> statistics;
    String linkedexperienceId;
    Timestamp timeCompleted;
    Timestamp timeCreated;
    Timestamp dateChosen;
    Timestamp timeChosen;
    Timestamp dateTime; //the time for the date
    HashMap<String, String> participantStatus;
    String inviteLink;

    public DateModel(String id, String password, String dateCreator, String dateinvitee, HashMap<String, String> participants, Timestamp dateDuration, Timestamp timeCreated, Timestamp dateTime, String dateStatus, String inviteLink, String linkedexperienceId, HashMap<String, String> participantstatus, HashMap<String, HashMap<String, Integer>> dateStats, String dateInviteeId) {
        this.id = id;
        this.password = password;
        this.dateCreator = dateCreator;
        this.dateInvitee = dateinvitee;
        this.participants = participants;
        this.dateDuration = dateDuration;
        this.timeCreated = timeCreated;
        this.dateTime = dateTime;
        this.dateStatus = dateStatus;
        this.inviteLink = inviteLink;
        this.linkedexperienceId = linkedexperienceId;
        this.participantStatus = participantstatus;
        this.dateInviteeId = dateInviteeId;
        this.statistics = dateStats;
    }

    /**
     * No arg constructor
     */
    public DateModel() {

    }

    public String getId() {
        return id;
    }

    public String setId(String id) {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getDateCreator() {
        return dateCreator;
    }

    public String getDateInvitee() {
        return dateInvitee;
    }

    public HashMap<String, String> getParticipants() {
        return participants;
    }

    public Timestamp getDateDuration() {
        return dateDuration;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public String getDateStatus() {
        return dateStatus;
    }

    public Timestamp getTimeCreated() {
        return timeCreated;
    }


    public String getInviteLink() {
        return inviteLink;
    }

    public HashMap<String, String> getParticipantStatus() {
        return participantStatus;
    }

    public String getLinkedexperienceId() {
        return linkedexperienceId;
    }

    public Map<String, HashMap<String, Integer>> getStatistics() {
        return statistics;
    }

    public String getDateInviteeId() {
        return dateInviteeId;
    }
}
