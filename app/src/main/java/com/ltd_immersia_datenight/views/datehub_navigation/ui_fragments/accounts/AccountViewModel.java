package com.ltd_immersia_datenight.views.datehub_navigation.ui_fragments.accounts;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ltd_immersia_datenight.modelfirestore.User.UserModel;

public class AccountViewModel extends ViewModel {

    private final MutableLiveData<UserModel> userData;

    public AccountViewModel() {
        userData = new MutableLiveData<>();
    }

    public MutableLiveData<UserModel> getUserLiveData() {
        return userData;
    }
}
