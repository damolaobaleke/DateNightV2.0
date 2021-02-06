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

package com.immersia_ltd_datenight.utils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.immersia_ltd_datenight.R;
import com.immersia_ltd_datenight.modelfirestore.Date.DateModel;
import com.immersia_ltd_datenight.modelfirestore.Experience.ExperienceModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PendingAdapter extends FirestoreRecyclerAdapter<DateModel, PendingAdapter.PendingViewHolder> {
    DocumentReference userCreatorRef, dateInviteeRef, expRef;
    CollectionReference experienceCollRef, userCollRef;
    ExperienceModel experienceModel;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    String inviteeKey;
    String creatorKey;

    protected final static String PENDING = "PENDING";
    protected final static String ACCEPTED = "ACCEPTED";
    protected final static String TAG = "AdapterPending";

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PendingAdapter(@NonNull FirestoreRecyclerOptions<DateModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PendingViewHolder holder, int position, @NonNull DateModel model) {

        Log.i(TAG, "The exp id: " + model.getLinkedExperienceId());
        expRef = db.collection("experiences").document(model.getLinkedExperienceId());
        expRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                experienceModel = documentSnapshot.toObject(ExperienceModel.class);
                Log.i(TAG, "The experience " + experienceModel.getName());
            }
        });


        for (String key : model.getParticipants().keySet()) {
            if (key.equals(model.getCreator())) {
                creatorKey = key;
            } else {
                inviteeKey = key;
            }
        }

        if (model.getCreator().equals(mAuth.getCurrentUser().getUid()) && model.getParticipantStatus().get(inviteeKey).equals(PENDING)) {
            holder.inviteDescription.setText(String.format("Waiting for %s to accept your invite to ", model.getParticipants().get(inviteeKey)));
            holder.timeCreated.setText(model.getDateTime().toString());
        } else {
            holder.inviteDescription.setText(String.format("%s is inviting you to paris", model.getParticipants().get(creatorKey)));
            holder.cancelInvite.setText(R.string.reject_invite);
            holder.editInvite.setText(R.string.accept_invite);
        }
    }

    @NonNull
    @Override
    public PendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_dates_pending_lv, parent, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        userCreatorRef = db.collection("userData").document(mAuth.getCurrentUser().getUid());

        return new PendingViewHolder(view, mListener);
    }

    //click listeners to listen on recycler view child view buttons for pendingAdapter
    private PendingAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onCancelInvite(int position);

        void onEditInvite(int position);
    }

    public void setOnItemClickListener(PendingAdapter.OnItemClickListener listener) {
        mListener = listener;
    }
    //


    class PendingViewHolder extends RecyclerView.ViewHolder {
        public TextView inviteDescription;
        Button editInvite;
        Button cancelInvite;
        TextView timeCreated;

        public PendingViewHolder(@NonNull View itemView, PendingAdapter.OnItemClickListener listener) {
            super(itemView);

            inviteDescription = itemView.findViewById(R.id.invite_description);
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
}
