package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.datehub

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DateHubFragmentViewModel : ViewModel() {
    private var dtcBalance = MutableLiveData<Int>()
    private var kissesRcvd = MutableLiveData<Int>()
    private var datesBeenOn = MutableLiveData<Int>()


    fun getDtcBalance(): LiveData<Int> {
        dtcBalance.value = 0
        return dtcBalance
    }

    fun getKissesReceived(): LiveData<Int> {
        kissesRcvd.value = 0
        return kissesRcvd
    }

    fun getDatesBeenOn(): LiveData<Int> {
        datesBeenOn.value = 0
        return datesBeenOn
    }


}