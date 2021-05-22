package com.immersia_ltd_datenight.views.datehub_navigation.ui_fragments.datehub

import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.immersia_ltd_datenight.modelfirestore.User.UserModel
import com.immersia_ltd_datenight.utils.constants.DatabaseConstants

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
    private lateinit var userModel : UserModel

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

    fun getKissesReceived(): LiveData<Int> { // TODO: Update to actual kisses received and user ratings
        docReference.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(UserModel::class.java)
                kissesRcvd.value = user!!.getAvgDateStats().kissCount
                Log.i("Datehub VM", "kissCount: ${user!!.getAvgDateStats().kissCount}, dateCount:${user!!.getAvgDateStats().dateCount}")
            } else {
                kissesRcvd.value = 0
                Log.i(TAG, "value is 0 ")
            }
        }
        return kissesRcvd
    }

    companion object {
        var TAG = "Datehubviewmodel"
    }

    fun initializeViews(dtcBalance: TextView, datesCount: TextView, avgRating: TextView, ratingProgBar: ProgressBar, kissCount: TextView){
        docReference.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                userModel = documentSnapshot.toObject(UserModel::class.java)!!
                datesCount.setText(userModel.getAvgDateStats().dateCount.toString())
                avgRating.setText(userModel.getAvgDateStats().rating.toString())
                ratingProgBar.progress = userModel.getAvgDateStats().rating * 100 / 5
                dtcBalance.setText(userModel.getDtc().toString())
                kissCount.setText(userModel.getAvgDateStats().kissCount.toString())

            } else {
                Log.i(TAG, "Unable to get user model")
            }
        }
    }

    fun buyDtc(parentFrag: DatehubFragment) {
//        val intent = Intent(requireContext(), DateHubNavigation::class.java)
//        intent.putExtra(IntentConstants.FRAGMENT_TO_LOAD, IntentConstants.BUY_DTC_FRAGMENT)
//        parentFrag.startActivity(intent)
    }
}