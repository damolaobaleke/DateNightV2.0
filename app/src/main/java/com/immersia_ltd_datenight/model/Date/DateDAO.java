package com.immersia_ltd_datenight.model.Date;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

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
