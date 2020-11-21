package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.chats

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.datenight_immersia_ltd.DatabaseConstants
import com.datenight_immersia_ltd.IntentConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StartNewChatViewModel: ViewModel() {
    private val dbReference = FirebaseFirestore.getInstance()
    private val dbReferenceUser = dbReference.collection(DatabaseConstants.USERS_NODE)
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    init {

    }

    fun searchForUserByUsername(username: String, parentContext: StartNewChatActivity){
        val query = dbReferenceUser.whereEqualTo("username", username).get()
                .addOnSuccessListener {querySnapShot ->
                    if (querySnapShot != null){
                        val documents = querySnapShot.documents
                        if (documents.size == 1){
                            val id = documents[0].get("id").toString()
                            val name = documents[0].get("name").toString()
                            val photoUrl = "" // TODO: Populate photo appropriately
                            Log.e(TAG, "Found user: $username, id: $id, name: $name")
                            val toastMessage = "Loading chat room with $username"
                            Toast.makeText(parentContext, toastMessage, Toast.LENGTH_SHORT).show()
                            endActivityAndReturnFoundUser(id, username, name, photoUrl, parentContext)
                        } else{
                            // Username should never match more than one user
                            Log.e(TAG, "Invalid number of users (possibly more than 1) matching username: $username")
                            val toastMessage = "Encountered error searching for $username"
                            Toast.makeText(parentContext, toastMessage, Toast.LENGTH_SHORT).show()
                            parentContext.setResult(AppCompatActivity.RESULT_CANCELED)
                            parentContext.finish()
                        }

                    } else {
                        Log.e(TAG, "Encountered error searching username: $username")
                        val toastMessage = "Encountered error searching for $username"
                        Toast.makeText(parentContext, toastMessage, Toast.LENGTH_SHORT).show()
                        parentContext.setResult(AppCompatActivity.RESULT_CANCELED)
                        parentContext.finish()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, exception.message)

                }

    }

    fun endActivityAndReturnFoundUser(userId: String,
                                      username: String,
                                      name: String,
                                      userPhotoUrl: String,
                                      parentContext: StartNewChatActivity){
        // Construct chat room Id
        val compareResult = currentUserId!!.compareTo(userId)
        val newChatRoomId = if (compareResult < 0){
            "$currentUserId,$userId"
        } else {
            "$userId,$currentUserId"
        }
        Log.e(TAG, "Found ID: $newChatRoomId, Found name: $username")

        // End Activity and return found user details
        val intent = Intent()
                .putExtra(IntentConstants.CHAT_ROOM_ID_EXTRA, newChatRoomId)
                .putExtra(IntentConstants.CHAT_PARTICIPANT_ID_EXTRA, userId)
                .putExtra(IntentConstants.CHAT_PARTICIPANT_FULL_NAME_EXTRA, name)
                .putExtra(IntentConstants.CHAT_PARTICIPANT_USER_NAME_EXTRA, username)
                .putExtra(IntentConstants.CHAT_PARTICIPANT_PHOTO_URL_EXTRA, userPhotoUrl)
        parentContext.setResult(AppCompatActivity.RESULT_OK, intent)
        parentContext.finish()
    }

    companion object{
        const val TAG = "StarNewChatActivity"
    }
}