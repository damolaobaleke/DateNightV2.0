package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.chats

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.datenight_immersia_ltd.DatabaseConstants
import com.datenight_immersia_ltd.IntentConstants
import com.datenight_immersia_ltd.R
import com.datenight_immersia_ltd.modelfirestore.Chat.ChatHead
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class InboxViewModel( username: String, userFullName: String) : ViewModel() {

    // Firebase related variables
    private val dbReferenceRealtimeDb = FirebaseDatabase.getInstance().reference // TODO: Possibly move dbReferences to model
    private var dbReferenceChatRoomsRealtimeDb: DatabaseReference
    private val dbReferenceFirestore = FirebaseFirestore.getInstance().collection(DatabaseConstants.USER_DATA_NODE)
    private lateinit var chatRoomDataParser: SnapshotParser<ChatHead?>
    private lateinit var firebaseRecyclerViewOptions: FirebaseRecyclerOptions<ChatHead>
    lateinit var chatHeadsFirebaseRecyclerViewAdapter: FirebaseRecyclerAdapter<ChatHead, ChatHeadViewHolder>
    val START_NEW_CHAT_REQUEST_CODE = 1001

    init {
        currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        // Fetch current user details
        val query = dbReferenceFirestore.document(currentUserId!!).get()
                .addOnSuccessListener {docSnapShot ->
                    if (docSnapShot != null){
                        currentUsername = docSnapShot.get(DatabaseConstants.USERNAME_CHILD).toString()
                        currentUserFullName = docSnapShot.get(DatabaseConstants.FULL_NAME_CHILD).toString()
                        Log.e(TAG, "Found username: $currentUsername, name: $currentUserFullName")
                    } else {
                        Log.e(TAG, "Encountered retrieving detail for userId: $currentUserId")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, exception.message)
                }

        // Set up Database reference and firebase recycler view adapter
        dbReferenceChatRoomsRealtimeDb =
                dbReferenceRealtimeDb.child(DatabaseConstants.CHAT_ROOMS_NODE).child(currentUserId!!)

    }

    fun initializeFirebaseRecyclerView(parentContext: InboxFragment){
        chatRoomDataParser =
                SnapshotParser<ChatHead?> { dataSnapshot ->
                    Log.e(TAG, dataSnapshot.value.toString())
                    val chatHead = dataSnapshot.getValue(ChatHead::class.java)
                    chatHead?.key = dataSnapshot.key
                    chatHead!!
                }
        firebaseRecyclerViewOptions = FirebaseRecyclerOptions.Builder<ChatHead>() // TODO: Modify this so it selects sorts chat rooms
                .setQuery(dbReferenceChatRoomsRealtimeDb.orderByChild(DatabaseConstants.MOST_RECENT_MESSAGE_TIMESTAMP), chatRoomDataParser)
                .setLifecycleOwner(parentContext)
                .build()
        chatHeadsFirebaseRecyclerViewAdapter =
                object : FirebaseRecyclerAdapter<ChatHead, ChatHeadViewHolder>(firebaseRecyclerViewOptions) {
                    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ChatHeadViewHolder {
                        val newChatHead = LayoutInflater.from(parent.context)
                                .inflate(R.layout.item_chat_head, parent, false)
                        return ChatHeadViewHolder(newChatHead)
                    }

                    override fun onBindViewHolder(holder: ChatHeadViewHolder, position: Int, data: ChatHead) {
                        data.setChatParticipantDetails(currentUserId!!)
                        holder.bind(data)
                        Log.e(TAG, "here after bind")
                    }

                    override fun onDataChanged() {
                        super.onDataChanged()
                        if (itemCount > 0){
                            parentContext.emptyInboxImage.visibility = View.INVISIBLE
                            parentContext.emptyInboxText.visibility = View.INVISIBLE
                        }else{
                            parentContext.emptyInboxImage.visibility = View.VISIBLE
                            parentContext.emptyInboxText.visibility = View.VISIBLE
                        }
                    }

                    // Overriding to reverse order of chat room heads
                    override fun getItem(position: Int): ChatHead {
                        return super.getItem(itemCount - (position + 1))
                    }
                }
    }

    fun startActivityToFindUser(parentContext: InboxFragment) {
        val intent = Intent(parentContext.requireActivity(), StartNewChatActivity::class.java)
                .putExtra(IntentConstants.USER_ID_EXTRA, currentUserId)
        parentContext.startActivityForResult(intent, START_NEW_CHAT_REQUEST_CODE)
    }

    fun launchChatRoomActivityForNewChat(data: Intent?, parentContext: InboxFragment) {
        val chatRoomActivityIntent = Intent(parentContext.requireActivity(), ChatRoomActivity::class.java)
                .putExtra(IntentConstants.USER_NAME_EXTRA, currentUsername)
                .putExtra(IntentConstants.USER_FULL_NAME_EXTRA, currentUserFullName)
                .putExtra(IntentConstants.CHAT_ROOM_ID_EXTRA, data?.getStringExtra(IntentConstants.CHAT_ROOM_ID_EXTRA))
                .putExtra(IntentConstants.CHAT_PARTICIPANT_ID_EXTRA, data?.getStringExtra(IntentConstants.CHAT_PARTICIPANT_ID_EXTRA))
                .putExtra(IntentConstants.CHAT_PARTICIPANT_USER_NAME_EXTRA, data?.getStringExtra(IntentConstants.CHAT_PARTICIPANT_USER_NAME_EXTRA))
                .putExtra(IntentConstants.CHAT_PARTICIPANT_FULL_NAME_EXTRA, data?.getStringExtra(IntentConstants.CHAT_PARTICIPANT_FULL_NAME_EXTRA))
                .putExtra(IntentConstants.CHAT_PARTICIPANT_PHOTO_URL_EXTRA, data?.getStringExtra(IntentConstants.CHAT_PARTICIPANT_PHOTO_URL_EXTRA))
        parentContext.startActivity(chatRoomActivityIntent, null)
    }

    class ChatHeadViewHolder(private val chatHeadView: View) :
            RecyclerView.ViewHolder(chatHeadView) {
        // Layouts
        private var mChatHeadImage: CircleImageView? = null
        private var mChatHeadName: TextView? = null
        private var mMostRecentMessage: TextView? = null
        private var mChatHeadTimeStamp: TextView? = null

        // Data
        private var mChatRoomKey: String? = null
        private var mChatParticipantId: String? = null
        private var mParticipantPhotoUrl: String? = null
        private var mChatParticipantUsername: String? = null
        private var mChatParticipantFullName: String? = null

        init {
            mChatHeadImage = itemView.findViewById(R.id.chatHeadImage)
            mChatHeadName = itemView.findViewById(R.id.chatHeadLabel)
            mMostRecentMessage = itemView.findViewById(R.id.chatHeadMostRecentMessage)
            mChatHeadTimeStamp = itemView.findViewById(R.id.chatHeadTimeStamp)

            // TODO: Set on click listener for view here to spawn new activity
            chatHeadView.setOnClickListener {
                // Launch chatRoomActivity
                Log.e(TAG, "Trying to start room: $mChatRoomKey")
                launchChatRoomActivityForExistingChat(
                        chatHeadView,
                        mChatRoomKey,
                        mChatParticipantId,
                        mChatParticipantUsername,
                        mParticipantPhotoUrl
                )
            }
        }

        fun bind(data: ChatHead) {
            // Data for views
            mChatHeadName?.text = data.participantFullName
            mMostRecentMessage?.text = data.mostRecentMessage.text
            mChatHeadTimeStamp?.text = constructChatHeadTime(data.mostRecentMessage.timeStamp)

            // Metadata
            mChatRoomKey = data.key
            mChatParticipantId = data.participantId
            mParticipantPhotoUrl = data.participantPhotoUrl //TODO: fix to photo url
            mChatParticipantUsername = data.participantUsername
            mChatParticipantFullName = data.participantFullName
        }

        private fun launchChatRoomActivityForExistingChat(
                view: View,
                mChatRoomKey: String?,
                mChatParticipantId: String?,
                mChatParticipantUsername: String?,
                mParticipantPhotoUrl: String?
        ) {
            val chatRoomActivityIntent = Intent(view.context, ChatRoomActivity::class.java)
                    .putExtra(IntentConstants.USER_NAME_EXTRA, currentUsername)
                    .putExtra(IntentConstants.USER_FULL_NAME_EXTRA, currentUserFullName)
                    .putExtra(IntentConstants.CHAT_ROOM_ID_EXTRA, mChatRoomKey)
                    .putExtra(IntentConstants.CHAT_PARTICIPANT_ID_EXTRA, mChatParticipantId)
                    .putExtra(IntentConstants.CHAT_PARTICIPANT_USER_NAME_EXTRA, mChatParticipantUsername)
                    .putExtra(IntentConstants.CHAT_PARTICIPANT_FULL_NAME_EXTRA, mChatParticipantFullName)
                    .putExtra(IntentConstants.CHAT_PARTICIPANT_PHOTO_URL_EXTRA, mParticipantPhotoUrl)
            startActivity(view.context, chatRoomActivityIntent, null)
        }

        private fun constructChatHeadTime(seconds: Long): String{
            var rVal = ""
            val messageTime = Calendar.getInstance()
            messageTime.timeInMillis = seconds * 1000
            val currentTime = Calendar.getInstance()

            if(messageTime.get(Calendar.YEAR) == currentTime.get(Calendar.YEAR) &&
                    messageTime.get(Calendar.MONTH) == currentTime.get(Calendar.MONTH) &&
                    messageTime.get(Calendar.WEEK_OF_MONTH) == currentTime.get(Calendar.WEEK_OF_MONTH)){

                if (messageTime.get(Calendar.DAY_OF_WEEK) == currentTime.get(Calendar.DAY_OF_WEEK)){
                    // Format time to show hours and minutes
                    rVal = SimpleDateFormat("h:mm a", Locale.getDefault()).format(messageTime.time)
                } else if(currentTime.get(Calendar.DAY_OF_WEEK) - messageTime.get(Calendar.DAY_OF_WEEK) == 1){
                    rVal = "Yesterday"
                } else{
                    // Format time to show day of week as in Tuesday
                    rVal = SimpleDateFormat("E", Locale.getDefault()).format(messageTime.time)
                }

            } else{
                // Format time to show only date
                rVal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(messageTime.time)
            }
            return rVal
        }
    }

    class InboxViewModelFactory(private val username: String,
                                private val userFullName: String): ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return InboxViewModel(username, userFullName) as T
        }

    }

    companion object {
        const val TAG = "InboxActivity"

        // User values are here so they can be visible within onBind method of adapter
        var currentUserId: String? = null
        lateinit var currentUsername: String
        lateinit var currentUserFullName: String
    }
}