/*
 * Copyright 2021 Damola Obaleke. All rights reserved.
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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.modelfirestore.Date.DateModel;
import com.datenight_immersia_ltd.modelfirestore.Experience.ExperienceModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ScheduledAdapter extends FirestoreRecyclerAdapter<DateModel, ScheduledAdapter.ScheduledViewHolder> {
    DocumentReference datesRef;
    DocumentReference userCreatorRef, dateInviteeRef, expRef;
    CollectionReference experienceCollRef, userCollRef;
    ExperienceModel experienceModel;
    DateModel mDateModel;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    String inviteeKey;
    String creatorKey;

    protected final static String PENDING = "PENDING";
    protected final static String ACCEPTED = "ACCEPTED";

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ScheduledAdapter(@NonNull FirestoreRecyclerOptions<DateModel> options) {
        super(options);
    }

    @NonNull
    @Override
    public ScheduledViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_dates_sch_list_view, parent, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        userCreatorRef = db.collection("userData").document(mAuth.getCurrentUser().getUid());

        return new ScheduledViewHolder(view, mListener);
    }

    @Override
    protected void onBindViewHolder(@NonNull ScheduledViewHolder holder, int position, @NonNull DateModel model) {

        /*expRef = db.collection("experiences").document(model.getLinkedExperienceId());
        expRef.get().addOnSuccessListener(documentSnapshot -> {
            experienceModel = documentSnapshot.toObject(ExperienceModel.class);

        });*/

        holder.avatar.setImageResource(R.drawable.avatar_ellipse);

        for (String key : model.getParticipants().keySet()) {
            if (key.equals(model.getCreator())) {
                creatorKey = key;
            } else {
                inviteeKey = key;
            }
        }

        if (!model.getCreator().equals(mAuth.getCurrentUser().getUid()) && model.getParticipantStatus().get(inviteeKey).equals(ACCEPTED)) { //invitee
            holder.dateTitle.setText(String.format("Scheduled %s with %s", experienceModel.getName(), model.getParticipants().get(creatorKey)));
            holder.dayChosen.setText(model.getDateTime().toString());

            long daySecondsToDate = model.getDateTime().getSeconds() / 60 * 60; //divide by the hr to get day
            holder.timeChosen.setText(String.valueOf(daySecondsToDate));

        } else if (model.getParticipantStatus().get(inviteeKey).equals(ACCEPTED) && model.getCreator().equals(mAuth.getCurrentUser().getUid())) {
            holder.dateTitle.setText(String.format("Scheduled %s with %s", experienceModel.getName(), model.getParticipants().get(inviteeKey)));

        }
    }



    //describes position or metadata of each itemview on the recycler view
    public static class ScheduledViewHolder extends RecyclerView.ViewHolder {
        TextView dateTitle;
        TextView dayChosen;
        TextView timeChosen;
        ImageView avatar;


        public ScheduledViewHolder(@NonNull View itemView, ScheduledAdapter.OnItemClickListener listener) {
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
                            listener.onItemClick(position); //pass recyler view adapter position to interface method
                        }
                    }
                }
            });
            //onItemPosClicked


        }
    }

    //click listeners to listen on recycler view child view buttons for pendingAdapter
    private ScheduledAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(ScheduledAdapter.OnItemClickListener listener) {
        mListener = listener;
    }
    //
}
