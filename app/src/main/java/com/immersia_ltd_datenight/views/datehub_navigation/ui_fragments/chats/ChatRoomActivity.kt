package com.immersia_ltd_datenight.views.datehub_navigation.ui_fragments.chats

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.immersia_ltd_datenight.utils.constants.IntentConstants
import com.immersia_ltd_datenight.R

class ChatRoomActivity : AppCompatActivity() {
    private lateinit var messageEditText: EditText
    private lateinit var sendMessageButton: ImageButton
    private lateinit var chatRoomRecyclerView: RecyclerView
    private lateinit var chatRoomLayoutManager: LinearLayoutManager
    private lateinit var viewModel: ChatRoomViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        setSupportActionBar(findViewById(R.id.chatRoomToolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Initialize chat participants' data
        val currentUsername = intent.getStringExtra(IntentConstants.USER_NAME_EXTRA)
        val currentUserFullName = intent.getStringExtra(IntentConstants.USER_FULL_NAME_EXTRA)
        val chatParticipantId = intent.getStringExtra(IntentConstants.PARTICIPANT_ID_EXTRA)
        val chatParticipantUsername = intent.getStringExtra(IntentConstants.PARTICIPANT_USER_NAME_EXTRA) // TODO: Change to username
        val chatParticipantFullName = intent.getStringExtra(IntentConstants.PARTICIPANT_FULL_NAME_EXTRA)
        val chatRoomID = intent.getStringExtra(IntentConstants.CHAT_ROOM_ID_EXTRA)

        if (currentUsername == null || chatParticipantId == null ||
                chatParticipantUsername == null || chatRoomID == null){
            //TODO
        }
        else {

        }
        viewModel = ViewModelProvider(this, ChatRoomViewModel.ChatRoomViewModelFactory(currentUsername!!, currentUserFullName!!, chatParticipantId!!,
                        chatParticipantUsername!!,
                        chatParticipantFullName!!,
                        chatRoomID!!,
                        this))
                .get(ChatRoomViewModel::class.java)

        chatRoomLayoutManager = LinearLayoutManager(this)
        chatRoomLayoutManager.stackFromEnd = true
        title = chatParticipantUsername
        sendMessageButton = findViewById(R.id.sendMessageButton)
        messageEditText = findViewById(R.id.chatRoomMessageEditText)
        chatRoomRecyclerView = findViewById<RecyclerView>(R.id.chatRoomRecyclerView).apply{
            layoutManager = chatRoomLayoutManager
            adapter = viewModel.messagesFirebaseRecyclerAdapter
        }
    }

    fun onSend(v: View){
        val messageText = messageEditText.text.toString()
        if (messageText.isNotEmpty()){
            viewModel.sendMessage(messageText)
            messageEditText.setText("")
        }
    }

    @Override
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}