package com.immersia_ltd_datenight.model.DateExperience;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.immersia_ltd_datenight.utils.RoomConveters;

import java.net.URL;

@Entity(tableName = "date_experience_table")
public class DateExperienceObject {
    @PrimaryKey(autoGenerate = true)
    int id;
    String experienceName;
    double price;
    String experienceDescription;
    @TypeConverters(RoomConveters.class)
    URL experienceImage;
    @TypeConverters(RoomConveters.class)
    URL unityEnvironment;//Environment/Unity app gotten from server, EnvironmentAPI

    public DateExperienceObject(String experienceName, double price, String experienceDescription, URL experienceImage, URL unityEnvironment) {
        this.experienceName = experienceName;
        this.price = price;
        this.experienceDescription = experienceDescription;
        this.experienceImage = experienceImage;
        this.unityEnvironment = unityEnvironment;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExperienceName() {
        return experienceName;
    }

    public double getPrice() {
        return price;
    }

    public String getExperienceDescription() {
        return experienceDescription;
    }

    public URL getExperienceImage() {
        return experienceImage;
    }

    public URL getUnityEnvironment() {
        return unityEnvironment;
    }
}
