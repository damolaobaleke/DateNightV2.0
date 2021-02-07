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

package com.immersia_ltd_datenight.modelfirestore;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.immersia_ltd_datenight.DatabaseConstants;
import com.immersia_ltd_datenight.modelfirestore.Experience.ExperienceModel;
import com.immersia_ltd_datenight.modelfirestore.User.UserModel;
import com.immersia_ltd_datenight.views.datehub_navigation.DateHubNavigation;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;

public class DateNightAppData {
    // Firebase vars
    private FirebaseFirestore dbFirestore;
    private String currentUserId; // Holds the id of the user who owns the data in instance of class
    // Data to be persisted
    private UserModel currentUserModel;
    private HashMap<String, ExperienceModel> experiencesData = new HashMap<String, ExperienceModel>();
    // Util data
    private final static String TAG = "RetrieveUserAndExperienceData";

    public DateNightAppData (String currentUserId, Context launchContext){
        this.currentUserId = currentUserId;
        if (currentUserId != null){
            dbFirestore = FirebaseFirestore.getInstance();
            dbFirestore.collection(DatabaseConstants.USER_DATA_NODE).document(currentUserId).get().addOnSuccessListener(userDocumentSnapshot -> {

                // Grab user data, upon success, grab experiences
                if (userDocumentSnapshot.exists()){
                    currentUserModel = userDocumentSnapshot.toObject(UserModel.class);

                    // Grab experiences as well
                    dbFirestore.collection(DatabaseConstants.EXPERIENCE_NODE).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            for(QueryDocumentSnapshot document: task.getResult()){
                                ExperienceModel exp = document.toObject(ExperienceModel.class);
                                experiencesData.put(exp.getId(), exp);
                            }
                            Log.e(TAG, this.toString());

                            //Launch DateHub navigation activity
                            Intent intent = new Intent(launchContext, DateHubNavigation.class);
                            launchContext.startActivity(intent);
                        } else {
                            //TODO: Error out
                            Log.e(TAG, "Unable to get experiences data from Firestore");
                        }
                    });
                }
                else {
                    //TODO: error out
                    Log.e(TAG, "Unable to get user data from Firestore");
                }
            });
        }
    }

    /*
    Method exist to prevent ever returning data of another user
     */
    public boolean validateUserAccessingData(String userId){
        boolean rVal = true;
        if (currentUserId != null && !currentUserId.equals(userId)) {
            rVal = false;
        }
        return rVal;
    }

    public UserModel getCurrentUser(){
        return currentUserModel;
    }
    public ExperienceModel getExperience (String id){
        return experiencesData.get(id);
    }

    public String getExperienceName (String expId){
        return experiencesData.get(expId).getName();
    }

    public HashMap<String, ExperienceModel> getExperiencesData(){
        return experiencesData;
    }

    public String toString(){
        String theString = "User Id: " + currentUserModel.getId();
        theString += "/nExpereiences: ";
        for (String expId: experiencesData.keySet()){
            theString += "/n" + experiencesData.get(expId).getName();
        }
        return theString;
    }
}
