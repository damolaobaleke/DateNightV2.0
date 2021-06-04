package com.ltd_immersia_datenight.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Datenight.db";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME,null, 1);
        SQLiteDatabase db = this.getWritableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Users(id INTEGER PRIMARY KEY, username VARCHAR ,password VARCHAR, email VARCHAR, DOB INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DELETE FROM Users");
        onCreate(db);
    }

    public boolean signUp(String username, String password, String email, int DOB) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("email", email);
        cv.put("Date of Birth", DOB);
        long result = db.insert("Users", null, cv);
        if (result == -1) {
            Log.e("Database", "Didn't store");
            return false;
        } else {
            Log.i("Database", "Stored");
            return true;
        }

    }

    public Cursor getData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Users",null);
        return cursor;
    }

    public Boolean auth(String username, String password){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Users where username = ? AND password = ?",new String[]{username,password});
        //checks for username and password in the cursor, if its > 0
        if(cursor.getCount() > 0){
            //Wont work
            Log.i("Auth", "already in database");
            return false;
        }else{
            return true;
        }
    }

    public Integer DeleteData(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("Users", "username = ?",new String[]{username});
    }

}
