package com.datenight_immersia_ltd.network.api;

import java.util.Date;

public class User {
    String _id;
    String email;
    String password;
    String username;
    String[] gender;
    String[] country;
    String[] pitchesInvestedIn;
    String[] datesOfInvestments;
    int[] investments;
    boolean emailMarketing;
    boolean isInvested;
    Date dateJoined;
    Date dateOfBirth;
    int networth;

    public User(String _id, String email, String username, String password, String[] gender, String[] country, String[] pitchesInvestedIn, String[] datesOfInvestments, int[] investments, boolean emailMarketing, boolean isInvested, Date dateJoined, Date dateOfBirth, int networth) {
        this._id = _id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.gender = gender;
        this.country = country;
        this.pitchesInvestedIn = pitchesInvestedIn;
        this.datesOfInvestments = datesOfInvestments;
        this.investments = investments;
        this.emailMarketing = emailMarketing;
        this.isInvested = isInvested;
        this.dateJoined = dateJoined;
        this.dateOfBirth = dateOfBirth;
        this.networth = networth;
    }

    public String get_id() {
        return _id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }


    public String getUsername() {
        return username;
    }

    public String[] getGender() {
        return gender;
    }

    public String[] getCountry() {
        return country;
    }

    public String[] getPitchesInvestedIn() {
        return pitchesInvestedIn;
    }

    public String[] getDatesOfInvestments() {
        return datesOfInvestments;
    }

    public int[] getInvestments() {
        return investments;
    }

    public boolean isEmailMarketing() {
        return emailMarketing;
    }

    public boolean isInvested() {
        return isInvested;
    }

    public Date getDateJoined() {
        return dateJoined;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public int getNetworth() {
        return networth;
    }
}
