package com.ltd_immersia_datenight.views.datehub_navigation.ui_fragments.buy_dtc;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DateNightCoinViewModel extends ViewModel {

    //private to view model
    private final MutableLiveData<Double> dtc100; //doesn't need to be mutable really not dynamic
    private final MutableLiveData<Double> dtc200;
    private final MutableLiveData<Double> dtc500;
    private final MutableLiveData<Double> dtc1000;
    private final MutableLiveData<Double> dtc2000;
    private final MutableLiveData<Double> dtc5000;
    private final MutableLiveData<Double> dtc10000;
    private final MutableLiveData<Double> dtc20000;


    public DateNightCoinViewModel() {
        dtc100 = new MutableLiveData<Double>();
        dtc100.setValue(0.99);

        dtc200 = new MutableLiveData<Double>();
        dtc200.setValue(1.99);

        dtc500 = new MutableLiveData<Double>();
        dtc500.setValue(3.99);

        dtc1000 = new MutableLiveData<Double>();
        dtc1000.setValue(5.99);

        dtc2000 = new MutableLiveData<Double>();
        dtc2000.setValue(7.99);

        dtc5000 = new MutableLiveData<Double>();
        dtc5000.setValue(9.99);

        dtc10000 = new MutableLiveData<Double>();
        dtc10000.setValue(14.99);

        dtc20000 = new MutableLiveData<Double>();
        dtc20000.setValue(19.99);

    }

    //public to be used outside the view model
    public LiveData<Double> getdtc100() {
       return dtc100;
    }

    public LiveData<Double> getdtc200() {
        return dtc200;
    }

    public MutableLiveData<Double> getDtc500() {
        return dtc500;
    }

    public MutableLiveData<Double> getDtc1000() {
        return dtc1000;
    }

    public MutableLiveData<Double> getDtc2000() {
        return dtc2000;
    }

    public MutableLiveData<Double> getDtc5000() {
        return dtc5000;
    }

    public MutableLiveData<Double> getDtc10000() {
        return dtc10000;
    }

    public MutableLiveData<Double> getDtc20000() {
        return dtc20000;
    }
}