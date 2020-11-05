package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.accounts;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.datenight_immersia_ltd.modelfirestore.User.UserModel;

public class AccountViewModel extends ViewModel {

    private MutableLiveData<UserModel> userModelMutableLiveData;

    public AccountViewModel() {
        userModelMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<UserModel> getUserModelMutableLiveData() {
        return userModelMutableLiveData;
    }
}
