package com.datenight_immersia_ltd.modelfirestore.User;

public class UserStatsModel {
    int ratings;
    long dateCount;
    long kissCount;

    public UserStatsModel(){
        /**Public no arg constructor needed*/
    }

    public UserStatsModel(int ratings, long dateCount, long kissCount) {
        this.ratings = ratings;
        this.dateCount = dateCount;
        this.kissCount = kissCount;
    }

    public int getRatings() {
        return ratings;
    }

    public long getDateCount() {
        return dateCount;
    }

    public long getKissCount() {
        return kissCount;
    }
}
