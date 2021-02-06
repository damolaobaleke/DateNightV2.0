package com.immersia_ltd_datenight.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.immersia_ltd_datenight.model.DateNightDatabase;
import com.immersia_ltd_datenight.model.User.UserDAO;
import com.immersia_ltd_datenight.model.User.UserObject;


import java.util.List;

//Abstraction Layer
public class UserRepository {
    private final UserDAO userDAO;
    private final LiveData<List<UserObject>> userObjectList;
    private final LiveData<UserObject> userObject;

    public UserRepository(Application app) {
        DateNightDatabase database = DateNightDatabase.getInstance(app);
        userDAO = database.userDAO();
        userObjectList = userDAO.getAllUsers();
        userObject = userDAO.getUser(null);
    }

    public void insert(UserObject user) {
        new createUserAsyncTask(userDAO).execute(user);
    }

    public void update(UserObject user) {
        new updateUserAsyncTask(userDAO).execute(user);
    }

    public void delete(UserObject user) {
        new deleteUserAsyncTask(userDAO).execute(user);
    }

    public LiveData<UserObject> getUserObject() {
        return userObject;
    }

    public LiveData<List<UserObject>> getUserObjectList() {
        return userObjectList;
    }

    //static - so its independent, has no ref to repository class
    private static class createUserAsyncTask extends AsyncTask<UserObject, Void, Void> {
        private final UserDAO userDAO;

        private createUserAsyncTask(UserDAO userDAO) {
            this.userDAO = userDAO;
        }

        @Override
        protected Void doInBackground(UserObject... userObjects) {
            userDAO.insert((userObjects[0]));
            return null;
        }
    }

    private static class updateUserAsyncTask extends AsyncTask<UserObject, Void, Void> {
        private final UserDAO userDAO;

        private updateUserAsyncTask(UserDAO userDAO) {
            this.userDAO = userDAO;
        }

        @Override
        protected Void doInBackground(UserObject... userObjects) {
            userDAO.update((userObjects[0]));
            return null;
        }
    }

    private static class deleteUserAsyncTask extends AsyncTask<UserObject, Void, Void> {
        private final UserDAO userDAO;

        private deleteUserAsyncTask(UserDAO userDAO) {
            this.userDAO = userDAO;
        }

        @Override
        protected Void doInBackground(UserObject... userObjects) {
            userDAO.delete((userObjects[0]));
            return null;
        }
    }
}
