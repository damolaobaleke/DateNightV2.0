package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.premium;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PremiumModel extends ViewModel {

    private final MutableLiveData<String> experienceName;
    private final MutableLiveData<String> price;


    public PremiumModel() {
        experienceName = new MutableLiveData<>();
        experienceName.setValue("Love in the clouds");
        price = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return experienceName;
    }

    public MutableLiveData<String> getPrice() {
        return price;
    }
}