package com.immersia_datenight.ui_fragments.premium;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PremiumModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PremiumModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}