package com.ltd_immersia_datenight.views.datehub_navigation.ui_fragments.accounts

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.ltd_immersia_datenight.R

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var oldPassword: EditText
    private lateinit var newPassword: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var changePasswordBtn: Button
    private lateinit var oldPasswordTextInputLayout: TextInputLayout
    private lateinit var newPasswordTextInputLayout: TextInputLayout
    private lateinit var confirmPasswordTextInputLayout: TextInputLayout
    val TAG = "ChangePasswordActivity"
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
        confirmPassword = findViewById(R.id.confirmPasswordEditText)
        oldPasswordTextInputLayout = findViewById(R.id.oldPasswordTextInputLayout)
        newPasswordTextInputLayout = findViewById(R.id.newPasswordTextInputLayout)
        confirmPasswordTextInputLayout = findViewById(R.id.confirmPasswordTextInputLayout)
        changePasswordBtn = findViewById(R.id.changePasswordButton)
        changePasswordBtn.setOnClickListener {
            changePassword()
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
        var confirmPass = confirmPassword.text.toString()
        val email = user!!.email

        val credential = EmailAuthProvider.getCredential(email!!, oldPass)
        user.reauthenticate(credential)
                .addOnFailureListener{
                    oldPasswordTextInputLayout.error = "Incorrect old password enter"
                    //val toast = Toast.makeText(this, "The old password you entered is incorrect", Toast.LENGTH_SHORT)
                   // toast.show()

                }
                .addOnSuccessListener {
                    val error = validateNewPassword(newPass, confirmPass)
                    if (error.isEmpty()){
                        user.updatePassword(newPass)
                            .addOnCompleteListener { task ->
                                if(task.isSuccessful){
                                    val toast = Toast.makeText(this, "Successfully updated password", Toast.LENGTH_SHORT)
                                    toast.show()
                                    onBackPressed()
                                }
                            }
                    } else {
                        newPasswordTextInputLayout.error = error
                        confirmPasswordTextInputLayout.error = error
                    }
                }


    }

    fun validateNewPassword(newPasswordValue: String, confirmPasswordValue: String): String{
        var error = "";

        val PASSWORD_PATTERN = ".*[a-z]+.*"
        val PASSWORD_PATTERN_2 = ".*[A-Z]+.*"
        val PASSWORD_PATTERN_3 = ".*[0-9]+.*"
        val PASSWORD_PATTERN_4 = ".*[!@#$&*_\\\\-]+.*"
        if (newPasswordValue.length < 8 ||
            !(newPasswordValue.matches(Regex(PASSWORD_PATTERN)) && newPasswordValue.matches(Regex(PASSWORD_PATTERN_2)) &&
                    newPasswordValue.matches(Regex(PASSWORD_PATTERN_3)) && newPasswordValue.matches(Regex(PASSWORD_PATTERN_4)))
        ) {
            Log.e(TAG, newPasswordValue.matches(Regex(PASSWORD_PATTERN)).toString())
            Log.e(TAG, newPasswordValue.matches(Regex(PASSWORD_PATTERN_2)).toString())
            Log.e(TAG, newPasswordValue.matches(Regex(PASSWORD_PATTERN_3)).toString())
            Log.e(TAG, newPasswordValue.matches(Regex(PASSWORD_PATTERN_4)).toString())
            error  =
                "Password must be a minimum of 8 characters and must contain uppercase, lowercase, number and one of !, @, #, $, &, *, _, \\, -"
        } else if (newPasswordValue != confirmPasswordValue){
            error = "Passwords entered do not match"
        }
        return error;
    }
}