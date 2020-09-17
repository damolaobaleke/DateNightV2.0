package com.immersia_datenight.ui_fragments.buy_dtc;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DateNightCoinModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DateNightCoinModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}