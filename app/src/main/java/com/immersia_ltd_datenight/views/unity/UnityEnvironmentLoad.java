package com.immersia_ltd_datenight.views.unity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.gson.Gson;
import com.immersia_ltd_datenight.R;
import com.immersia_ltd_datenight.modelfirestore.Chat.ChatHead;
import com.immersia_ltd_datenight.modelfirestore.Chat.ChatRoomMessage;
import com.immersia_ltd_datenight.utils.UnityPlayerWrapperActivity;
import com.immersia_ltd_datenight.utils.constants.DatabaseConstants;
import com.immersia_ltd_datenight.utils.constants.IntentConstants;
import com.immersia_ltd_datenight.utils.constants.UnityConstants;
import com.immersia_ltd_datenight.utils.stripe.config.DateNight;
import com.immersia_ltd_datenight.views.datehub_navigation.ui_fragments.dates.post_date.DateFinishedActivity;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import java.util.HashMap;
import java.util.Map;

public class UnityEnvironmentLoad extends UnityPlayerActivity {
    // Views
    ConstraintLayout constraintLayoutForUnity;
    // App Data
    DateNight appState;
    // Firebase
    private DocumentReference dateDocument;
    private DatabaseReference chatRoomRef;
    private DatabaseReference chatHeadRef;
    private ListenerRegistration dateListenerRegister;
    private ChildEventListener chatRoomEventListener;
    //Needed for unity scene and date finished activity
    private String currentUserId = FirebaseAuth.getInstance().getUid();
    private String currentUserFullName;
    private String currentUserName;
    private HashMap<String, String> currentUserAvatarUrl;
    private String participantId;
    private String participantFullName;
    private String participantUserName;
    private String dateId;
    private String dateCreatorId;
    private String experienceId;
    private String avatarUrl;
    private String chatRoomId;
    Map<String, String> mapUsernames = new HashMap<String, String>();;
    Map<String, String> mapFullNames = new HashMap<>();;
    //Stats
    private int participantKissCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addControlsToUnityFrame();
        setContentView(R.layout.activity_unity_environment_load);

        // Grab data needed for unity
        appState = ((DateNight) this.getApplication());

        currentUserFullName = appState.getAppData(currentUserId).getCurrentUser().getFullName();
        currentUserName = appState.getAppData(currentUserId).getCurrentUser().getUsername();
        avatarUrl = appState.getAppData(currentUserId).getCurrentUser().getAvatar().get(DatabaseConstants.AVATAR_URL_FIELD);

        //passed from scheduled fragment
        Intent intent = getIntent();
        dateId = intent.getStringExtra(IntentConstants.DATE_ID);
        dateCreatorId = intent.getStringExtra(IntentConstants.DATE_CREATOR_ID);
        experienceId = intent.getStringExtra(IntentConstants.EXPERIENCE_ID);
        participantId = intent.getStringExtra(IntentConstants.PARTICIPANT_ID_EXTRA);
        participantFullName = intent.getStringExtra(IntentConstants.PARTICIPANT_FULL_NAME_EXTRA);
        participantUserName = intent.getStringExtra(IntentConstants.PARTICIPANT_USER_NAME_EXTRA);
        mapUsernames.put(currentUserId, currentUserName); mapUsernames.put(participantId, participantUserName);
        mapFullNames.put(currentUserId, currentUserFullName); mapUsernames.put(participantId, participantFullName);

        // Build chat room
        if (currentUserId.compareTo(participantId) < 0){
            chatRoomId = currentUserId + "," + participantId;
        } else if (currentUserId.compareTo(participantId) < 0) {
            chatRoomId = participantId + "," + currentUserId;
        }
        dateDocument =  FirebaseFirestore.getInstance()
                .collection(DatabaseConstants.USER_DATA_NODE)
                .document(currentUserId)
                .collection(DatabaseConstants.DATES_COLLECTION)
                .document(dateId);
        chatRoomRef = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseConstants.MESSAGES_NODE).child(chatRoomId);
        chatHeadRef = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseConstants.CHAT_ROOMS_NODE)
                .child(currentUserId)
                .child(chatRoomId);
        constraintLayoutForUnity = findViewById(R.id.constraintLayoutForUnity);

        //Unity activity take whole view
        constraintLayoutForUnity.addView(mUnityPlayer.getView(), ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        // Initialize Unity
        initializeUnity();

        //mUnityPlayer.requestFocus();
        mUnityPlayer.windowFocusChanged(true);


    }

    private String generateInitializationParams(String serverToConnect) {
        Map<String, Object> unityParams = new HashMap<>();

        //unity expected json keys don't match defined Intent constants
        unityParams.put("experienceID", experienceId);
        unityParams.put("dateID", dateId);
        unityParams.put("userId", currentUserId);
        unityParams.put("userName", currentUserFullName);
        unityParams.put("avatarUrl", avatarUrl);
        unityParams.put("serverToConnect", serverToConnect);

        //constructs map as a GSON object and then converts to JSON.  serialize java object to JSON
        String jsonBodyForUnity = new Gson().toJson(unityParams);
        Log.i("UnityJson", jsonBodyForUnity);

        return jsonBodyForUnity;
    }

    private void addControlsToUnityFrame() {
        FrameLayout layout = mUnityPlayer;
        Button leaveDateButton = new Button(this);
        leaveDateButton.setText(R.string.leave_date);
        leaveDateButton.setAllCaps(false);
        leaveDateButton.setTextColor(getColor(R.color.white));
        leaveDateButton.setX(500);
        leaveDateButton.setY(100);
        leaveDateButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.custom_login_button, null));

        leaveDateButton.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked - leave date
                        launchDateFinishedActivity();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked -> do nothing
                        dialog.dismiss();
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Confirmation")
                    .setMessage("Are you sure you want to leave date?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener)
                    .show();
        });
        layout.addView(leaveDateButton, 180, 70);
    }

    public void initializeUnity(){
        // On-start unity must be initialized initialization params
        if (currentUserId.equals(dateCreatorId)){
            // serverToConnect should be empty string if current user is date creator
            UnitySendMessage(UnityConstants.NETWORK_MANAGER_GAME_OBJECT,
                             UnityConstants.STARTUP_FUNCTION_ARG,
                             generateInitializationParams(""));
            initializeChatMessageListener();
        } else {
            dateListenerRegister = dateDocument.addSnapshotListener((dateSnapshot, error) -> {
                if(dateSnapshot != null &&
                        dateSnapshot.getString("serverToConnect") != null &&
                        !dateSnapshot.getString("serverToConnect").isEmpty()){
                    dateListenerRegister.remove();
                    clearServerToConnectFromDate();
                    UnitySendMessage(UnityConstants.NETWORK_MANAGER_GAME_OBJECT,
                                     UnityConstants.STARTUP_FUNCTION_ARG,
                                     generateInitializationParams(dateSnapshot.getString(DatabaseConstants.SERVER_TO_CONNECT_STRING)));
                    initializeChatMessageListener();
                }
            });
        }
    }

    public void initializeChatMessageListener(){
        chatRoomEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot,
                                     @Nullable String previousChildName) {
                if (snapshot.exists() && snapshot.getValue() != null){
                    ChatRoomMessage message = snapshot.getValue(ChatRoomMessage.class);
                    if(!message.getSenderId().equals(currentUserId)){
                        sendReceivedParticipantChatToUnity(message.getText());
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        chatRoomRef.addChildEventListener(chatRoomEventListener);
    }

    public void UnitySendMessage(String gameObject, String functionName, String jsonParam) {
        UnityPlayer.UnitySendMessage(gameObject, functionName, jsonParam);
    }

    //@Override
    protected void sendChatFromUnity(String messageText) {
        if (!messageText.isEmpty()){
            // Send message
            ChatRoomMessage message = new ChatRoomMessage(currentUserId, "", messageText.trim(), System.currentTimeMillis() / 1000);
            chatRoomRef.push().setValue(message);

            // Update chatHeads for both users
            ChatHead chatHead = new ChatHead(mapFullNames, mapUsernames, message);
            chatHeadRef.setValue(chatHead);
        }
    }

    //@Override
    protected void sendKissFromUnity() {
        ++participantKissCount;
    }

    //@Override
    protected void sendServerFromUnity(String server) {
        // Write to date object
        Map<String, Object> data = new HashMap<>();
        data.put(DatabaseConstants.SERVER_TO_CONNECT_STRING, server);
        dateDocument.update(data);
    }

    private void sendReceivedParticipantChatToUnity(String message){
        UnitySendMessage(UnityConstants.MESSAGE_CONTROLLER_GAME_OBJECT,
                         UnityConstants.SEND_MESSAGE_FUNCTION,
                         message);
    }

    private void clearServerToConnectFromDate(){
        HashMap<String, Object> data = new HashMap<>();
        data.put(DatabaseConstants.SERVER_TO_CONNECT_STRING, "");
        dateDocument.update(data);
    }

    public void launchDateFinishedActivity() {
        mUnityPlayer.unload();
        chatRoomRef.removeEventListener(chatRoomEventListener);
        //Start new activity after unity player is unloaded
        Intent intent = new Intent(this, DateFinishedActivity.class)
                .putExtra(IntentConstants.EXPERIENCE_ID, experienceId)
                .putExtra(IntentConstants.DATE_ID, dateId)
                .putExtra(IntentConstants.PARTICIPANT_ID_EXTRA, participantId)
                .putExtra(IntentConstants.PARTICIPANT_FULL_NAME_EXTRA, participantFullName);
        startActivity(intent);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    //make the activity aware of what’s going on with the device. --Land,potr
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }


    @Override
    public void onUnityPlayerQuitted() {
        Log.e("UnityEnvironmentLoaded", "**************************QUITTING APP***************************");
        super.onUnityPlayerQuitted();
    }

    @Override
    public void onUnityPlayerUnloaded() {
        //super.onUnityPlayerUnloaded();
    }
}