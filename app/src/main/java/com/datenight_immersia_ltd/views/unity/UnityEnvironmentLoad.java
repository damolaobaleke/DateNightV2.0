package com.datenight_immersia_ltd.views.unity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.res.Configuration;
import android.os.Bundle;

import com.datenight_immersia_ltd.R;
import com.unity3d.player.UnityPlayer;

public class UnityEnvironmentLoad extends AppCompatActivity {

    UnityPlayer mUnityPlayer;
    ConstraintLayout constraintLayoutForUnity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unity_environment_load);

        mUnityPlayer = new UnityPlayer(this);
        constraintLayoutForUnity = findViewById(R.id.constraintLayoutForUnity);
        //Unity activity take whole view
        constraintLayoutForUnity.addView(mUnityPlayer.getView(), ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);

        mUnityPlayer.requestFocus();
        mUnityPlayer.windowFocusChanged(true);

//        Intent intent = new Intent(this, UnityPlayerActivity.class);
//        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        mUnityPlayer.quit();
        super.onDestroy();
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
    public void onBackPressed() {
        super.onBackPressed();
        //DATE FINISHED IMPLEMENTATION
    }
}