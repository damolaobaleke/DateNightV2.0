package com.ltd_immersia_datenight.views.datehub_navigation.ui_fragments.datehub

import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.ltd_immersia_datenight.modelfirestore.User.UserModel
import com.ltd_immersia_datenight.modelfirestore.avatar.RenderObject
import com.ltd_immersia_datenight.network.api.DatenightApi
import com.ltd_immersia_datenight.utils.constants.DatabaseConstants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class DateHubFragmentViewModel : ViewModel() {
    private var dtcBalance = MutableLiveData<Int>()
    private var kissesRcvd = MutableLiveData<Int>()
    private var dateCount = MutableLiveData<Int>()
    private var dateRating = MutableLiveData<Int>()
    private var userAvatar = MutableLiveData<String>()
    private var isAvatarUrl = MutableLiveData<Boolean>()
    private lateinit var api: DatenightApi

    private lateinit var docReference: DocumentReference
    private lateinit var collectionReference: CollectionReference
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var currentUser: String = mAuth.currentUser!!.uid
    private lateinit var userModel: UserModel


    fun DateNightCoinViewModel() {
        dateRating = MutableLiveData()
        dateCount = MutableLiveData()
        dtcBalance = MutableLiveData()
        userAvatar = MutableLiveData()
        isAvatarUrl = MutableLiveData()
    }

    init {
        docReference = db.collection(DatabaseConstants.USER_DATA_NODE).document(currentUser)
    }

    fun getDtcBalance(): LiveData<Int> {
        dtcBalance.value = 0
        docReference.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(UserModel::class.java)
                dtcBalance.value = user!!.dtc
                Log.i(TAG, "dtc: ${user.dtc} ")
            } else {
                dtcBalance.value = 0
            }
        }
        return dtcBalance
    }


    fun initializeViews(dtcBalance: TextView, datesCount: TextView, avgRating: TextView, ratingProgBar: ProgressBar, kissCount: TextView) {
        docReference.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                try{
                    userModel = documentSnapshot.toObject(UserModel::class.java)!!
                    datesCount.text = userModel.avgDateStats.dateCount.toString()
                    avgRating.text = userModel.avgDateStats.rating.toString()
                    ratingProgBar.progress = userModel.avgDateStats.rating * 100 / 5
                    dtcBalance.text = userModel.dtc.toString()
                    kissCount.text = userModel.avgDateStats.kissCount.toString()
                    userAvatar.value = userModel.avatar[DatabaseConstants.AVATAR_HEADSHOT_URL_FIELD]
                } catch(e: Exception){
                    Log.e(TAG, "Error while trying to cast response to UserModel")
                    Log.e(TAG, e.message)
                    e.printStackTrace()
                }
            } else {
                userAvatar.value = ""
                Log.i(TAG, "Unable to get user model")
            }
        }
    }

    fun get2dAvatar(): LiveData<String> {
        docReference.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                try{
                    val user = documentSnapshot.toObject(UserModel::class.java)
                    if (user!!.avatar[DatabaseConstants.AVATAR_HEADSHOT_URL_FIELD] != null) {
                        userAvatar.value = user.avatar[DatabaseConstants.AVATAR_HEADSHOT_URL_FIELD]
                    } else {
                        userAvatar.value = ""
                    }
                } catch (e: Exception){

                }
            }
        }
        return userAvatar
    }

    fun checkUserAvatarExists(): LiveData<Boolean> {
        docReference.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                try{
                    val user = documentSnapshot.toObject(UserModel::class.java)
                    if(user!!.avatar[DatabaseConstants.AVATAR_URL_FIELD] != null){
                        isAvatarUrl.value = true
                        if(user.avatar[DatabaseConstants.AVATAR_HEADSHOT_URL_FIELD] == null) {
                            setAvatarHeadShot()
                        }
                    } else {
                        isAvatarUrl.value = false
                    }
                } catch (e:Exception){
                    Log.e(TAG, "Error while trying to cast object UserModel")
                    Log.e(TAG, e.message)
                    e.printStackTrace()
                }
            }
        }
        return isAvatarUrl
    }


    private fun setUpNetworkRequest() {
        //Logging (Http)REQUEST and RESPONSE
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient().newBuilder()
                .addInterceptor(loggingInterceptor)
                .build()
        //Logging Request and Response

        val retrofit = Retrofit.Builder()
                .baseUrl("https://render.readyplayer.me")
                .addConverterFactory(GsonConverterFactory.create()) //GSON convert java object to JSON
                .client(okHttpClient)
                .build()
        api = retrofit.create(DatenightApi::class.java)
    }


    private fun setAvatarHeadShot() {
        setUpNetworkRequest()

        docReference.get().addOnSuccessListener { documentSnapshot ->
            if(documentSnapshot.exists()){
                val user = documentSnapshot.toObject(UserModel::class.java)
                val params = HashMap<String, String?>()

                params["scene"] = "fullbody-portrait-v1"
                params["armature"] = "ArmatureTargetMale"
                params["model"] = user!!.avatar[DatabaseConstants.AVATAR_URL_FIELD]


                val render = api.getAvatarUrl(params)

                render.enqueue(object : Callback<RenderObject?> {
                    override fun onResponse(call: Call<RenderObject?>, response: Response<RenderObject?>) {
                        if (!response.isSuccessful) {
                            Log.i(TAG, response.message())
                            return
                        }

                        val objRender = response.body()
                        Log.i("2D Link", objRender!!.render[0].toString() + "")

                        val avatar = HashMap<String, Any>()
                        avatar[DatabaseConstants.AVATAR_HEADSHOT_URL_FIELD] = objRender.render[0]

                        //update avatar map with new K,V
                        docReference.update(mapOf("avatar.avatarHeadShotUrl" to objRender.render[0]))
                    }

                    override fun onFailure(call: Call<RenderObject?>, t: Throwable) {
                        Log.i("Error", t.message)
                    }
                })
            }
        }
    }

    companion object {
        var TAG = "DateHubFragmentViewModel"
    }

    fun buyDtc(parentFrag: DatehubFragment) {
        /*
        val intent = Intent(requireContext(), DateHubNavigation::class.java)
        intent.putExtra(IntentConstants.FRAGMENT_TO_LOAD, IntentConstants.BUY_DTC_FRAGMENT)
        parentFrag.startActivity(intent)
         */
    }
}