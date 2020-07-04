package com.datenight;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private static final long SPLASH_TIME_OUT = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView dateNightLogo = findViewById(R.id.dateNightLogo);
        dateNightLogo.animate().scaleX(1.1f).scaleY(1.1f).setStartDelay(2000);
        dateNightLogo.animate().scaleX(1.4f).scaleY(1.4f);
        dateNightLogo.animate().start();


        Runnable runnable = () -> {
            Intent intent = new Intent(this, BoardingScreen.class);
            startActivity(intent);

        };

        Handler handler = new Handler();
        handler.postDelayed(runnable, SPLASH_TIME_OUT);

    }
}