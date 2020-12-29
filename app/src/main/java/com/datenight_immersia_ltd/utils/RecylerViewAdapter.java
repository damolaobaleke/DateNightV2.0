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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.modelfirestore.User.UserModel;

import java.util.ArrayList;

public class RecylerViewAdapter extends RecyclerView.Adapter<RecylerViewAdapter.UserViewHolder> {
    private final ArrayList<UserModel> mUserList;

    private OnItemClickListener mListener;

    //listener to listen on recycler view item or items clicked
    public interface OnItemClickListener {
        void onInviteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    //

    // ViewHolder describes an item view and metadata about its place within the RecyclerView.
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView mUsername;
        ImageView mUserImage;
        public Button inviteButton;

        public UserViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            mUsername = itemView.findViewById(R.id.user_username);
            mUserImage = itemView.findViewById(R.id.user_image);
            inviteButton = itemView.findViewById(R.id.invite_btn);

            //On invite clicked
            inviteButton.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) { //make sure position is valid, there is a position
                        listener.onInviteClick(position); //pass r adapter position to interface method
                    }
                }
            });
            //On invite clicked
        }
    }

    //Constructor to get data of ArrayList of users into Adapter
    public RecylerViewAdapter(ArrayList<UserModel> userList) {
        mUserList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //root is the view group here
        //child view not added to parent by us
        View childView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_search_item, parent, false);
        return new UserViewHolder(childView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        //update contents with item at given position and get specific item position
        UserModel currentUser = mUserList.get(position);
        Log.i("Adapter User", currentUser.getId() +" "+currentUser.getUsername());

        holder.mUserImage.setImageResource(R.drawable.avatar_ellipse); //set user avatar pic in spec pos >>currentUser.getAvatar()
        if (currentUser.getUsername() == null) { //currentUser.getUsername().length() <= 0 ||
            holder.mUsername.setText(R.string.no_user);
        } else {
            holder.mUsername.setText(currentUser.getUsername());
        }
    }

    @Override
    public int getItemCount() {
        //return amount of items in list
        return mUserList.size();
    }


}
