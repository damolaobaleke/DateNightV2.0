/**
 * ==========================================
 * ; Title:  Datenight
 * ; Description: Social Media
 * ; Author: Oyindamola Obaleke
 * ; Date:   4 Jul 2020
 * ;=======================================
 */
package com.immersia_ltd_datenight;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.immersia_ltd_datenight.modelfirestore.User.UserModel;
import com.immersia_ltd_datenight.utils.stripe.config.DateNight;
import com.immersia_ltd_datenight.views.authentication.LoginActivity;
import com.immersia_ltd_datenight.views.datehub_navigation.DateHubNavigation;
import com.immersia_ltd_datenight.views.landing_screen.BoardingScreen;
import com.immersia_ltd_datenight.views.onboarding.UserOnBoarding;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Getting Image";
    private static final long SPLASH_TIME_OUT = 3700;
    private ImageView dateNightLogo;
    private ImageView dateNightTextLogo;
    private FirebaseAuth mAuth;
    DocumentReference userDocRef;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateNightLogo = findViewById(R.id.dateNightLogo);
        dateNightTextLogo = findViewById(R.id.datenightTextLogo);


        //getImageBitmap();
        dateNightLogo.animate().scaleX(1.1f).scaleY(1.1f).setStartDelay(1000);
        dateNightLogo.animate().scaleX(1.4f).scaleY(1.4f);
        dateNightLogo.animate().start();

        dateNightTextLogo.animate().alpha(1f).setStartDelay(2000).start();

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.datenightlogo);
        dateNightLogo.setAnimation(animation);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        Runnable runnable = () -> {
            /*NOTE: WORKS FINE, COMMENTED OUT BECAUSE OF APP DATA, EVEN AFTER INITILIAZING IN HERE AS ENTRY POINT IS NOW HERE ALSO, STILL FAILS TO GET OBJECT IN PEND & SCHEDULED*/
            if (mAuth.getCurrentUser() != null) {
                userDocRef = db.collection(DatabaseConstants.USER_DATA_NODE).document(mAuth.getCurrentUser().getUid());
                checkOnBoarded();

            } else {
                //standard flow
                Intent intent = new Intent(this, UserOnBoarding.class);
                startActivity(intent);
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(runnable, SPLASH_TIME_OUT);
    }

    public void checkOnBoarded() {
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            UserModel user = documentSnapshot.toObject(UserModel.class);
            if (documentSnapshot.exists()) {
                if (!user.isOnBoarded() && mAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(MainActivity.this, UserOnBoarding.class);
                    startActivity(intent);

                } else if (user.isOnBoarded() && mAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(MainActivity.this, BoardingScreen.class);
                    startActivity(intent);

                } else if (!user.isOnBoarded() && mAuth.getCurrentUser() != null) {
                    DateNight appState = ((DateNight)this.getApplication());
                    if (appState.getAppData(mAuth.getUid()) == null) {
                        // Fetch required launch data and then launch DateHubNavigation class
                        appState.initializeAppData(mAuth.getUid(), MainActivity.this);
                    } else {
                        Intent intent = new Intent(MainActivity.this, DateHubNavigation.class);
                        startActivity(intent);
                    }

                } else {
                    DateNight appState = ((DateNight)this.getApplication());;
                    if (appState.getAppData(mAuth.getUid()) == null) {
                        // Fetch required launch data and then launch DateHubNavigation class
                        appState.initializeAppData(mAuth.getUid(), MainActivity.this);
                    } else {
                        Intent intent = new Intent(MainActivity.this, DateHubNavigation.class);
                        startActivity(intent);
                    }
                }
            }
        });

    }
}