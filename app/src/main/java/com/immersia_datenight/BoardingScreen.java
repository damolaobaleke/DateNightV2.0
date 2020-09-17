package com.immersia_datenight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

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

            Intent intent = new Intent(this, DateHubNavigation.class);
            startActivity(intent);

            Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();

        }
    }
}