package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.accounts;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.datenight_immersia_ltd.modelfirestore.User.UserModel;

public class AccountViewModel extends ViewModel {

    private final MutableLiveData<UserModel> userData;

    public AccountViewModel() {
        userData = new MutableLiveData<>();
    }

    public MutableLiveData<UserModel> getUserLiveData() {
        return userData;
    }
}
