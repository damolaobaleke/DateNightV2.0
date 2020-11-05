package com.datenight_immersia_ltd.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.datenight_immersia_ltd.model.Date.DateDAO;
import com.datenight_immersia_ltd.model.Date.DateObject;
import com.datenight_immersia_ltd.model.DateExperience.DateEDAO;
import com.datenight_immersia_ltd.model.DateExperience.DateExperienceObject;
import com.datenight_immersia_ltd.model.User.UserDAO;
import com.datenight_immersia_ltd.model.User.UserObject;
import com.datenight_immersia_ltd.utils.RoomConveters;

import java.util.Date;

@Database(entities = {UserObject.class, DateObject.class, DateExperienceObject.class}, version = 1)//all collections in the database...Chats included here
@TypeConverters({RoomConveters.class})
public abstract class DateNightDatabase extends RoomDatabase {

    public static String TAG = "DateNight_db";

    private static DateNightDatabase instance;

    public abstract UserDAO userDAO();

    public abstract DateDAO dateDAO();

    public abstract DateEDAO dateExperienceDAO();

    //synchronized- only one thread at a time can access the method, prevent two instances of database being created on error
    public static synchronized DateNightDatabase getInstance(Context context) {
        if (instance != null) {
            Log.i(TAG, "Already an instance");
        } else {
            //create an instance
            instance = Room.databaseBuilder(context.getApplicationContext(), DateNightDatabase.class, "datenight_database.db")
                    .fallbackToDestructiveMigration() // tell room how to migrate to new schema -- delete db and start from scratch, maintain db versioning
                    .addCallback(roomCallBack) // on instance created execute populateDB task
                    .build();
        }
        return instance;
    }

    //Add random data on database created-- FOR TEST
    private static RoomDatabase.Callback roomCallBack = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new populateDBAsyncTask(instance).execute();
        }
    };

    private static class populateDBAsyncTask extends AsyncTask<Void, Void, Void> {
        private UserDAO userDAO;

        private populateDBAsyncTask(DateNightDatabase db) {
            userDAO = db.userDAO();
        }

        @Override
        protected Void doInBackground(Void... Void) {
            Bitmap bitmap;

            userDAO.insert(new UserObject("Rolly", "Damola","Obaleke",new Date(),null,null,null,null));
            return null;
        }
    }

}
