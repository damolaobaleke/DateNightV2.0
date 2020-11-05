package com.datenight_immersia_ltd.model.Date;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.datenight_immersia_ltd.model.User.UserObject;

import java.util.List;

@Dao
public interface DateDAO {

    @Insert()
    void createDate(DateObject date);

    @Query("SELECT * FROM date_table WHERE creator_id")
    List<DateObject> getAllDates();

    @Delete()
    void  deleteDate(DateObject date);
}
