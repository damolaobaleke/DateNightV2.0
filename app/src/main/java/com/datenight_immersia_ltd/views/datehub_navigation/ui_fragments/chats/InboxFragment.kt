package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.chats

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.datenight_immersia_ltd.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class InboxFragment : Fragment() {
    // User Data
    private var currentUserId: String? = null
    private var currentUserName: String? = null
    private var currentUserFullName: String? = null

    // Layout related variables
    private lateinit var chatHeadsRecyclerView: RecyclerView
    private lateinit var chatHeadsLayoutManager: RecyclerView.LayoutManager
    private lateinit var newChatFloatingButton: FloatingActionButton;
    lateinit var emptyInboxImage: ImageView
    lateinit var emptyInboxText: TextView

    //View model
    private lateinit var viewModel: InboxViewModel
    private lateinit var inboxFragmentView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this,
                InboxViewModel.InboxViewModelFactory( "tempUsername", "tempName" ))
                .get(InboxViewModel::class.java)
        viewModel.initializeFirebaseRecyclerView(this)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        inboxFragmentView =  inflater.inflate(R.layout.fragment_inbox, container, false)
        emptyInboxImage = inboxFragmentView.findViewById(R.id.emptyInboxImageView)
        emptyInboxText = inboxFragmentView.findViewById(R.id.emptyInboxTextView)
        newChatFloatingButton = inboxFragmentView.findViewById<FloatingActionButton>(R.id.newChatButton).apply {
            setOnClickListener { startActivityToFindUser() }
        }

        chatHeadsLayoutManager = LinearLayoutManager(context)
        chatHeadsRecyclerView = inboxFragmentView.findViewById<RecyclerView>(R.id.inbox_recycler_view).apply {
            layoutManager = chatHeadsLayoutManager // Specify recycler view layout manager
            adapter = viewModel.chatHeadsFirebaseRecyclerViewAdapter // Set Recycler View Adapter to be firebase adapter
        }

        return inboxFragmentView
    }

    private fun startActivityToFindUser(){
        // Launches a new activity to search for a user
        viewModel.startActivityToFindUser(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            viewModel.START_NEW_CHAT_REQUEST_CODE -> {
                if (resultCode == AppCompatActivity.RESULT_OK){
                    viewModel.launchChatRoomActivityForNewChat(data, this)
                    Log.e(TAG,"Here back in Inbox Fragment")
                }else {
                    Log.e(TAG,"Bad activity response")
                }
            }
            else -> {
                Log.e(TAG,"Bad activity result code")
            }
        }
    }
    companion object {
        fun newInstance() = InboxFragment()
        const val TAG = "InboxFragment"
    }
}