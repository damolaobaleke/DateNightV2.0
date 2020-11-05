package com.datenight_immersia_ltd.model.User;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDAO {

    @Insert()
    void insert(UserObject user);

    @Update()
    void update(UserObject user);

    @Delete()
    void  delete(UserObject user);

    @Query("SELECT * FROM users WHERE id=:userId")
    LiveData<UserObject> getUser(Integer userId); //LiveData -Observer for the UserObject, as soon as any changes in the table, value updated, activity notified

    @Query("SELECT * FROM users ORDER BY username DESC")
    LiveData<List<UserObject>> getAllUsers();
}
