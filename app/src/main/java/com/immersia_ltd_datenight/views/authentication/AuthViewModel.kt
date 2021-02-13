package com.immersia_ltd_datenight.views.authentication

import android.app.Application
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.immersia_ltd_datenight.utils.constants.DatabaseConstants
import com.immersia_ltd_datenight.modelfirestore.User.UserModel
import com.immersia_ltd_datenight.modelfirestore.User.UserStatsModel
import com.immersia_ltd_datenight.views.authentication.SignUpActivity.dateStringToTimestamp
import java.util.*
import kotlin.properties.Delegates

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var mAuth: FirebaseAuth
    var progressVisible by Delegates.notNull<Int>()
    var progressInvisible by Delegates.notNull<Int>()
    lateinit var userId: String
    lateinit var userRef: DocumentReference
    lateinit var db: FirebaseFirestore
    lateinit var fcmToken: String

    init {
        mAuth = FirebaseAuth.getInstance()
    }

    fun logIn(email: String, password: String) {

    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun signUp(email: String, password: String, username: String, age: String, name: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult: AuthResult? -> Toast.makeText(getApplication(), "Registered Successfully", Toast.LENGTH_SHORT).show() }
                .addOnFailureListener(getApplication<Application>().applicationContext.mainExecutor, OnFailureListener { e: Exception ->
                    Log.i("Failed", e.localizedMessage)
                    Toast.makeText(getApplication(), e.localizedMessage, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                })
                .addOnCompleteListener(getApplication<Application>().applicationContext.mainExecutor, OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information

                        val avatar = HashMap<String, String>()
                        avatar["avatar"] = ""

                        //Add User to db
                        val userStats = UserStatsModel(0, 0, 0, 0)
                        val user = UserModel("", username, name, email, dateStringToTimestamp(age), avatar, "", DatabaseConstants.LOCAL_AUTH, null, userStats, null, "", Timestamp(mAuth.currentUser!!.metadata!!.creationTimestamp / 1000, 0), false, generateFcmToken())
                        createUserInDb(user)

                    } else {
                        Toast.makeText(getApplication(), "Authentication failed.", Toast.LENGTH_SHORT).show()
                        Log.e("AuthViewModel", "Authentication Failed")
                    }
                })
    }

    fun createUserInDb(user: UserModel) {
        //Firestore instance
        db = FirebaseFirestore.getInstance()

        userId = mAuth.currentUser!!.uid

        userRef = db.collection("users").document(userId)
        userRef.set(user).addOnSuccessListener {
            Toast.makeText(getApplication(), "created successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Toast.makeText(getApplication(), e.message, Toast.LENGTH_SHORT).show()
            Log.e("AuthViewModel", e.localizedMessage)
        }
    }

    fun progressVisible() {
        progressVisible = View.VISIBLE
    }

    fun progressInvisible() {
        progressInvisible = View.INVISIBLE
    }

    private fun generateFcmToken(): String? {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String?> ->
            if (!task.isSuccessful) {
                Log.w("AuthVM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            // Get new FCM registration token
            fcmToken = task.result.toString()

            // Log
            Log.d("AuthVM", fcmToken)
        }
        return fcmToken
    }


}


