package com.ltd_immersia_datenight.views.user_profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ltd_immersia_datenight.model.User.UserObject;
import com.ltd_immersia_datenight.repository.UserRepository;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final LiveData<UserObject> userObject;
    private final LiveData<List<UserObject>> allUsers;


    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        userObject = userRepository.getUserObject();
        allUsers = userRepository.getUserObjectList();
    }
    
    public void insert(UserObject user){
        userRepository.insert(user);
    }

    public void update(UserObject user){
        userRepository.update(user);
    }

    public void delete(UserObject user){
        userRepository.delete(user);
    }

    public LiveData<UserObject> getUserObject() {
        return userObject;
    }

    public LiveData<List<UserObject>> getAllUsers() {
        return allUsers;
    }
}
