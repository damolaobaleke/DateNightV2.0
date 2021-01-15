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

package com.datenight_immersia_ltd.utils;

import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.modelfirestore.Date.DateModel;
import com.datenight_immersia_ltd.modelfirestore.Experience.ExperienceModel;
import com.datenight_immersia_ltd.modelfirestore.User.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class RecyclerViewAdapterPending extends RecyclerView.Adapter<RecyclerViewAdapterPending.PendingDatesViewHolder> {
    ArrayList<DateModel> mDateLists;
    DocumentReference datesRef;
    DocumentReference userCreatorRef, dateInviteeRef, expRef;
    CollectionReference experienceCollRef, userCollRef;
    UserModel dateCreator;
    ExperienceModel experienceModel;
    UserModel userModel;
    DateModel mDateModel;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String result;

    Intent intent;
    String inviteeKey;
    String creatorKey;

    protected final static String PENDING = "PENDING";
    protected final static String ACCEPTED = "ACCEPTED";
    protected final static String TAG = "AdapterPending";

    //click listeners to listen on recycler view child view buttons for pendingAdapter
    private OnItemClickListener mListener;

    //refactor
    public interface OnItemClickListener {
        void onCancelInvite(int position);

        void onEditInvite(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    //

    public static class PendingDatesViewHolder extends RecyclerView.ViewHolder {
        public TextView mInvitee;
        Button editInvite;
        Button cancelInvite;
        TextView timeCreated;

        public PendingDatesViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            mInvitee = itemView.findViewById(R.id.invite_description);
            editInvite = itemView.findViewById(R.id.edit_invite_btn);
            cancelInvite = itemView.findViewById(R.id.cancel_invite_btn);
            timeCreated = itemView.findViewById(R.id.time_created);

            //On edit invite clicked
            editInvite.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) { //make sure position is valid, there is a position
                        listener.onEditInvite(position); //pass recycler adapter position to interface method
                    }
                }
            });
            //On edit invite clicked

            //On cancel invite clicked
            cancelInvite.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) { //make sure position is valid, there is a position
                        listener.onCancelInvite(position); //pass r adapter position to interface method
                    }
                }
            });
            //On cancel invite clicked
        }
    }

    //Constructor to get data of (ArrayList of dates) into Adapter
    public RecyclerViewAdapterPending(ArrayList<DateModel> mDateLists) {
        this.mDateLists = mDateLists;
    }

    @NonNull
    @Override
    public PendingDatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_dates_pending_lv, parent, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        userCreatorRef = db.collection("userData").document(mAuth.getCurrentUser().getUid());

        return new PendingDatesViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingDatesViewHolder holder, int position) {
        DateModel dateModel = mDateLists.get(position); //get position of item in recy view
        Log.i(TAG, "Date doc\n" + dateModel.getId() + "\n" + dateModel.getLinkedExperienceId() + "\n" + dateModel.getParticipantUsernames() +"\n"+ dateModel.getPassword());

        datesRef = db.collection("dates").document(dateModel.getId()); //id of date in position

        userCreatorRef.get().addOnSuccessListener(documentSnapshot -> {

            dateCreator = documentSnapshot.toObject(UserModel.class);//currentUser
            assert dateCreator != null;
            dateCreator.setId(dateModel.getCreator());

            datesRef.get().addOnSuccessListener(datedocumentSnapshot -> {

                if (datedocumentSnapshot.exists()) {
                    DateModel dateModel1 = datedocumentSnapshot.toObject(DateModel.class);
                    assert dateModel1 != null;
                    mDateModel = dateModel1; //
                    Log.i(TAG, "Date doc\n" + dateModel1.getId() + "\n" + dateModel1.getLinkedExperienceId() + "\n" + dateModel1.getParticipantUsernames());

                    //get experience name
                    //expRef = db.collection("experiences").document(dateModel1.getLinkedexperienceId());

                    for (String key : dateModel1.getParticipants().keySet()) {
                        if (key.equals(mAuth.getCurrentUser().getUid()) && key.equals(dateModel1.getCreator())) { //key.equals(mAuth.getCurrentUser().getUid())
                            creatorKey = key;
                            Log.i(TAG, "creator: " + key);
                        } else {
                            inviteeKey = key;
                        }
                    }


                    //If creator is the current user
                    if (dateModel1.getCreator().equals(mAuth.getCurrentUser().getUid())) {

//                        expRef.get().addOnSuccessListener(documentSnapshot1 -> {
//                            if (documentSnapshot1.exists()) {
//                                experienceModel = documentSnapshot1.toObject(ExperienceModel.class);
//                                assert experienceModel != null;
//                                Log.i(TAG, experienceModel.getName());


                                holder.mInvitee.setText(String.format("Waiting for %s to accept your invite to paris" , dateModel1.getParticipants().get(inviteeKey)));

                                result = String.valueOf(DateUtils.getRelativeTimeSpanString(dateModel1.getTimeCreated().getSeconds() * 1000, currentTime().getSeconds() * 1000, 0));
                                Log.i("AdapterPending", result);

                                holder.timeCreated.setText(result);
//                            }
//                        });

                    } else {

//                        expRef.get().addOnSuccessListener(documentSnapshot1 -> {
//                            if (documentSnapshot1.exists()) {
//                                experienceModel = documentSnapshot1.toObject(ExperienceModel.class);
//                                assert experienceModel != null;

                                ////
                                Log.i(TAG, "invitee id: " + inviteeKey);
                                Log.i(TAG, "invitee name: " + dateModel1.getParticipants().get(inviteeKey));

                                holder.mInvitee.setText(String.format("%s is inviting you to paris", dateModel1.getParticipants().get(dateModel1.getCreator())));
                                holder.editInvite.setText(R.string.accept_invite);

                                //change /override default click listener function to "ACCEPT" invite function
                                holder.editInvite.setOnClickListener(v -> {

                                    for (String key : dateModel1.getParticipants().keySet()) {
                                        if (key.equals(dateModel1.getCreator())) {
                                            creatorKey = key;
                                            Log.i(TAG, "creator: " + key);
                                        } else {
                                            inviteeKey = key;
                                        }
                                    }

                                    HashMap<String, Object> participantStatus = new HashMap<>();
                                    participantStatus.put(inviteeKey, ACCEPTED); //invitee now ACCEPTED
                                    participantStatus.put(dateModel1.getCreator(), ACCEPTED);

                                    Log.i("Adapter Pending", dateModel1.getParticipants().get(inviteeKey) + " The invitee Now accepted");
                                    datesRef.update("participantStatus", participantStatus);


                                    //after accept clicked remove position from pending fragment, show in scheduled
                                    mDateLists.remove(position);

                                });

                                holder.cancelInvite.setText(R.string.reject_invite);
                                holder.timeCreated.setText(result);
                                ////

                                dateInviteeRef = db.collection("userData").document(inviteeKey); //add invitee id

                            }
//                        });
//                    }
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return mDateLists.size();
    }

    public String calculateTimeCreated(int pos) {
        db = FirebaseFirestore.getInstance();
        DateModel dateModel = mDateLists.get(pos); //NOT DRY, MAKE GLOBAL

        datesRef = db.collection("dates").document(dateModel.getId());
        datesRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                DateModel dateModel1 = documentSnapshot.toObject(DateModel.class);
                assert dateModel1 != null;
                Timestamp timeCreated = dateModel1.getTimeCreated();

                result = String.valueOf(DateUtils.getRelativeTimeSpanString(timeCreated.getSeconds() * 1000, currentTime().getSeconds() * 1000, 0));
                Log.i("AdapterPending", result);
            }
        });
        return result;
    }


    public Timestamp currentTime() {
        //get current time
        Date date = Calendar.getInstance().getTime();
        return new Timestamp(date);
    }

    public String getEmojiUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    public String getExperienceName() {
        experienceCollRef = db.collection("experiences");
        experienceCollRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (documentSnapshot.exists()) {
                        experienceModel = documentSnapshot.toObject(ExperienceModel.class);

//                        if(experienceModel.getName() == cardclickedName(experience)){
//
//                        }
                    }
                }
            }
        });

        //==========================\\
        //experience
        expRef = db.collection("experiences").document(mDateModel.getLinkedExperienceId());
        expRef.get().addOnSuccessListener(documentSnapshot1 -> {
            if (documentSnapshot1.exists()) {
                experienceModel = documentSnapshot1.toObject(ExperienceModel.class);
                assert experienceModel != null;
                Log.i(TAG, experienceModel.getName());
            }
        });
        //experience
        return experienceModel.getName() + "";
    }

    public String getInvitee() {
        dateInviteeRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                userModel = documentSnapshot.toObject(UserModel.class);
                assert userModel != null;
                Log.i("AdapterPending", userModel.getId());
            }
        });
        return userModel.getId();
    }


    public void redundant() {
        //paris night dinner shouldn't be hard coded, experience name should be gotten based on experience clicked. Only there for test
//        if (dateModel1.getId() != null && dateModel1.getDateCreator().equals(mAuth.getCurrentUser().getUid())) {
//
//            //get experience name
//            expRef = db.collection("experiences").document(dateModel1.getLinkedexperienceId());
//
//            expRef.get().addOnSuccessListener(documentSnapshot1 -> {
//                if (documentSnapshot1.exists()) {
//                    experienceModel = documentSnapshot1.toObject(ExperienceModel.class);
//                    assert experienceModel != null;
//                    Log.i(TAG, experienceModel.getName());
//                    //Log.i(TAG, "The exp name: " + getExperienceName());
//
//                    holder.mInvitee.setText(String.format("Waiting for %s to accept your invite to " + experienceModel.getName(), dateModel.getDateInvitee()));
//                    Log.i(TAG, "Date creator id: " + dateCreator.getId() + " " + mAuth.getCurrentUser().getUid() + " " + dateModel1.getDateInviteeId() + " " + dateModel1.getId());
//
//                    result = String.valueOf(DateUtils.getRelativeTimeSpanString(timeCreated.getSeconds() * 1000, currentTime().getSeconds() * 1000, 0));
//                    Log.i("AdapterPending", result);
//
//                    holder.timeCreated.setText(result);
//                }
//            });
//            //experience
//
//        } else {
//            Log.i(TAG, "invitee id: " + dateModel1.getDateInviteeId() + " " + mAuth.getCurrentUser().getUid());
//
////                        expRef.get().addOnSuccessListener(documentSnapshot1 -> {
////                            if (documentSnapshot1.exists()) {
////                                experienceModel = documentSnapshot1.toObject(ExperienceModel.class);
////                                assert experienceModel != null;
//
//            holder.mInvitee.setText(String.format("%s is inviting you to " + "experience", dateModel1.getParticipants().get(dateModel1.getDateCreator())));
//            holder.editInvite.setText(R.string.accept_invite);
//
//            //change /override default click listener function to "ACCEPT" invite function
//            holder.editInvite.setOnClickListener(v -> {
//                HashMap<String, Object> participantStatus = new HashMap<>();
//                participantStatus.put(dateModel1.getParticipants().get(dateModel1.getDateCreator()), ACCEPTED);
//                participantStatus.put(dateModel1.getParticipants().get(dateModel1.getDateInviteeId()), ACCEPTED); //put invitee
//
//                Log.i("Adapter Pending", DateCreated.userId + "Now accepted");
//                datesRef.update(participantStatus);
//            });
//            holder.cancelInvite.setText(R.string.reject_invite);
//
//            result = String.valueOf(DateUtils.getRelativeTimeSpanString(timeCreated.getSeconds() * 1000, currentTime().getSeconds() * 1000, 0));
//            Log.i("AdapterPending", result);
//
//            holder.timeCreated.setText(result);
////                            }
////                        });


//        datesRef.get().addOnSuccessListener(datedocumentSnapshot -> {
//            if (datedocumentSnapshot.exists()) {
//                DateModel dateModel1 = datedocumentSnapshot.toObject(DateModel.class);
//                assert dateModel1 != null;
//                Timestamp timeCreated = dateModel1.getTimeCreated();
//
//                result = String.valueOf(DateUtils.getRelativeTimeSpanString(timeCreated.getSeconds() * 1000, currentTime().getSeconds() * 1000, 0));
//                Log.i("AdapterPending", result);
//
//                holder.timeCreated.setText(result);
//            }
//        });

    }


}
