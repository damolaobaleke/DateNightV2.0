package com.immersia_ltd_datenight.modelfirestore.User;

public class UserStatsModel {
    int rating;
    int dateCount;
    int kissCount;
    int numRatedDates;
    int dtc;

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

}
