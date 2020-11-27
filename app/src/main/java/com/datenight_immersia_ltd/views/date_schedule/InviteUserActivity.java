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

package com.datenight_immersia_ltd.views.date_schedule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.modelfirestore.Date.DateModel;
import com.datenight_immersia_ltd.modelfirestore.Experience.ExperienceModel;
import com.datenight_immersia_ltd.modelfirestore.User.UserModel;
import com.datenight_immersia_ltd.modelfirestore.User.UserStatsModel;
import com.datenight_immersia_ltd.utils.RecylerViewAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class InviteUserActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseFirestore db;
    DocumentReference userRef, datesRef, experienceRef;
    CollectionReference usercollRef;
    EditText userSearch;
    Button cancelSearchButton;
    TextView cancelSearchTextView;
    String textInSearch;
    String userId;
    String experienceId;
    String dateChosen;
    String timeChosen;
    FirebaseAuth mAuth;
    String currentUser;
    UserModel user; //invitee
    UserModel creatorUser;
    HashMap<String, String> participants;
    HashMap<String, String> participantStatus;
    int mPosition;
    Date date;
    static String TAG = "InviteUserActivity";

    private RecyclerView recyclerView;
    private RecylerViewAdapter adapter; //bridge
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<UserModel> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_user);
        setTheme(R.style.InviteUserTheme);
        setTitle("Invite a user");
        //initialize db
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        currentUser = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        userSearch = findViewById(R.id.search_user);
        cancelSearchButton = findViewById(R.id.cancel_search_btn);
        recyclerView = findViewById(R.id.user_search_recyler_view);

        usercollRef = db.collection("userData");
        userRef = db.collection("userData").document(mAuth.getCurrentUser().getUid());


        searchBoxClicked();
        searchUser();
        cancelButton();

    }

    public void searchBoxClicked() {
        userSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                changeView();
            }
        });
    }

    public void searchUser() {
        userSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //suggestions ?
            }

            @Override
            public void afterTextChanged(Editable s) {
                usercollRef.whereEqualTo("search", s.toString().toLowerCase()) //searches based on lowercase username==== userSearch.getQuery().toString().toLowerCase()
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    if (documentSnapshot.exists()) {
                                        user = documentSnapshot.toObject(UserModel.class); //recreate doc object from the class
                                        user.setId(documentSnapshot.getId());

                                        System.out.println(user.getUsername() + " " + user.getId());
                                        Log.i(TAG, user.getUsername() + " " + user.getId());

                                        //populate recycler view
                                        users = new ArrayList<>();
                                        if (!(s.toString().length() <= 0) || user.getUsername() == null) {
                                            users.add(new UserModel(user.getUsername(), null, null, null, R.drawable.avatar_ellipse, "BASIC", null, null, null, "", ""));
                                        } else {
                                            users.add(new UserModel("No user found", null, null, null, R.drawable.avatar_ellipse, "BASIC", null, null, null, "", ""));
                                        }

                                        recyclerView.setHasFixedSize(true);
                                        layoutManager = new LinearLayoutManager(InviteUserActivity.this);
                                        adapter = new RecylerViewAdapter(users);

                                        recyclerView.setLayoutManager(layoutManager);
                                        recyclerView.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();

                                        inviteAndCreateDate();
                                        //populate recycler view

                                    } else {
                                        Toast.makeText(InviteUserActivity.this, "user not found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InviteUserActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void filter(String textInSearchBox) {
        //suggestions
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

    public CharSequence cancelButtonClicked() {
        if (userSearch.getText().toString().length() > 1) {
            return "";
        } else {
            return userSearch.getText().toString();
        }
    }

    public void cancelButton() {
        cancelSearchButton.setOnClickListener(v -> {
            if (userSearch.getText().length() >= 1) {
                userSearch.setText("");
                adapter.notifyDataSetChanged();
                return;
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

        adapter.setOnItemClickListener(new RecylerViewAdapter.OnItemClickListener() {
            @Override
            public void onInviteClick(int position) {
                mPosition = position;
                // Create Date and go to congrats screen
                createDateInDb();
                Log.i(TAG, user.getUsername() + " :username");
                congratulations(user.getId(), user.getFullName()); //the current pos of user being invited id
            }
        });
    }

    public void congratulations(String userId, String userFullName) {
        //Congratulations Screen
        Intent intent = new Intent(this, DateCreated.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userFullName", userFullName);
        startActivity(intent);
    }

    public void createDateInDb() {
        datesRef = db.collection("dates").document();

        //get creator User
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                creatorUser = documentSnapshot.toObject(UserModel.class);
                assert creatorUser != null;
                creatorUser.setId(documentSnapshot.getId());

                //Participants
                participants = new HashMap<>();
                participants.put(currentUser, creatorUser.getFullName());
                participants.put(user.getId(), user.getFullName());

                Log.i(TAG, creatorUser.getFullName() + " invited " + user.getFullName());
                //Participants

                Log.i(TAG, "The creator user " + creatorUser.getUsername() + " " + creatorUser.getFullName());

                //Participant Status
                participantStatus = new HashMap<>();
                participantStatus.put(creatorUser.getId(), "ACCEPTED");
                participantStatus.put(user.getId(), "PENDING");
                Log.i(TAG, "The participant status:" + participantStatus.get(creatorUser.getId()) + " " + participantStatus.get(user.getId()));
                //Participant Status
            }
        });

        //get experience
        experienceRef = db.collection("experiences").document("aNightInParis");
        experienceRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                ExperienceModel experience = documentSnapshot.toObject(ExperienceModel.class); //recreate document object from class
                assert experience != null;
                experienceId = experience.getId();
                Log.i(TAG, "The experience id:" + experience.getId());

                //create date in db with experience
                DateModel dateModel = new DateModel(datesRef.getId(), "345678", mAuth.getCurrentUser().getUid(), user.getFullName(), participants, dateDuration(), currentTime(), dateStringToTimestamp(dateChosen + timeChosen), "", "https://datenight.co.uk/3456ghb7", experience.getId(), participantStatus);
                datesRef.set(dateModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        toast();
                        //Associate dates with user
                        userRef.update("dateId", FieldValue.arrayUnion(datesRef.getId()));

                        Log.i(TAG,"The dateTime " +dateChosen+" "+timeChosen);
                        //Log.i(TAG, String.valueOf(dateStringToTimestamp(dateChosen)));
                        //go to congrats screen, should be here
                    }
                });
                //create date in db with experience
            }
        });

    }

    public void toast() {
        View view = getLayoutInflater().inflate(R.layout.create_date_toast, null);
        Button b = view.findViewById(R.id.toast_btn);

        Toast toast = new Toast(InviteUserActivity.this);
        b.setText("You've created a date");
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
    public  Timestamp dateStringToTimestamp(String dateStr) {
        try {
            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.UK);
            Date date = formatter.parse(dateStr);
            assert date != null;
            //convert date to timestamp
            return new Timestamp(date);
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
            return null;
        }
    }

    /*Using a Search View has limitations*/
    public void usingSearchView() {
//        userSearch.setQueryHint("Search for a user to invite");
//        userSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                //called on search button pressed
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                //called on text input change
//                textInSearch = newText;
//                usercollRef.whereEqualTo("search", newText.toLowerCase()) //searches based on lowercase username==== userSearch.getQuery().toString().toLowerCase()
//                        .get()
//                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                            @Override
//                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                                    if (documentSnapshot.exists()) {
//                                        UserModel user = documentSnapshot.toObject(UserModel.class); //recreate object from the class
//                                        user.setId(documentSnapshot.getId());
//
//                                        System.out.println(user.getUsername());
//                                    }
//                                }
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(InviteUserActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
//                    }
//                });
//
//                return false;
//            }
//        });
//    }
//
//    if (userSearch.getQuery().toString().length() > 1) {
//        return "";
//    } else {
//        return userSearch.getQuery().toString();
//    }
    }

}