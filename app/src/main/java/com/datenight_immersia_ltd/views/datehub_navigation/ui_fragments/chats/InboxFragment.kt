package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.chats

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.datenight_immersia_ltd.IntentConstants
import com.datenight_immersia_ltd.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class InboxFragment : Fragment() {
    // User Data
    private var currentUserId: String? = null
    private var currentUserName: String? = null

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
        arguments?.let {
            currentUserId = it.getString(IntentConstants.USER_ID_EXTRA)
            currentUserName = it.getString(IntentConstants.USER_NAME_EXTRA)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        inboxFragmentView =  inflater.inflate(R.layout.inbox_fragment, container, false)
        newChatFloatingButton = requireView().findViewById<FloatingActionButton>(R.id.newChatButton).apply {
            setOnClickListener { startActivityToFindUser() }
        }

        chatHeadsLayoutManager = LinearLayoutManager(context)
        chatHeadsRecyclerView = requireView().findViewById<RecyclerView>(R.id.inbox_recycler_view).apply {
            layoutManager = chatHeadsLayoutManager // Specify recycler view layout manager
            adapter = viewModel.chatHeadsFirebaseRecyclerViewAdapter // Set Recycler View Adapter to be firebase adapter
        }

        return inboxFragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(InboxViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun startActivityToFindUser(){
        viewModel.startActivityToFindUser()
    }


    companion object {
        fun newInstance() = InboxFragment()
    }
}