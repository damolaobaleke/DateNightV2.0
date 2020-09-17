package com.immersia_datenight.ui_fragments.casual;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CasualViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CasualViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}