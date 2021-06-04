package com.ltd_immersia_datenight.model;


import android.graphics.Bitmap;

import androidx.appcompat.app.AppCompatActivity;

public class DateDataModel extends AppCompatActivity {
    //int image; using drawable locally
    String dateTitle;
    Bitmap dateImage;
    String dateDate;
    String dateTime; //Changed from Date

    public DateDataModel(String dateTitle, Bitmap dateImage, String dateDate, String dateTime){
        //this.image = image;
        this.dateTitle = dateTitle;
        this.dateImage = dateImage;
        this.dateDate = dateDate;
        this.dateTime = dateTime;

    }

    public String getDateTitle() {
        return dateTitle;
    }

    public Bitmap getDateImage() {
        return dateImage;
    }

    public String getDateDate() {
        return dateDate;
    }

    public String getDateTime() {
        return dateTime;
    }

//    int getImage() {
//        return image;
//    }


}

