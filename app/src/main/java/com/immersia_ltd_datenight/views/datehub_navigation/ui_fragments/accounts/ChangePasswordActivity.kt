package com.immersia_ltd_datenight.views.datehub_navigation.ui_fragments.accounts

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.immersia_ltd_datenight.R

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var oldPassword: EditText
    private lateinit var newPassword: EditText
    private lateinit var changePasswordBtn: Button
    val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        val actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.title = "Change Password"

        oldPassword = findViewById(R.id.oldPasswordEditText)
        newPassword = findViewById(R.id.newPasswordEditText)
        changePasswordBtn = findViewById(R.id.changePasswordButton)
        changePasswordBtn.setOnClickListener {
            changePassword();
        }
    }

    @Override
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    fun changePassword(){
        var oldPass = oldPassword.text.toString()
        var newPass = newPassword.text.toString()
        var email = user!!.email

        val credential = EmailAuthProvider.getCredential(email!!, oldPass)
        user.reauthenticate(credential)
                .addOnFailureListener{
                    val toast = Toast.makeText(this, "The old password you entered is incorrect", Toast.LENGTH_SHORT)
                    toast.show();

                }
                .addOnSuccessListener {
                    user.updatePassword(newPass)
                            .addOnCompleteListener { task ->
                                if(task.isSuccessful){
                                    val toast = Toast.makeText(this, "Successfully updated password", Toast.LENGTH_SHORT)
                                    toast.show();
                                    onBackPressed()
                                }
                            }
                }


    }
}