package com.ltd_immersia_datenight.views.unity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.ltd_immersia_datenight.R;
import com.ltd_immersia_datenight.modelfirestore.Chat.ChatHead;
import com.ltd_immersia_datenight.modelfirestore.Chat.ChatRoomMessage;
import com.ltd_immersia_datenight.modelfirestore.User.UserModel;
import com.ltd_immersia_datenight.unity_plugin.UnityPlayerWrapperActivity;
import com.ltd_immersia_datenight.utils.constants.DatabaseConstants;
import com.ltd_immersia_datenight.utils.constants.IntentConstants;
import com.ltd_immersia_datenight.utils.constants.UnityConstants;
import com.ltd_immersia_datenight.utils.DateNight;
import com.ltd_immersia_datenight.views.datehub_navigation.DateHubNavigation;
import com.ltd_immersia_datenight.views.datehub_navigation.ui_fragments.dates.post_date.DateFinishedActivity;
import com.unity3d.player.UnityPlayer;

import java.util.HashMap;
import java.util.Map;

public class UnityEnvironmentLoad extends UnityPlayerWrapperActivity {
    // Views
    ConstraintLayout constraintLayoutForUnity;
    ConstraintLayout loadingScreenConstraintLayout;
    Button waitingRoomLeaveButton;
    FrameLayout unityViewLayout = mUnityPlayer;
    TextView datePartnerComing1;
    TextView datePartnerComing2;
    TextView loadingExpTextView;
    ProgressBar loadingExpProgBar;
    // App Data
    DateNight appState;
    // Firebase
    private DocumentReference dateDocument;
    private DatabaseReference chatRoomRef;
    private DatabaseReference chatHeadRef;
    private ListenerRegistration dateListenerRegisterInvitee;
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
    //Other
    private String TAG = "UnityEnvironmentLoad";
    private boolean quittedFromWaitingRoom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mapFullNames.put(currentUserId, currentUserFullName); mapFullNames.put(participantId, participantFullName);

        // Build chat room
        if (currentUserId.compareTo(participantId) < 0){
            chatRoomId = currentUserId + "," + participantId;
        } else if (currentUserId.compareTo(participantId) > 0) {
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
        // initialize Views
        constraintLayoutForUnity = findViewById(R.id.constraintLayoutForUnity);
        loadingScreenConstraintLayout = findViewById(R.id.loadingScreenLayout);
        datePartnerComing1 = findViewById(R.id.datePartnerComingTextView);
        datePartnerComing2 = findViewById(R.id.datePartnerComingTextView2);
        loadingExpTextView = findViewById(R.id.loadingExperienceTextView);
        loadingExpProgBar = findViewById(R.id.loadingExperienceProgBar);
        waitingRoomLeaveButton = findViewById(R.id.waitngRoomLeaveBtn);
        waitingRoomLeaveButton.setOnClickListener(v -> leaveDateFromWaitingRoom());

        //Add unity to view
        constraintLayoutForUnity.addView(mUnityPlayer.getView(), ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        mUnityPlayer.getView().setVisibility(View.INVISIBLE);

        // Send message to unity to download assets
        notifyUnityToDownloadAssets();

        mUnityPlayer.windowFocusChanged(true);
    }

    public void notifyUnityToDownloadAssets(){
        // On-start unity must download assets
        if (currentUserId.equals(dateCreatorId)){
            // serverToConnect should be empty string if current user is date creator
            UnitySendMessage(UnityConstants.NETWORK_MANAGER_GAME_OBJECT,
                             UnityConstants.STARTUP_FUNCTION_ARG,
                             generateInitializationParams(true));
        } else {
            UnitySendMessage(UnityConstants.NETWORK_MANAGER_GAME_OBJECT,
                             UnityConstants.STARTUP_FUNCTION_ARG,
                             generateInitializationParams(false));
        }
    }

    public void UnitySendMessage(String gameObject, String functionName, String jsonParam) {
        UnityPlayer.UnitySendMessage(gameObject, functionName, jsonParam);
    }

    private String generateInitializationParams(boolean isHost) {
        Map<String, Object> unityParams = new HashMap<>();

        //unity expected json keys don't match defined Intent constants
        unityParams.put("experienceID", experienceId);
        unityParams.put("dateID", dateId);
        unityParams.put("userId", currentUserId);
        unityParams.put("userName", currentUserFullName);
        unityParams.put("avatarUrl", avatarUrl);
        unityParams.put("isHost", isHost);

        //constructs map as a GSON object and then converts to JSON.  serialize java object to JSON
        String jsonBodyForUnity = new Gson().toJson(unityParams);
        Log.i("UnityJson", jsonBodyForUnity);

        return jsonBodyForUnity;
    }

    @Override
    protected void updateAssetDownloadPercentage(double percentage) {
        Log.i(TAG, "Unity Asset download percentage: " + percentage + "%");
        loadingExpProgBar.setProgress((int)percentage);
    }

    @Override
    protected void assetDownloadCompleted(int error) {
        runOnUiThread(() -> {
            Log.i(TAG, "Unity Asset download complete");
            // Initialize listener for date participant
            initializeListeners();

            // Hide Loading Views
            loadingExpTextView.setVisibility(View.INVISIBLE);
            loadingExpProgBar.setVisibility(View.INVISIBLE);

            // Show date partner views
            datePartnerComing1.setVisibility(View.VISIBLE);
            datePartnerComing2.setVisibility(View.VISIBLE);
        });
    }

    protected void initializeListeners(){
        if (!currentUserId.equals(dateCreatorId)){
            Log.i(TAG, "User is not date creator: Waiting to receive server information");
            // Date invitee will listen until serverToConnect is populated
            dateListenerRegisterInvitee = dateDocument.addSnapshotListener((dateSnapshot, error) -> {
                if(dateSnapshot != null &&
                        dateSnapshot.getString(DatabaseConstants.SERVER_TO_CONNECT_STRING) != null &&
                        !dateSnapshot.getString(DatabaseConstants.SERVER_TO_CONNECT_STRING).isEmpty()){
                    dateListenerRegisterInvitee.remove();
                    // clear server to connect in anticipation of next date
                    clearServerToConnectFromDate();
                    // Send server to unity
                    UnitySendMessage(UnityConstants.NETWORK_MANAGER_GAME_OBJECT,
                                     UnityConstants.CONNECT_TO_PHOTON,
                                     dateSnapshot.getString(DatabaseConstants.SERVER_TO_CONNECT_STRING));
                }
            });
        }
    }

    protected void photonConnectionCompleted() {
        runOnUiThread(() -> {
            Log.i(TAG, "Photon connection complete. Attempting to bring unity to front");
            initializeChatMessageListener();
            bringUnityActivityToFront();
        });
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

    private void bringUnityActivityToFront(){
        loadingScreenConstraintLayout.setVisibility(View.GONE);
        mUnityPlayer.getView().setVisibility(View.VISIBLE);
    }

    @Override
    protected void sendServerFromUnity(String server) {
        // Unity will call back to this function to populate server field if user is date creator
        Log.i(TAG, "Unity requesting to send server");
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
        Log.e(TAG, "Attempting to clear server to connect");
        HashMap<String, Object> data = new HashMap<>();
        data.put(DatabaseConstants.SERVER_TO_CONNECT_STRING, "");
        dateDocument.update(data);
    }

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

    @Override
    protected void sendKissFromUnity() {
        ++participantKissCount;
    }

    private void leaveDateFromWaitingRoom(){
        quittedFromWaitingRoom = true;
        mUnityPlayer.unload();

    }

    @Override
    public void onUnityPlayerUnloaded() {
        super.onUnityPlayerUnloaded();
        if (!quittedFromWaitingRoom){
            completeDate();
            launchDateFinishedActivity();
        } else {
            Intent intent = new Intent (this, DateHubNavigation.class);
            startActivity(intent);
        }
        Log.i(TAG, "Unloaded unity");
    }

    private void completeDate(){
        // Write date finished time
        if (currentUserId.equals(dateCreatorId)){
            // Write to date object
            Map<String, Object> data = new HashMap<>();
            data.put(DatabaseConstants.DATE_COMPLETED_TIME_FIELD, Timestamp.now());
            dateDocument.update(data);
        }

        // Write date statistics
        DocumentReference userRefDoc = FirebaseFirestore.getInstance()
                .collection(DatabaseConstants.USER_DATA_NODE)
                .document(participantId);
        userRefDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    UserModel user = task.getResult().toObject(UserModel.class);
                    if(user.getAvgDateStats() != null) {
                        int dateCount = user.getAvgDateStats().getDateCount() + 1;
                        int kissCount = user.getAvgDateStats().getKissCount() + participantKissCount;
                        HashMap<String, Object> data = new HashMap<>();
                        data.put(DatabaseConstants.AVG_DATE_STATS_DOC,
                                 new HashMap<String, Integer>() {{
                                     put(DatabaseConstants.DATE_COUNT, dateCount);
                                     put(DatabaseConstants.KISS_COUNT, kissCount);
                                 }});
                        userRefDoc.set(data, SetOptions.merge());
                    } else {
                        HashMap<String, Object> data = new HashMap<>();
                        data.put(DatabaseConstants.AVG_DATE_STATS_DOC,
                                 new HashMap<String, Integer>() {{
                                     put(DatabaseConstants.DATE_COUNT, 1);
                                     put(DatabaseConstants.KISS_COUNT, participantKissCount);
                                 }});
                        userRefDoc.set(data, SetOptions.merge());
                    }
                }
            }
        });
    }

    public void launchDateFinishedActivity() {
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
    protected int buyDTC(int amount) {
        return 0;
    }

    @Override
    protected boolean makePayment(int cost) {
        return false;
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
}