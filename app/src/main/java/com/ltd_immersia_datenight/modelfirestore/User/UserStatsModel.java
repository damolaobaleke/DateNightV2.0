package com.ltd_immersia_datenight.modelfirestore.User;

public class UserStatsModel {
    int rating;
    int dateCount;
    int kissCount;
    int numRatedDates;
    int dtc;
    int ratedDates;

    public UserStatsModel(){
        /**Public no arg constructor needed*/
    }

    public UserStatsModel(int rating, int dateCount, int kissCount, int dtc) {
        this.rating = rating;
        this.dateCount = dateCount;
        this.kissCount = kissCount;
        this.dtc = dtc;
    }

    public int getRating() {
        return rating;
    }

    public int getDateCount() {
        return dateCount;
    }

    public int getKissCount() {
        return kissCount;
    }

    public int getDtc() {
        return dtc;
    }

    public int getNumRatedDates() {
        return numRatedDates;
    }

    public void setNumRatedDates(int numRatedDates) {
        this.numRatedDates = numRatedDates;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setDateCount(int dateCount) {
        this.dateCount = dateCount;
    }

    public void setKissCount(int kissCount) {
        this.kissCount = kissCount;
    }

    public void setDtc(int dtc) {
        this.dtc = dtc;
    }

    public int getRatedDates() {
        return ratedDates;
    }

    public void setRatedDates(int ratedDates) {
        this.ratedDates = ratedDates;
    }
}
