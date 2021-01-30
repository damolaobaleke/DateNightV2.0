 package com.datenight_immersia_ltd.views.unity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.datenight_immersia_ltd.DatabaseConstants;
import com.datenight_immersia_ltd.IntentConstants;
import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.utils.DateNight;
import com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.dates.post_date.DateFinishedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

 public class UnityEnvironmentLoad extends UnityPlayerActivity {

    //UnityPlayer mUnityPlayer;
    ConstraintLayout constraintLayoutForUnity;
    // App Data
    DateNight appState;
    //Needed for unity scene and date finished activity
    private String currentUserId = FirebaseAuth.getInstance().getUid();
    private String currentUserFullName;
    private String currentUserAvatarUrl;
    private String dateId;
    private String experienceId;
    private String participantId;
    private String participantFullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addControlsToUnityFrame();
        setContentView(R.layout.activity_unity_environment_load);

        // Grab data needed for unity
        appState = ((DateNight)this.getApplication());
        Intent intent = getIntent();
        currentUserFullName = appState.getAppData(currentUserId).getCurrentUser().getFullName();
        //currentUserAvatarUrl = appState.getAppData(currentUserId).getCurrentUser().getAvatar();
        dateId = intent.getStringExtra(IntentConstants.DATE_ID);
        experienceId = intent.getStringExtra(IntentConstants.EXPERIENCE_ID);
        participantId = intent.getStringExtra(IntentConstants.PARTICIPANT_ID_EXTRA);
        participantFullName = intent.getStringExtra(IntentConstants.PARTICIPANT_FULL_NAME_EXTRA);

        //mUnityPlayer = new UnityPlayer(this);
        constraintLayoutForUnity = findViewById(R.id.constraintLayoutForUnity);
        //Unity activity take whole view
        constraintLayoutForUnity.addView(mUnityPlayer.getView(), ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);

        //mUnityPlayer.requestFocus();
        mUnityPlayer.windowFocusChanged(true);

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
    private void addControlsToUnityFrame(){
        FrameLayout layout = mUnityPlayer;
        Button leaveDateButton = new Button(this);
        leaveDateButton.setText(R.string.leave_date);
        leaveDateButton.setX(50);
        leaveDateButton.setY(150);

        leaveDateButton.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which){
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
        layout.addView(leaveDateButton, 300, 200);
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

    public void launchDateFinishedActivity(){
        mUnityPlayer.unload();
    }

     @Override
     public void onUnityPlayerQuitted() {
        Log.e("UniteyEnvironmentLoaded", "**************************QUITTING APP***************************");
        super.onUnityPlayerQuitted();
     }

     @Override
     public void onUnityPlayerUnloaded() {
         //super.onUnityPlayerUnloaded();
         Log.e("UnityEnvironmentLoad", "**************Here attempting to launch date finished***********************");
         // Start new activity after unity player is unloaded
         Intent intent  = new Intent(this, DateFinishedActivity.class)
                 .putExtra(IntentConstants.EXPERIENCE_ID, experienceId)
                 .putExtra(IntentConstants.DATE_ID, dateId)
                 .putExtra(IntentConstants.PARTICIPANT_ID_EXTRA, participantId)
                 .putExtra(IntentConstants.PARTICIPANT_FULL_NAME_EXTRA, participantFullName);
         startActivity(intent);

     }
 }