
package com.immersia_ltd_datenight.views.readyplayerweb

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.immersia_ltd_datenight.R

class CreateAvatarInstructionsActivity : AppCompatActivity() {
    private lateinit var continueBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_avatar_instructions)
        val actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.title = "Instructions"

        continueBtn = findViewById(R.id.continueToCreateAvatarButton)
        continueBtn.setOnClickListener { v -> launchCreateAvatarActivity() }
    }

    fun launchCreateAvatarActivity(){
        val intent = Intent(this, CreatAvatarActivity::class.java)
        startActivity(intent)
    }

    @Override
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}