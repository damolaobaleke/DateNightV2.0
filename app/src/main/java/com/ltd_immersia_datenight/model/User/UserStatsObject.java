package com.ltd_immersia_datenight.model.User;

import androidx.room.ColumnInfo;

public class UserStatsObject {
    @ColumnInfo(name = "ratings")
    int ratings;
    @ColumnInfo(name = "date_count")
    long dateCount;
    @ColumnInfo(name = "kiss_count")
    long kissCount;

    public UserStatsObject(int ratings, long dateCount, long kissCount) {
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
