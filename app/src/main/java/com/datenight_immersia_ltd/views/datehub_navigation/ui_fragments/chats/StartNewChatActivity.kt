package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.chats

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.datenight_immersia_ltd.R
import de.hdodenhof.circleimageview.CircleImageView


class StartNewChatActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var usernameSearchButton: Button
    private lateinit var clearSearchEditText: ImageView
    private lateinit var foundUserLayout: LinearLayout
    lateinit var foundUserNameTextView: TextView
    lateinit var foundUserPhoto: CircleImageView
    lateinit var noSuchUserTextView: TextView
    private lateinit var viewModel: StartNewChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_new_chat)
        setSupportActionBar(findViewById(R.id.startNewChatToolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = "Invite a user"

        viewModel = ViewModelProvider(this).get(StartNewChatViewModel::class.java)

        // Layout Initializations
        usernameSearchButton = findViewById(R.id.usernameSearchButton)
        usernameEditText = findViewById(R.id.usernameSearchEditText)
        clearSearchEditText = findViewById(R.id.clearSearchEditText)
        foundUserLayout = findViewById(R.id.foundUserLayout)
        foundUserNameTextView = findViewById(R.id.foundUserNameTextView)
        foundUserPhoto = findViewById(R.id.foundUserPhoto)
        noSuchUserTextView = findViewById(R.id.noUserFoundMessage)
        foundUserLayout.isVisible = false
        noSuchUserTextView.isVisible = false
        clearSearchEditText.isVisible = false
        usernameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequnce: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(charSequnce: Editable?) {}

            override fun onTextChanged(charSequnce: CharSequence?, p1: Int, p2: Int, p3: Int) {
                usernameSearchButton.isEnabled = charSequnce.toString().trim().isNotEmpty()
                clearSearchEditText.isVisible = charSequnce.toString().trim().isNotEmpty()
            }
        })
    }

    @Override
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    fun onSearchUsername(v: View){
        val username = usernameEditText.text.toString()
        viewModel.searchForUserByUsername(username, this)
    }

    fun onChat(v: View){
        viewModel.endActivityAndReturnFoundUser(this)
    }

    fun onClearSearchEditText(v: View){
        usernameEditText.setText("")
    }
}
