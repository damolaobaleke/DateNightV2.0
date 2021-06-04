package com.ltd_immersia_datenight.model.User;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.ltd_immersia_datenight.model.Date.DateObject;
import com.ltd_immersia_datenight.utils.RoomConveters;

import java.net.URL;
import java.util.Date;
import java.util.List;

@Entity(tableName = "users")
public class UserObject {
    @PrimaryKey(autoGenerate = true)
    public int id; //if string wont auto-generate
    @ColumnInfo(name = "username")
    public String username;
    @ColumnInfo(name = "firstname")
    public String firstName;
    @ColumnInfo(name = "lastname")
    public String lastName;
    @ColumnInfo(name = "date_of_birth")
    @TypeConverters(RoomConveters.class)
    public Date dateOfBirth;
    @ColumnInfo(name = "avatar")
    @TypeConverters(RoomConveters.class)
    public URL Avatar;
    @ColumnInfo(name = "status")
    public Boolean status;
    //List<DateObject> dateId; //column--date_id
    @Embedded(prefix = "stats_")
    public UserStatsObject statistics;

    //Empty constructor required, in room
    public UserObject() {
    }

    public UserObject(String username, String firstName, String lastName, Date dateOfBirth, URL avatar, Boolean status, List<DateObject> dateId, UserStatsObject statistics) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.Avatar = avatar;
        this.status = status;
        //this.dateId = dateId;
        this.statistics = statistics;
    }

    public void setId(int id) {
        this.id = id;
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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public URL getAvatar() {
        return Avatar;
    }

    public Boolean getStatus() {
        return status;
    }

//    public List<DateObject> getDateIds() {
//        return dateId;
//    }

    public UserStatsObject getStatistics() {
        return statistics;
    }
}