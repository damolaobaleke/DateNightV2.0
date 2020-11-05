package com.datenight_immersia_ltd.model.Date;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.datenight_immersia_ltd.model.User.UserObject;
import com.datenight_immersia_ltd.utils.RoomConveters;

import java.net.URL;
import java.sql.Time;
import java.util.Date;

//foreignKeys = @ForeignKey(entity = UserObject.class, parentColumns ="dateId", childColumns = "[date_id]",onDelete = ForeignKey.CASCADE)
@Entity(tableName = "date_table")
public class DateObject {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "date_id")
    int id;
    String password; //library for encryption crypto random 8 bit value
    //List<UserObject> participants -- update
    @Embedded(prefix = "creator_")
    UserObject dateCreator;
    @Embedded(prefix = "participant_one_")
    UserObject participant1; //reference to userEntity or Model, association
    @Embedded(prefix = "participant_two_")
    UserObject participant2;
    @TypeConverters(RoomConveters.class)
    Date dateDuration;
    @TypeConverters(RoomConveters.class)
    Date dateTime;
    Boolean dateStatus; //completed
    @TypeConverters(RoomConveters.class)
    URL inviteLink;

    public DateObject(){}


    public DateObject(String password, UserObject dateCreator, UserObject participant1, UserObject participant2, Date duration, Date date, Boolean dateStatus, URL inviteLink) {
        this.password = password;
        this.dateCreator = dateCreator;
        this.participant1 = participant1;
        this.participant2 = participant2;
        this.dateDuration = duration;
        this.dateTime = date;
        this.dateStatus = dateStatus;
        this.inviteLink = inviteLink;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public UserObject getDateCreator() {
        return dateCreator;
    }

    public UserObject getParticipant1() {
        return participant1;
    }

    public UserObject getParticipant2() {
        return participant2;
    }

    public Date getDuration() {
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
