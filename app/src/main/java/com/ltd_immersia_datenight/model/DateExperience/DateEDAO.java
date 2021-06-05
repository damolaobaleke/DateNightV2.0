package com.ltd_immersia_datenight.model.DateExperience;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;


@Dao
public interface DateEDAO {

    @Insert()
    void createDate(DateExperienceObject date); //Really for us instead of creating each time

    @Query("SELECT * FROM date_experience_table")
    LiveData<List<DateExperienceObject>> getAllDateExperiences();

}
