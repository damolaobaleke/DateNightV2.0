package com.ltd_immersia_datenight.views.datehub_navigation.ui_fragments.accounts

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.ltd_immersia_datenight.R
import java.util.regex.Pattern

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var oldPassword: EditText
    private lateinit var newPassword: EditText
    private lateinit var changePasswordBtn: Button
    private lateinit var errorMsg: TextView
    val user = FirebaseAuth.getInstance().currentUser
    val email = user!!.email

    private var isError:Boolean = false
    var errMessageText = "Password must be a minimum of 8 characters and must contain uppercase, lowercase, number and one of !, @, #, \$, &, *, _"

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
        errorMsg = findViewById(R.id.errorMsg)

        oldPass = oldPassword.text.toString()
        newPass = newPassword.text.toString()

        livePasswordChecker()

        changePasswordBtn.setOnClickListener {
            changePassword()
        }
    }

    @Override
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun changePassword(){
        if(TextUtils.isEmpty(newPass) || !isValid(newPass)) {
            isError = true
            newPassword.error = errMessageText
            showError()
        }else{
            isError = false;
            updatePassword()
        }

    }

    private fun showError(){
        if(isError){
            errorMsg.text = newPassword.error
            errorMsg.visibility =  VISIBLE
        }else{
            errorMsg.text = ""
            errorMsg.visibility =  GONE
        }
    }

    private fun isValid(passwordInput: String): Boolean{
        val passwordPattern: Pattern = Pattern.compile("[a-zA-Z0-9!@#$&*_]{8,20}") //minimum 8 characters, max 20, special characters
        return passwordPattern.matcher(passwordInput).matches();
    }

    private fun livePasswordChecker(){
        newPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
               if(isValid(s.trim().toString())){
                   errorMsg.visibility =  GONE
               }else{
                   errorMsg.text = errMessageText
                   errorMsg.visibility =  VISIBLE
               }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun updatePassword(){
        val credential = EmailAuthProvider.getCredential(email!!, oldPass)
        user!!.reauthenticate(credential)
            .addOnFailureListener {
                Toast.makeText(this, "The old password you entered is incorrect", Toast.LENGTH_SHORT).show()

            }
            .addOnSuccessListener {
                user.updatePassword(newPass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Successfully updated password", Toast.LENGTH_SHORT).show()
                            onBackPressed()
                        }
                    }
            }
    }

    companion object {
        private var oldPass : String = ""
        private var newPass : String = ""
    }

}