package com.datenight_immersia_ltd.utils;

import androidx.room.TypeConverter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class RoomConveters  {

    //DATE
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    //URL
    @TypeConverter
    public static URL fromurl(String value) {
        try {
            return value == null ? null : new URL(value);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @TypeConverter
    public static String stringToUrl(URL value) {
        return value == null ? null : value.getPath();
    }


}
