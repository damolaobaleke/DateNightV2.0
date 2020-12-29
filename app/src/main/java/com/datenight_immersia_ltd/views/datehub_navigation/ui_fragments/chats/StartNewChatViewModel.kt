package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.chats

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import com.datenight_immersia_ltd.DatabaseConstants
import com.datenight_immersia_ltd.IntentConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_start_new_chat.*

class StartNewChatViewModel: ViewModel() {
    private val dbReference = FirebaseFirestore.getInstance()
    private val dbReferenceUser = dbReference.collection(DatabaseConstants.USERS_NODE)
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    var foundUserId: String? = null
    var foundUserFullName: String? = null
    var foundUserPhotoUrl: String? = null
    var queryUserName: String? = null


    init {

    }

    fun searchForUserByUsername(username: String, parentContext: StartNewChatActivity){
        queryUserName = username
        parentContext.foundUserLayout.isVisible = false
        parentContext.foundUserLayout.isVisible = false
        val query = dbReferenceUser.whereEqualTo("username", queryUserName).get()
                .addOnSuccessListener {querySnapShot ->
                    if (querySnapShot != null){
                        val documents = querySnapShot.documents
                        if (documents.size == 1){
                            foundUserId = documents[0].get("id").toString()
                            foundUserFullName = documents[0].get("name").toString()
                            foundUserPhotoUrl = "" // TODO: Populate photo appropriately
                            Log.i(TAG, "Found user: $queryUserName, id: $foundUserId, name: $foundUserFullName")

                            // Display found user
                            parentContext.foundUserNameTextView.text = username
                            // parentContext.foundUserPhoto.setImageResource(); //TODO: Fix!
                            parentContext.noSuchUserTextView.isVisible = false
                            parentContext.foundUserLayout.isVisible = true

                        } else if (documents.size == 0){
                            foundUserId = null
                            foundUserFullName = null
                            foundUserPhotoUrl = null
                            parentContext.noSuchUserTextView.isVisible = true
                            parentContext.foundUserLayout.isVisible = false
                            Log.i(TAG, "No result for username: $username")
                            val toastMessage = "No user found for $username"
                            Toast.makeText(parentContext, toastMessage, Toast.LENGTH_SHORT).show()

                        } else {
                            // Username should never match more than one user
                            foundUserId = null
                            foundUserFullName = null
                            foundUserPhotoUrl = null
                            Log.i(TAG, "Invalid number of users (possibly more than 1) matching username: $username")
                            val toastMessage = "No user found for $username"
                            Toast.makeText(parentContext, toastMessage, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        foundUserId = null
                        foundUserFullName = null
                        foundUserPhotoUrl = null
                        Log.e(TAG, "Encountered error searching for $username")
                        val toastMessage = "Encountered error searching for $username"
                        Toast.makeText(parentContext, toastMessage, Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.i(TAG, exception.message)

                }

    }

    fun endActivityAndReturnFoundUser(parentContext: StartNewChatActivity){

        if(queryUserName != null && foundUserId != null && foundUserFullName != null){
            // Construct chat room Id
            val compareResult = currentUserId!!.compareTo(foundUserId!!)
            val newChatRoomId = if (compareResult < 0){
                "$currentUserId,$foundUserId"
            } else {
                "$foundUserId,$currentUserId"
            }
            Log.i(TAG, "Found ID: $newChatRoomId, Found name: $queryUserName")

            // End Activity and return found user details
            val intent = Intent()
                    .putExtra(IntentConstants.CHAT_ROOM_ID_EXTRA, newChatRoomId)
                    .putExtra(IntentConstants.CHAT_PARTICIPANT_ID_EXTRA, foundUserId)
                    .putExtra(IntentConstants.CHAT_PARTICIPANT_FULL_NAME_EXTRA, foundUserFullName)
                    .putExtra(IntentConstants.CHAT_PARTICIPANT_USER_NAME_EXTRA, queryUserName)
                    .putExtra(IntentConstants.CHAT_PARTICIPANT_PHOTO_URL_EXTRA, foundUserPhotoUrl)
            parentContext.setResult(AppCompatActivity.RESULT_OK, intent)
            parentContext.finish()
        }

    }

    companion object{
        const val TAG = "StarNewChatActivity"
    }
}