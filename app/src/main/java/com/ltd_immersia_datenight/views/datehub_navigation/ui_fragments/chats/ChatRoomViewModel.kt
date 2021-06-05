package com.ltd_immersia_datenight.views.datehub_navigation.ui_fragments.chats

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ltd_immersia_datenight.R
import com.ltd_immersia_datenight.modelfirestore.Chat.ChatHead
import com.ltd_immersia_datenight.modelfirestore.Chat.ChatRoomMessage
import com.ltd_immersia_datenight.utils.constants.DatabaseConstants
import java.text.SimpleDateFormat
import java.util.*

class ChatRoomViewModel(username: String,
                        fullName: String,
                        participantId: String,
                        participantUsername: String,
                        participantFullName: String,
                        roomId: String,
                        private val parentContext: ChatRoomActivity) : ViewModel() {

    private val currentUserId: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val currentUsername: String = username
    private val currentUserFullName: String = fullName
    private val chatParticipantId: String = participantId
    private val chatParticipantUsername: String = participantUsername
    private val chatParticipantFullName: String = participantFullName
    private val chatRoomId: String = roomId
    private val mapFullNames: Map<String?, String?>
    private val mapUsernames: Map<String?, String?>

    private  var dbReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var dbReferenceMessages: DatabaseReference
    private lateinit var messageDataParser: SnapshotParser<ChatRoomMessage?>
    private lateinit var firebaseRecyclerViewOptions: FirebaseRecyclerOptions<ChatRoomMessage>
    private lateinit var parentRecyclerView : RecyclerView
    var messagesFirebaseRecyclerAdapter:
            FirebaseRecyclerAdapter<ChatRoomMessage, ChatRoomMessageViewHolder>
    // Constanta
    val HORIZONTAL_BIAS_HOST = 1F
    val HORIZONTAL_BIAS_PARTICIPANT = 0F
    val WIDTH_DIVISOR = 0.45

    init{
        mapUsernames = mapOf(currentUserId to currentUsername, chatParticipantId to chatParticipantUsername)
        mapFullNames = mapOf(currentUserId to currentUserFullName, chatParticipantId to chatParticipantFullName)
        dbReferenceMessages = dbReference.child(DatabaseConstants.MESSAGES_NODE).child(chatRoomId)
        messageDataParser =
                SnapshotParser<ChatRoomMessage?> { dataSnapshot ->
                    val message = dataSnapshot.getValue(ChatRoomMessage::class.java)
                    Log.e(TAG, message?.text.toString())
                    message?.key = dataSnapshot.key // Store chat head key
                    message!!
                }
        firebaseRecyclerViewOptions = FirebaseRecyclerOptions.Builder<ChatRoomMessage>()
                .setQuery(dbReferenceMessages, messageDataParser)
                .setLifecycleOwner(parentContext)
                .build()
        messagesFirebaseRecyclerAdapter =
                object : FirebaseRecyclerAdapter<ChatRoomMessage, ChatRoomMessageViewHolder>(firebaseRecyclerViewOptions){


                    override fun getItemViewType(position: Int): Int {
                        return super.getItemViewType(position)
                    }
                    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ChatRoomMessageViewHolder {
                        val newMessage = LayoutInflater.from(parent.context)
                                .inflate(R.layout.item_chat_room_message, parent, false)
                        return ChatRoomMessageViewHolder(newMessage)
                    }

                    override fun onBindViewHolder(holder: ChatRoomMessageViewHolder, position: Int, data: ChatRoomMessage){
                        holder.bind(data, mapUsernames)
                        //parentRecyclerView.smoothScrollToPosition(itemCount)
                    }

                    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
                        super.onAttachedToRecyclerView(recyclerView)
                        parentRecyclerView = recyclerView
                    }
                    override fun onDataChanged() {
                        // There's nothing extra that needs doing
                        super.onDataChanged()
                        parentRecyclerView.smoothScrollToPosition(itemCount - 1)
                    }
                }

    }

    fun sendMessage(messageText: String){
        if (messageText.isNotEmpty()){
            // TODO: Check to make sure both user Ids and usernames are not null

            // Send message
            val message = ChatRoomMessage(currentUserId!!, "", messageText, System.currentTimeMillis() / 1000)
            dbReference.child(DatabaseConstants.MESSAGES_NODE)
                    .child(chatRoomId)
                    .push()
                    .setValue(message)

            // Update chatHeads for both users
            Log.e(TAG, "RoomID: $chatRoomId")
            val chatHead = ChatHead(mapFullNames, mapUsernames, message)
            dbReference.child(DatabaseConstants.CHAT_ROOMS_NODE)
                    .child(currentUserId)
                    .child(chatRoomId)
                    .setValue(chatHead)
        }
    }

    inner class ChatRoomMessageViewHolder(private val chatRoomMessageView: View):
            RecyclerView.ViewHolder(chatRoomMessageView){
        // Layouts
        private var mChatMessageText: TextView? = null
        private var mChatMessageSender: TextView? = null
        private var mTime: TextView? = null

        //Data
        private var mTimestamp: Long? = null
        private var imageUrl: String? = null

        init{
            mChatMessageText = itemView.findViewById(R.id.messageBodyTextView)
            mChatMessageSender = itemView.findViewById(R.id.messageSenderTextView)
            mTime = itemView.findViewById(R.id.messageTimeStampTextView)

            // Set on click listener if need be
        }

        fun bind(data: ChatRoomMessage, mapUsernames: Map<String?, String?>){
            val username =  mapUsernames[data.senderId]
            val messageTime = constructMessageTime(data.timeStamp)

            // Adjust message margins and backgrounds based on if current user or not
            val layoutParams = mChatMessageText!!.layoutParams as ConstraintLayout.LayoutParams
            if(data.senderId == currentUserId){
                layoutParams.horizontalBias = HORIZONTAL_BIAS_HOST
                mChatMessageText!!.setBackgroundResource(R.drawable.chat_room_msg_host_bg)
                mChatMessageText!!.setTextColor(parentRecyclerView.context.getColor(R.color.white))
                mChatMessageSender!!.setTextColor(parentRecyclerView.context.getColor(R.color.white))
                mTime!!.setTextColor(parentRecyclerView.context.getColor(R.color.white))
            } else {
                layoutParams.horizontalBias = HORIZONTAL_BIAS_PARTICIPANT
                mChatMessageText!!.setBackgroundResource(R.drawable.chat_room_msg_participant_bg)
                mChatMessageText!!.setTextColor(parentRecyclerView.context.getColor(R.color.date_night_grey_600))
                mChatMessageSender!!.setTextColor(parentRecyclerView.context.getColor(R.color.date_night_grey_600))
                mTime!!.setTextColor(parentRecyclerView.context.getColor(R.color.date_night_grey_600))
            }
            mChatMessageText!!.minEms = ((messageTime.length + username!!.length) * WIDTH_DIVISOR).toInt()
            mChatMessageText!!.layoutParams = layoutParams

            // Populate views
            mChatMessageText?.text = data.text
            mChatMessageSender?.text = username
            mTime?.text = messageTime
            mTimestamp = data.timeStamp
            imageUrl = data.imageUrl
        }

        private fun constructMessageTime(seconds: Long): String{
            var rVal = ""
            val messageTime = Calendar.getInstance()
            messageTime.timeInMillis = seconds * 1000
            val currentTime = Calendar.getInstance()

            if(messageTime.get(Calendar.YEAR) == currentTime.get(Calendar.YEAR) &&
                    messageTime.get(Calendar.MONTH) == currentTime.get(Calendar.MONTH) &&
                    messageTime.get(Calendar.WEEK_OF_MONTH) == currentTime.get(Calendar.WEEK_OF_MONTH)){

                if (messageTime.get(Calendar.DAY_OF_WEEK) == currentTime.get(Calendar.DAY_OF_WEEK)){
                    // Format time to show hours and minutes
                    rVal = "• " + SimpleDateFormat("h:mm a", Locale.getDefault()).format(messageTime.time)
                } else if(currentTime.get(Calendar.DAY_OF_WEEK) - messageTime.get(Calendar.DAY_OF_WEEK) == 1){
                    // Format time to prev day + time as in "• Yesterday, 2.40pm"
                    rVal = "• Yesterday, " +  SimpleDateFormat("h:mm a", Locale.getDefault()).format(messageTime.time)
                } else{
                    // Format time to show day of week + time as in "• Tuesday, 2.40pm"
                    rVal = "• " + SimpleDateFormat("E", Locale.getDefault()).format(messageTime.time) +
                            ", " + SimpleDateFormat("h:mm a", Locale.getDefault()).format(messageTime.time)
                }

            } else{
                // Format time to show date + time as in "• 2020-11-01, 2.40pm"
                rVal = "• " + SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(messageTime.time) +
                        ", " + SimpleDateFormat("h:mm a", Locale.getDefault()).format(messageTime.time)
            }
            return rVal
        }
    }

    class ChatRoomViewModelFactory(private val username: String,
                                   private val fullName: String,
                                   private val participantId: String,
                                   private val participantUsername: String,
                                   private val participantFullName: String,
                                   private val roomId: String,
                                   private val parentContext: ChatRoomActivity): ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ChatRoomViewModel(username,
                    fullName,
                    participantId,
                    participantUsername,
                    participantFullName,
                    roomId,
                    parentContext) as T
        }
    }

    companion object{
        const val TAG = "ChatRoomActivity";
    }
}