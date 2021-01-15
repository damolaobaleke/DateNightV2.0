package com.datenight_immersia_ltd.modelfirestore.Date;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class DateModel {
    String id;
    String password; //library for encryption crypto random 8 bit value
    String creator;
    HashMap<String, String> participants;  //id: fullName
    Timestamp dateDuration;  //Date equivalent in java
    String dateStatus; //completed || Ongoing
    HashMap<String, HashMap<String, Integer>> statistics;
    String linkedExperienceId;
    Timestamp timeCompleted;
    Timestamp timeCreated;
    Timestamp dateChosen;
    Timestamp timeChosen;
    Timestamp dateTime; //the time for the date
    HashMap<String, String> participantStatus;
    public HashMap<String, String> participantUsernames;
    String inviteLink;

    public DateModel(String id, String password, String creator, HashMap<String, String> participants, HashMap<String, String> participantUsernames , Timestamp dateDuration, Timestamp timeCreated, Timestamp dateTime, String linkedExperienceId, HashMap<String, String> participantstatus, HashMap<String, HashMap<String, Integer>> dateStats) {
        this.id = id;
        this.password = password;
        this.creator = creator;
        this.participants = participants;
        this.dateDuration = dateDuration;
        this.timeCreated = timeCreated;
        this.dateTime = dateTime;
        this.dateStatus = dateStatus;
        this.inviteLink = inviteLink;
        this.linkedExperienceId = linkedExperienceId;
        this.participantStatus = participantstatus;
        this.participantUsernames = participantUsernames;
        this.statistics = dateStats;
    }

    /**
     * No arg constructor for recreating the doc object from db using the datemodel
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

    public String getCreator() {
        return creator;
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

    public String getLinkedExperienceId() {
        return linkedExperienceId;
    }

    public Map<String, HashMap<String, Integer>> getStatistics() {
        return statistics;
    }

    public HashMap<String, String> getParticipantUsernames() {
        return participantUsernames;
    }
}
