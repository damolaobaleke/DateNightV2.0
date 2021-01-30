package com.datenight_immersia_ltd.views.landing_screen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.datenight_immersia_ltd.utils.DateNight;
import com.datenight_immersia_ltd.views.datehub_navigation.DateHubNavigation;
import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.views.authentication.LoginActivity;
import com.datenight_immersia_ltd.views.authentication.SignUpActivity;
import com.datenight_immersia_ltd.views.user_profile.UserProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BoardingScreen extends AppCompatActivity {
    FirebaseAuth mAuth;
    Button signUp;
    Button logIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boarding_screen);

        signUp= findViewById(R.id.SignUp);
        logIn = findViewById(R.id.Login);

        logIn.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
            //Userprofile();
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        //instantiate firebase auth class
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        VideoView video= findViewById(R.id.videoView);
//        video.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.datenight_vid_bg);
//        video.setOnPreparedListener(mp -> {
//            mp.setLooping(true);
//            mp.start();
//        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Closes application
        Intent close_app = new Intent(Intent.ACTION_MAIN);
        close_app.addCategory(Intent.CATEGORY_HOME);
        close_app.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(close_app);
    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            String name = user.getEmail();
            assert name != null;
            Log.i("Name", name);

            DateNight appState = ((DateNight)this.getApplication());
            if (appState.getAppData(user.getUid()) == null){
                // Fetch required launch data and then launch DateHubNavigation class
                appState.initializeAppData(user.getUid(), this);
            } else {
                Intent intent = new Intent(this, DateHubNavigation.class);
                startActivity(intent);
            }


            Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();
        } else {
            // TODO: Log user out
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();

        }
    }

    //TEST
    public void Userprofile(){
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

}