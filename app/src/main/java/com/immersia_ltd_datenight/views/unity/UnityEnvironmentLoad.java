package com.immersia_ltd_datenight.views.unity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.immersia_ltd_datenight.R;
import com.immersia_ltd_datenight.utils.constants.IntentConstants;
import com.immersia_ltd_datenight.utils.stripe.config.DateNight;
import com.immersia_ltd_datenight.views.datehub_navigation.ui_fragments.dates.post_date.DateFinishedActivity;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import java.util.HashMap;
import java.util.Map;

public class UnityEnvironmentLoad extends UnityPlayerActivity {

    //UnityPlayer mUnityPlayer;
    ConstraintLayout constraintLayoutForUnity;
    // App Data
    DateNight appState;

    //Needed for unity scene and date finished activity
    private String currentUserId = FirebaseAuth.getInstance().getUid();

    private String currentUserFullName;
    private HashMap<String, String> currentUserAvatarUrl;
    private String dateId;
    private String experienceId;
    private String participantId;
    private String participantFullName;
    private String dateCreatoravatarUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addControlsToUnityFrame();
        setContentView(R.layout.activity_unity_environment_load);

        // Grab data needed for unity
        appState = ((DateNight) this.getApplication());
        Intent intent = getIntent();
        currentUserFullName = appState.getAppData(currentUserId).getCurrentUser().getFullName();

        //passed from scheduled fragment
        currentUserAvatarUrl = appState.getAppData(currentUserId).getCurrentUser().getAvatar();
        dateId = intent.getStringExtra(IntentConstants.DATE_ID);
        experienceId = intent.getStringExtra(IntentConstants.EXPERIENCE_ID);
        participantId = intent.getStringExtra(IntentConstants.PARTICIPANT_ID_EXTRA);
        participantFullName = intent.getStringExtra(IntentConstants.PARTICIPANT_FULL_NAME_EXTRA);

        for (String avatarUrl : currentUserAvatarUrl.values()) {
            Log.i("avatarUrl", avatarUrl);

            dateCreatoravatarUrl = avatarUrl;
        }


        //Initialize unity player
        //mUnityPlayer = new UnityPlayer(this);
        constraintLayoutForUnity = findViewById(R.id.constraintLayoutForUnity);

        //Unity activity take whole view
        constraintLayoutForUnity.addView(mUnityPlayer.getView(), ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);

        //mUnityPlayer.requestFocus();
        mUnityPlayer.windowFocusChanged(true);


        UnitySendInfo("NetworkManager", "JsonInfo", sendToUnity());
    }

    /* These are not needed as already implemented within UnityPlayerActivity
    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnityPlayer.unload();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mUnityPlayer.unload();
    }

    @Override
    public void onPause() {
        super.onPause();
        mUnityPlayer.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mUnityPlayer.resume();
    }
    */

    public void UnitySendInfo(String gameObject, String functionName, String jsonParam) {
        UnityPlayer.UnitySendMessage(gameObject, functionName, jsonParam);
    }

    private String sendToUnity() {
        Map<String, Object> unityParams = new HashMap<>();

        //unity expected json keys don't match defined Intent constants
        unityParams.put("experienceID", experienceId);
        unityParams.put("dateID", dateId);
        unityParams.put("userId", currentUserId);
        unityParams.put("userName", currentUserFullName);
        unityParams.put("avatarUrl", dateCreatoravatarUrl);

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

    public void launchDateFinishedActivity() {
        mUnityPlayer.unload();
        //Start new activity after unity player is unloaded
        Intent intent = new Intent(this, DateFinishedActivity.class)
                .putExtra(IntentConstants.EXPERIENCE_ID, experienceId)
                .putExtra(IntentConstants.DATE_ID, dateId)
                .putExtra(IntentConstants.PARTICIPANT_ID_EXTRA, participantId)
                .putExtra(IntentConstants.PARTICIPANT_FULL_NAME_EXTRA, participantFullName);
        startActivity(intent);
    }

    @Override
    public void onUnityPlayerQuitted() {
        Log.e("UnityEnvironmentLoaded", "**************************QUITTING APP***************************");
        super.onUnityPlayerQuitted();
    }

    @Override
    public void onUnityPlayerUnloaded() {
        //super.onUnityPlayerUnloaded();

        //Log.e("UnityEnvironmentLoad", "**************Here attempting to launch date finished***********************");
        // Start new activity after unity player is unloaded
        // Intent intent = new Intent(this, DateFinishedActivity.class)
        //              .putExtra(IntentConstants.EXPERIENCE_ID, experienceId)
//                .putExtra(IntentConstants.DATE_ID, dateId)
//                .putExtra(IntentConstants.PARTICIPANT_ID_EXTRA, participantId)
//                .putExtra(IntentConstants.PARTICIPANT_FULL_NAME_EXTRA, participantFullName);
//        startActivity(intent);
    }
}