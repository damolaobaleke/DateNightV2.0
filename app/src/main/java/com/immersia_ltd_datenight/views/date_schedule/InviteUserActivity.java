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

package com.immersia_ltd_datenight.views.date_schedule;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.immersia_ltd_datenight.utils.constants.DatabaseConstants;
import com.immersia_ltd_datenight.R;
import com.immersia_ltd_datenight.modelfirestore.Date.DateModel;
import com.immersia_ltd_datenight.modelfirestore.Experience.ExperienceModel;
import com.immersia_ltd_datenight.modelfirestore.User.UserModel;
import com.immersia_ltd_datenight.utils.RecylerViewAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class InviteUserActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseFirestore db;
    DocumentReference userRef, experienceRef;
    CollectionReference usercollRef, datesRef;
    EditText userSearch;
    TextView noUserFound;
    Button cancelSearchButton, shareButton, searchButton;
    String dateChosen;
    String timeChosen;
    FirebaseAuth mAuth;
    String currentUserId;
    UserModel dateinvitee; //invitee
    UserModel creatorUser;
    HashMap<String, String> participants;
    HashMap<String, String> participantStatus;
    HashMap<String, String> participantUsernames;
    HashMap<String, HashMap<String, Integer>> dateStatistics;
    int mPosition;
    Date date;
    static String TAG = "InviteUserActivity";
    protected final static String PENDING = "PENDING";
    protected final static String ACCEPTED = "ACCEPTED";

    private RecyclerView recyclerView;
    private RecylerViewAdapter adapter; //bridge
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<UserModel> users = new ArrayList<UserModel>();

    ExperienceModel experienceModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_user);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        setTitle("Invite a user");

        //initialize db
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        userSearch = findViewById(R.id.search_user);
        cancelSearchButton = findViewById(R.id.cancel_search_btn);
        noUserFound = findViewById(R.id.noUserFoundMessage2);
        shareButton = findViewById(R.id.share);
        searchButton = findViewById(R.id.inviteUserSearchButton);
        recyclerView = findViewById(R.id.user_search_recyler_view);

        usercollRef = db.collection(DatabaseConstants.USER_DATA_NODE);
        userRef = db.collection("userData").document(mAuth.getCurrentUser().getUid());
        experienceRef = db.collection("experiences").document("aNightInParis"); //change type to coll ref in future

        searchUserSetup();
        cancelButton();
        share();
    }

    private void share(){
        shareButton.setOnClickListener(v->{
            Uri link = Uri.parse("https://play.google.com/console/u/0/developers/8421302216223559919/app/4972918314666606268/tracks/4699075429511113233/releases/7/details");
            Spanned smiley = Html.fromHtml("&#U+263A",Html.FROM_HTML_MODE_LEGACY);

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Are you tired of the endless talking stages? Join Date night for a new dating experience.\nHere is the link "+ link).putExtra(Intent.EXTRA_SUBJECT,"Date Night");
            shareIntent.setType("text/plain");
            startActivity(shareIntent);
        });
    }

    public void searchUserSetup() {
        searchButton.setOnClickListener( l -> searchUsername());
        userSearch.setOnFocusChangeListener((v, hasFocus) -> changeView());
    }

    public void searchUsername(){
        Log.e("InviteUserActivity", "Search button clicked");
        String queryString =  userSearch.getText().toString().trim().toLowerCase();
        if (queryString.isEmpty()){
            return;
        }
        usercollRef.whereEqualTo("username",queryString) //searches based on lowercase username==== userSearch.getQuery().toString().toLowerCase()
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) { // TODO: Need to fix this to become a search button insead
                        if (queryDocumentSnapshots.size() < 1){
                            noUserFound.setVisibility(View.VISIBLE);
                        } else {
                            noUserFound.setVisibility(View.GONE);
                        }
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.exists()) {
                                dateinvitee = documentSnapshot.toObject(UserModel.class); //recreate document object from the model class
                                dateinvitee.setId(documentSnapshot.getId());
                                Log.e("InviteUserActivity", "Found user: " + documentSnapshot.get("id").toString());
                                Log.e("InviteUserActivity", "Found user (dateinvitee): " +dateinvitee.getId());

                                System.out.println(dateinvitee.getUsername() + " " + dateinvitee.getId());
                                Log.i(TAG, dateinvitee.getUsername() + " " + dateinvitee.getId());

                                HashMap<String, Integer> avtr = new HashMap<>();
                                avtr.put("avatar", R.drawable.avatar_ellipse); //R.drawable.avatar_ellipse

                                HashMap<String, String> avatar = new HashMap<>();
                                avatar.put("avatar", ""); //R.drawable.avatar_ellipse

                                users.add(new UserModel(dateinvitee.getId(), dateinvitee.getUsername(), dateinvitee.getFullName(), dateinvitee.getEmail(), null, null,null, null, null, null, null, null,Timestamp.now(),false,""));
                                //recyclerView.setHasFixedSize(true);
                                layoutManager = new LinearLayoutManager(InviteUserActivity.this);
                                adapter = new RecylerViewAdapter(users);

                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                                inviteAndCreateDate();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InviteUserActivity.this, "Error while searching for user", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, e.getMessage());
                    }
                });
    }

    public void changeView() {
        /*Not being used any longer*/
        //reduce search box size to 256dp
        //change constraints on user search view to fit new view
        ConstraintLayout.LayoutParams parameters = new ConstraintLayout.LayoutParams(500, ViewGroup.LayoutParams.WRAP_CONTENT);
        //convert int to density pixels
        int marginInDpTopRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        int marginInDpStart = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());

        parameters.setMargins(marginInDpStart, marginInDpTopRight, marginInDpTopRight, 0);
        parameters.horizontalBias = 0.19F;
        //userSearch.setLayoutParams(parameters);
        /*Not being used any longer*/

        //show cancel button
        cancelSearchButton.setVisibility(View.VISIBLE);
    }

    public void cancelButton() {
        cancelSearchButton.setOnClickListener(v -> {
            if (userSearch.getText().length() >= 1) {
                userSearch.setText("");

                if (users.size() > 0){
                    users.clear();
                    adapter.notifyItemRemoved(0);
                    return;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

    }


    public void inviteAndCreateDate() {
        //on invite click
        Intent intent = getIntent();
        dateChosen = intent.getStringExtra("dateChosen");
        timeChosen = intent.getStringExtra("timeChosen");
        Log.i(TAG, dateChosen + " at " + timeChosen);
        Log.i(TAG, "The " + dateStringToTimestamp(dateChosen + " " + timeChosen));

        adapter.setOnItemClickListener(new RecylerViewAdapter.OnItemClickListener() {
            @Override
            public void onInviteClick(int position) {
                mPosition = position;
                // Create Date and go to congrats screen
                createDateInDb();
                Log.i(TAG, dateinvitee.getUsername() + " :username");
                congratulations(dateinvitee.getId(), dateinvitee.getFullName(), datesRef.getId()); //the current pos of user being invited id
                //send notification
            }
        });
    }

    public void congratulations(String userId, String userFullName, String dateId) {
        //Congratulations Screen
        Intent intent = new Intent(this, DateCreated.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userFullName", userFullName);
        intent.putExtra("dateID", dateId);
        startActivity(intent);
    }

    public void createDateInDb() {
        datesRef = db.collection(DatabaseConstants.USER_DATA_NODE).document(currentUserId).collection(DatabaseConstants.DATES_COLLECTION);

        //get creator User
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                creatorUser = documentSnapshot.toObject(UserModel.class);
                assert creatorUser != null;
                creatorUser.setId(documentSnapshot.getId());

                //===Participants
                participants = new HashMap<>();
                participants.put(currentUserId, creatorUser.getFullName());
                participants.put(dateinvitee.getId(), dateinvitee.getFullName()); //invitee

                Log.i(TAG, creatorUser.getFullName() + " invited " + dateinvitee.getFullName());
                //Participants

                //Participant usernames
                participantUsernames = new HashMap<>();
                participantUsernames.put(creatorUser.getId(), creatorUser.getUsername());
                participantUsernames.put(dateinvitee.getId(), dateinvitee.getUsername());
                //Participant usernames

                Log.i(TAG, "The creator user " + creatorUser.getUsername() + " " + creatorUser.getFullName());

                //===Participant Status
                participantStatus = new HashMap<>();
                participantStatus.put(creatorUser.getId(), ACCEPTED);
                participantStatus.put(dateinvitee.getId(), PENDING);
                Log.i(TAG, "The participant status's:" + participantStatus.get(creatorUser.getId()) + " " + participantStatus.get(dateinvitee.getId()));
                //Participant Status

                //===Participants date statistics
                dateStatistics = new HashMap<>();
                //Rating
                HashMap<String, Integer> participantRating = new HashMap<>();
                participantRating.put(creatorUser.getId(), 0);
                participantRating.put(dateinvitee.getId(), 0);

                //Kisses
                HashMap<String, Integer> participantKisses = new HashMap<>();
                participantKisses.put(creatorUser.getId(), 0);
                participantKisses.put(dateinvitee.getId(), 0);
                //

                dateStatistics.put("participantRating", participantRating);
                dateStatistics.put("participantKisses", participantKisses);
                //Participants date statistics

                //create date in db with experience
                experienceRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            experienceModel = documentSnapshot.toObject(ExperienceModel.class); //recreate doc object from model class
                            assert experienceModel != null;

                            Log.i(TAG, "The experience id: " + experienceModel.getName());
                            //create date in db
                            String newDateId = datesRef.document().getId();
                            DateModel dateModel = new DateModel(newDateId, "345678", mAuth.getCurrentUser().getUid(), participants, participantUsernames, dateDuration(),dateStringToTimestamp(dateChosen +" "+ timeChosen), experienceModel.getId(), participantStatus);
                            datesRef.document(newDateId).set(dateModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //Associate dates with dateInvitee and dateCreator
                                    userRef.update("dateId", FieldValue.arrayUnion(datesRef.getId())); //update the fieldValue(array)

                                    //now using cloud functions
                                    DocumentReference dateInvitee = db.collection("userData").document(dateinvitee.getId());
                                    dateInvitee.update("dateId", FieldValue.arrayUnion(datesRef.getId()));

                                    Log.i(TAG, "The dateTime " + dateChosen + " " + timeChosen);
                                    Log.i(TAG, String.valueOf(dateStringToTimestamp(dateChosen + " " + timeChosen)));
                                    //Send Notification

                                }
                            });
                            //create date in db with experience

                        }
                    }
                });
            }
        });


    }

    public void toast(String message) {
        View view = getLayoutInflater().inflate(R.layout.create_date_toast, null);
        Button b = view.findViewById(R.id.toast_btn);

        Toast toast = new Toast(InviteUserActivity.this);
        b.setText(message);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    public Timestamp dateDuration() {
        Date date = Calendar.getInstance().getTime();
        return new Timestamp(date);
    }

    public Timestamp currentTime() {
        Date date = Calendar.getInstance().getTime();
        return new Timestamp(date);
    }

    public Timestamp convertdateStringtoMillis() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date dateC = formatter.parse(dateChosen);
            Date timeC = formatter.parse(timeChosen);
            long dateInmillis = dateC.getTime();
            long timeInmillis = timeC.getTime();
            long millis = dateInmillis + timeInmillis;

            System.out.println(millis);
            //convert millis to date
            date = new Date(millis);
        } catch (ParseException e) {
            System.out.println(e);
        }
        return new Timestamp(date);
    }

    //Might rather take two arguments dateChosen, timeChosen rather than having to add the two values
    public static Timestamp dateStringToTimestamp(final String dateStr) {
        try {
            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.getDefault()); //EE- Day name in week
            Date date = formatter.parse(dateStr);

            //convert date to timestamp
            return new Timestamp(date);
        } catch (ParseException e) {
            System.out.println("Error :" + e);
            return null;
        }
    }

    public String getExperience() {
        experienceRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    experienceModel = documentSnapshot.toObject(ExperienceModel.class); //recreate doc object from model class
                    assert experienceModel != null;
                    //Log.i(TAG, "The experience id: " + experienceModel.getName());
                }
            }
        });
        return experienceModel.getName();
    }

    public void populateRecyclerView() {
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(InviteUserActivity.this);
        adapter = new RecylerViewAdapter(users);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public static void notifyUsers(String inviteeId) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}