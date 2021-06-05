/*
 * Copyright 2020 Damola Obaleke. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltd_immersia_datenight.views.date_schedule;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ltd_immersia_datenight.R;
import com.ltd_immersia_datenight.modelfirestore.Experience.ExperienceModel;

import java.util.Calendar;
import java.util.Locale;

public class DateScheduleActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    TextView dateChosen, experienceDescription, experienceCost;
    TextView timeChosen;
    TextView experienceName, comingSoonText;
    Button scheduleDateTime;
    Button previewDateScene;

    Calendar calendar;
    Intent intent;

    VideoView videoView;
    DocumentReference expRef;
    FirebaseFirestore db;
    ExperienceModel experience;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_schedule);
        setTitle("Schedule Date");

        scheduleDateTime = findViewById(R.id.schedule_date);
        scheduleDateTime.setOnClickListener(v -> scheduleDateTime());

        previewDateScene = findViewById(R.id.preview_button);
        previewDateScene.setOnClickListener(v -> previewDateScene());

        calendar = Calendar.getInstance();

        bindId();

        intent = getIntent();
        Log.i("DateSchedule", intent.getStringExtra("experienceName") + intent.getStringExtra("experienceDesc") + intent.getStringExtra("experienceCost"));


        if (intent.getStringExtra("experienceCost") != null) {
            experienceCost.setText(intent.getStringExtra("experienceCost"));
        } else {
            experienceCost.setText(R.string.exp_val_free);
        }

    }

    public void bindId() {
        experienceName = findViewById(R.id.experience_name_in_schedule);
        experienceDescription = findViewById(R.id.experience_descr);
        experienceCost = findViewById(R.id.free_price);
        videoView = findViewById(R.id.videoView);
        comingSoonText = findViewById(R.id.coming_soon_text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        backgroundVideo();
    }

    public void backgroundVideo() {
        if (intent.getStringExtra("experienceName").equals("Dinner in Paris")) {
            videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.paris_one);
            videoView.setOnPreparedListener(mp -> {
                mp.setLooping(true);
                mp.start();
            });

        } else if (intent.getStringExtra("experienceName").equals("Picnic in the Meadow")) {
            videoView.setVisibility(View.INVISIBLE);
            scheduleDateTime.setVisibility(View.INVISIBLE);
            comingSoonText.setVisibility(View.VISIBLE);
            setExperienceVisibility();

        } else if (intent.getStringExtra("experienceName").equals("Love in the Clouds ☁")) {
            setExperienceVisibility();
            videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.paris_one);
            videoView.setOnPreparedListener(mp -> {
                mp.setLooping(true);
                mp.start();
            });
        }
    }

    public void setExperienceVisibility() {
        experienceName.setVisibility(View.VISIBLE);
        experienceDescription.setVisibility(View.VISIBLE);
        experienceName.setText(intent.getStringExtra("experienceName"));
        experienceDescription.setText(intent.getStringExtra("experienceDesc"));
    }

    public void previewDateScene() {
        //3D MODEL OF PARIS and avatar == https://d1a370nemizbjq.cloudfront.net/134d0587-f380-4226-b981-1c51796b7c3d.glb, https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Avocado/glTF/Avocado.gltf, https://res.cloudinary.com/dayvbcxai/image/upload/v1605911537/DateNight/134d0587-f380-4226-b981-1c51796b7c3d_bqhvuk.glb
        //get params(3d&mode=3d_only, AR, etc..): https://developers.google.com/ar/develop/java/scene-viewer#java

        Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
        sceneViewerIntent.setData(Uri.parse("https://arvr.google.com/scene-viewer/1.0?file=https://res.cloudinary.com/dayvbcxai/image/upload/v1605911537/DateNight/134d0587-f380-4226-b981-1c51796b7c3d_bqhvuk.glb"));
        sceneViewerIntent.setPackage("com.google.android.googlequicksearchbox");
        startActivity(sceneViewerIntent);
    }

    public void scheduleDateTime() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.custom_date_schedule_view, null);

        Button createDateSchedule = view.findViewById(R.id.create_date_schedule);
        Button pickDate = view.findViewById(R.id.pick_date);
        Button pickTime = view.findViewById(R.id.pick_time);
        dateChosen = view.findViewById(R.id.dateChosen);
        timeChosen = view.findViewById(R.id.timeChosen);
        Button cancelCreation = view.findViewById(R.id.cancel_schedule_creation);

        pickDate.setOnClickListener(v -> pickDate());
        pickTime.setOnClickListener(v -> pickTime());
        cancelCreation.setOnClickListener(v -> bottomSheetDialog.dismiss());

        createDateSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(this, InviteUserActivity.class);
            intent.putExtra("dateChosen", dateChosen.getText());
            intent.putExtra("timeChosen", timeChosen.getText());
            startActivity(intent);
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.create();
        bottomSheetDialog.show();

    }

    private void pickDate() {
        DatePickerDialog dateDialog = new DatePickerDialog(this, this, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        dateDialog.show();
    }

    private void pickTime() {
        TimePickerDialog timeDialog = new TimePickerDialog(this, this, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), false);
        timeDialog.show();

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        dateChosen.setText(String.format(Locale.getDefault(), "%02d-%02d-%02d", dayOfMonth, month + 1, year)); //add one to the month as array pos jan is 0
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        int hour12HourClock;
        String AM_PM = "";

        /*
        calendar = Calendar.getInstance();
        if(calendar.get(Calendar.AM_PM) == Calendar.AM){
            AM_PM = "am";
        }else if(calendar.get(Calendar.AM_PM) == Calendar.PM){
            AM_PM = "pm";
        }else{
            AM_PM = "";
        }

        */

        if (hourOfDay == 0){ // 12.00 AM
            AM_PM = "AM";
            hour12HourClock = 12;
        }
        else if (hourOfDay < 12){
            AM_PM = "AM";
            hour12HourClock = hourOfDay;
        }  else if (hourOfDay == 12){ // 12:00 PM
            AM_PM = "PM";
            hour12HourClock = hourOfDay;
        } else {
            AM_PM = "PM";
            hour12HourClock = hourOfDay - 12;
        }
        timeChosen.setText(String.format(Locale.getDefault(), "%01d:%02d %s", hour12HourClock, minute, AM_PM));
    }

    public ExperienceModel getExperience() {
        //Instance of db
        db = FirebaseFirestore.getInstance();
        //Ref to Doc
        expRef = db.collection("experiences").document("capBalloonRide");

        expRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                experience = documentSnapshot.toObject(ExperienceModel.class);
                assert experience != null;
                Log.i("DateSchedule", experience.getName());
            }
        });
        return experience;
    }
}