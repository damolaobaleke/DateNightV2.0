package com.ltd_immersia_datenight.modelfirestore.Date;

import com.google.firebase.Timestamp;

import java.util.HashMap;

public class DateModel {
    String id;
    String password; //library for encryption crypto random 8 bit value
    String creator;
    HashMap<String, String> participants;  //id: fullName
    HashMap<String, String> participantStatus;
    HashMap<String, String> participantUsernames;
    Timestamp dateTime;
    String linkedExperienceId;
    Timestamp timeCompleted;
    Timestamp timeCreated;
    HashMap<String, HashMap<String, Integer>> statistics; // TODO: Implement
    String inviteLink;

    public DateModel(String id, String password, String creator, HashMap<String, String> participants, HashMap<String, String> participantUsernames, Timestamp timeCreated, Timestamp dateTime, String linkedExperienceId, HashMap<String, String> participantStatus) {
        this.id = id;
        this.password = password;
        this.creator = creator;
        this.participants = participants;
        this.timeCreated = timeCreated;
        this.dateTime = dateTime;
        this.inviteLink = inviteLink;
        this.linkedExperienceId = linkedExperienceId;
        this.participantStatus = participantStatus;
        this.participantUsernames = participantUsernames;
        this.timeCompleted = null;
        // this.statistics = dateStats;
    }

    /**
     * No arg constructor for recreating the doc object from db using the datemodel
     */
    public DateModel() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public HashMap<String, String> getParticipants() {
        return participants;
    }

    public void setParticipants(HashMap<String, String> participants) {
        this.participants = participants;
    }

    public HashMap<String, String> getParticipantStatus() {
        return participantStatus;
    }

    public void setParticipantStatus(HashMap<String, String> participantStatus) {
        this.participantStatus = participantStatus;
    }

    public HashMap<String, String> getParticipantUsernames() {
        return participantUsernames;
    }

    public void setParticipantUsernames(HashMap<String, String> participantUsernames) {
        this.participantUsernames = participantUsernames;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public String getLinkedExperienceId() {
        return linkedExperienceId;
    }

    public void setLinkedExperienceId(String linkedExperienceId) {
        this.linkedExperienceId = linkedExperienceId;
    }

    public Timestamp getTimeCompleted() {
        return timeCompleted;
    }

    public void setTimeCompleted(Timestamp timeCompleted) {
        this.timeCompleted = timeCompleted;
    }

    public Timestamp getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Timestamp timeCreated) {
        this.timeCreated = timeCreated;
    }

    public HashMap<String, HashMap<String, Integer>> getStatistics() {
        return statistics;
    }

    public void setStatistics(HashMap<String, HashMap<String, Integer>> statistics) {
        this.statistics = statistics;
    }

    public String getInviteLink() {
        return inviteLink;
    }

    public void setInviteLink(String inviteLink) {
        this.inviteLink = inviteLink;
    }
}
