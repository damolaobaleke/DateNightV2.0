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

package com.immersia_ltd_datenight.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.immersia_ltd_datenight.R;
import com.immersia_ltd_datenight.modelfirestore.Date.DateModel;
import com.immersia_ltd_datenight.modelfirestore.Experience.ExperienceModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RecyclerViewAdapterScheduled extends RecyclerView.Adapter<RecyclerViewAdapterScheduled.ScheduledDatesViewHolder> {
    private final ArrayList<DateModel> mDateLists;
    DocumentReference datesRef,expDocRef;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String creatorKey;
    String inviteeKey;

    DateModel date;


    //Listeners
    private OnItemClickListener mListener;

    //interface for listener
    public interface OnItemClickListener {
        //methods on click
        void onItemClick(int position);

        void onCancelInvite(int position);

        void onStartDate(int position);
    }

    //click listener method
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    //Listeners

    //describes position or metadata of each itemview on the recycler view
    public static class ScheduledDatesViewHolder extends RecyclerView.ViewHolder {
        TextView dateTitle;
        TextView dayChosen;
        TextView timeChosen;
        ImageView avatar;


        public ScheduledDatesViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            dateTitle = itemView.findViewById(R.id.dateTitle);
            dayChosen = itemView.findViewById(R.id.dateDate);
            timeChosen = itemView.findViewById(R.id.dateTime);
            avatar = itemView.findViewById(R.id.dateImage);

            //onItemPosClicked
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) { //make sure position is valid on recycler view, there is a position
                            listener.onItemClick(position); //pass r adapter position to interface method
                        }
                    }
                }
            });
            //onItemPosClicked

            //On cancel invite clicked
//            cancelInvite.setOnClickListener(v -> {
//                if (listener != null) {
//                    int position = getAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION) { //make sure position is valid on recycler view, there is a position
//                      listener.onCancelInvite(position); //pass r adapter position to interface method
//                    }
//                }
//            });
            //On cancel invite clicked

        }
    }
    //

    //Constructor to get data of ArrayList of dates into Adapter
    public RecyclerViewAdapterScheduled(ArrayList<DateModel> mDateLists) {
        this.mDateLists = mDateLists;
    }

    @NonNull
    @Override
    public ScheduledDatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_dates_sch_list_view, parent, false);

        return new ScheduledDatesViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduledDatesViewHolder holder, int position) {
        DateModel dateModel = mDateLists.get(position); //position in recy view for datelist

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        datesRef = db.collection("dates").document(dateModel.getId());

        datesRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {

                date = documentSnapshot.toObject(DateModel.class);
                assert date != null;


                for (String key : date.getParticipants().keySet()) {
                    if (key.equals(mAuth.getCurrentUser().getUid()) && key.equals(date.getCreator())) {
                        creatorKey = key;
                    } else {
                        inviteeKey = key;
                    }
                }

                if (!dateModel.getCreator().equals(mAuth.getCurrentUser().getUid())) { //so invitee

                    expDocRef = db.collection("experiences").document(date.getLinkedExperienceId());

                    expDocRef.get().addOnSuccessListener(expdocumentSnapshot -> {
                        if (expdocumentSnapshot.exists()) {
                            ExperienceModel experience = expdocumentSnapshot.toObject(ExperienceModel.class);
                            assert experience != null;


                            holder.dateTitle.setText(String.format("Scheduled %s with %s", experience.getName(), dateModel.getParticipants().get(creatorKey)));
                        }
                    });


                    holder.avatar.setImageResource(R.drawable.avatar_ellipse);
                    if (dateModel.getDateTime() != null) {
                        holder.dayChosen.setText(dateModel.getDateTime().toString());

                        long daySecondsToDate = dateModel.getDateTime().getSeconds() / 60 * 60; //divide by the hr to get day
                        holder.timeChosen.setText(String.valueOf(daySecondsToDate));
                    } else {
                        holder.dayChosen.setText("Day");
                        holder.timeChosen.setText("Time");
                    }

                }else{
                    //creator
                    if(date.getLinkedExperienceId() != null) {
                        expDocRef = db.collection("experiences").document(date.getLinkedExperienceId());

                        expDocRef.get().addOnSuccessListener(expdocumentSnapshot -> {
                            if (expdocumentSnapshot.exists()) {
                                ExperienceModel experience = expdocumentSnapshot.toObject(ExperienceModel.class);
                                assert experience != null;
                                //holder.dateTitle.setText(String.format("%s with %s", experience.getName(), dateModel.getParticipants().get(inviteeKey)));
                            }
                        });
                    }
                }
            }

        });


    }

    @Override
    public int getItemCount() {
        return mDateLists.size();
    }

}
