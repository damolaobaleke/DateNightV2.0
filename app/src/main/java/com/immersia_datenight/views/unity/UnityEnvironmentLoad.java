package com.immersia_datenight.views.unity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;

import com.immersia_datenight.R;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

public class UnityEnvironmentLoad extends AppCompatActivity {

    UnityPlayer mUnityPlayer;
    ConstraintLayout constraintLayoutForUnity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unity_environment_load);

        mUnityPlayer = new UnityPlayer(this);
        constraintLayoutForUnity = findViewById(R.id.constraintLayoutForUnity);
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
}