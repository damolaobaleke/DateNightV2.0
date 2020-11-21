package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.chats

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.datenight_immersia_ltd.R

class StartNewChatActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var usernameSearchButton: Button
    private lateinit var viewModel: StartNewChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_new_chat)
        setSupportActionBar(findViewById(R.id.startNewChatToolbar))
        title = "Chats"

        viewModel = ViewModelProvider(this).get(StartNewChatViewModel::class.java)

        // Layout Initializations
        usernameSearchButton = findViewById(R.id.usernameSearchButton)
        usernameEditText = findViewById(R.id.usernameSearchEditText)
        usernameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequnce: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(charSequnce: Editable?) {}

            override fun onTextChanged(charSequnce: CharSequence?, p1: Int, p2: Int, p3: Int) {
                usernameSearchButton.isEnabled = charSequnce.toString().trim().isNotEmpty()
            }
        })
    }

    fun onSearch(v: View){
        val username = usernameEditText.text.toString()
        viewModel.searchForUserByUsername(username, this)
    }
}
