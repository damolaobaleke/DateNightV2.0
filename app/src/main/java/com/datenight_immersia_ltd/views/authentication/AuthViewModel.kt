package com.datenight_immersia_ltd.views.authentication

import android.app.Application
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import com.datenight_immersia_ltd.modelfirestore.User.UserModel
import com.datenight_immersia_ltd.modelfirestore.User.UserStatsModel
import com.datenight_immersia_ltd.views.authentication.SignUpActivity.dateStringToTimestamp
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.properties.Delegates

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var mAuth: FirebaseAuth
    var progressVisible by Delegates.notNull<Int>()
    var progressInvisible by Delegates.notNull<Int>()
    lateinit var userId: String
    lateinit var userRef: DocumentReference
    lateinit var db: FirebaseFirestore

    fun logIn(email: String, password: String) {

    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun signUp(email: String, password: String, username: String, age: String) {
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
                        avatar["avatarHead"] = ""
                        avatar["avatarFullBody"] = ""

                        //Add User to db
                        val userStats = UserStatsModel(0, 0, 0)
                        val user = UserModel(username, null, email, dateStringToTimestamp(age), avatar, "BASIC", null, userStats, "", username.toLowerCase(Locale.ROOT))

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


}


