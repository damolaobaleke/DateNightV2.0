package com.immersia_ltd_datenight.views.datehub_navigation.ui_fragments.datehub

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.immersia_ltd_datenight.DatabaseConstants
import com.immersia_ltd_datenight.modelfirestore.User.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class DateHubFragmentViewModel : ViewModel() {
    private var dtcBalance = MutableLiveData<Int>()
    private var kissesRcvd = MutableLiveData<Int>()
    private var dateCount = MutableLiveData<Int>()
    private var dateRating = MutableLiveData<Int>()

    private lateinit var docReference: DocumentReference
    private lateinit var collectionReference: CollectionReference
    var db: FirebaseFirestore
    lateinit var mAuth: FirebaseAuth
    lateinit var currentUser: String

    fun DateNightCoinViewModel() {
        dateRating = MutableLiveData()
        dateCount = MutableLiveData()
        dtcBalance = MutableLiveData()
    }

    init {
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser!!.uid
        docReference = db.collection(DatabaseConstants.USER_DATA_NODE).document(currentUser)
    }


    fun getDtcBalance(): LiveData<Int> {
        dtcBalance.value = 0
        docReference.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(UserModel::class.java)
                dtcBalance.value = user!!.getDtc()
                Log.i(TAG, "dtc: ${user.getDtc()} ")
            } else {
                dtcBalance.value = 0
            }
        }
        return dtcBalance
    }


    fun getDatesBeenOn(): LiveData<Int> {
        dateCount.value = 0

        docReference.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(UserModel::class.java)
                dateCount.value = user!!.getAvgDateStats().dateCount
            } else {
                dateCount.value = 0
                Log.i(TAG, "value is 0 ")
            }
        }
        return dateCount
    }

    fun getAvgDateRating(): LiveData<Int> {

        docReference.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(UserModel::class.java)
                dateRating.value = user!!.getAvgDateStats().rating
                Log.i("Datehub VM", "rating: ${user!!.getAvgDateStats().rating}, dateCount:${user!!.getAvgDateStats().dateCount}")
            } else {
                dateRating.value = 0
                Log.i(TAG, "value is 0 ")
            }
        }
        return dateRating;
    }

    fun getKissesReceived(): LiveData<Int> {
        kissesRcvd.value = 0
        return kissesRcvd
    }

    companion object {
        var TAG = "Datehubviewmodel"
    }

    fun buyDtc(parentFrag: DatehubFragment) {
//        val intent = Intent(requireContext(), DateHubNavigation::class.java)
//        intent.putExtra(IntentConstants.FRAGMENT_TO_LOAD, IntentConstants.BUY_DTC_FRAGMENT)
//        parentFrag.startActivity(intent)


    }


}