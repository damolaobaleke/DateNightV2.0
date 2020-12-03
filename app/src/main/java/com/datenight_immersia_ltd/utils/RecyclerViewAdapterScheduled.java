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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.modelfirestore.Date.DateModel;

import java.util.ArrayList;

public class RecyclerViewAdapterScheduled extends RecyclerView.Adapter<RecyclerViewAdapterScheduled.ScheduledDatesViewHolder> {
    private ArrayList<DateModel> mDateLists;


    //Listeners
    private OnItemClickListener mListener;

    //interface for listener
    public interface OnItemClickListener {
        //methods on click
        public void onItemClick(int position);

        public void onCancelInvite(int position);

        public void onStartDate(int position);
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_date_schedule_view, parent, false);

        return new ScheduledDatesViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduledDatesViewHolder holder, int position) {
        DateModel dateModel = mDateLists.get(position); //position in recy view for datelist

        holder.dateTitle.setText(dateModel.getPassword());
        holder.avatar.setImageResource(R.drawable.avatar_ellipse);
        holder.dayChosen.setText(dateModel.getDateTime().toString());

        long daySecondsToDate = dateModel.getDateTime().getSeconds()/60*60; //divide by the hr to get day
        holder.timeChosen.setText(String.valueOf(daySecondsToDate));
    }

    @Override
    public int getItemCount() {
        return mDateLists.size();
    }

}
