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

package com.immersia_ltd_datenight.views.datehub_navigation.ui_fragments.dates.post_date

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import com.immersia_ltd_datenight.DatabaseConstants
import com.immersia_ltd_datenight.IntentConstants
import com.immersia_ltd_datenight.modelfirestore.User.UserModel
import com.immersia_ltd_datenight.views.datehub_navigation.DateHubNavigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class  DateFinishedViewModel : ViewModel() {
    private val dbReference = FirebaseFirestore.getInstance()
    var userRating: Int? = null
    var mAuth : FirebaseAuth? = null
    lateinit var userDocRef: DocumentReference


    init{
        mAuth = FirebaseAuth.getInstance()
    }

    fun launchRateDateNightActivity(parentContext: DateFinishedActivity){
        parentContext.startActivity(Intent(parentContext, RateDateNightActivity::class.java))
    }

    fun submitUserRating(dateId: String, dateParticipantId: String){
        if (userRating != null){
            // Update user's rating within the date node
            val data = hashMapOf(DatabaseConstants.USER_RATING_FIELD to userRating);
            dbReference.collection(DatabaseConstants.DATES_COLLECTION)
                    .document(dateId)
                    .collection(DatabaseConstants.STATISTICS_NODE)
                    .document(dateParticipantId)
                    .set(data); // TODO: Update to update

            // Update average date statistics rating
            // TODO: Implement

        }
        userRating = null
    }

    fun backToDateHubFragment(parentContext: DateFinishedActivity){
        val intent = Intent(parentContext, DateHubNavigation::class.java)
                .putExtra(IntentConstants.FRAGMENT_TO_LOAD, IntentConstants.DATE_HUB_FRAGMENT)
        //Increment date count
        incrementDateCount()
        parentContext.startActivity(intent)
    }

    fun backToInboxFragment(parentContext: DateFinishedActivity){
        val intent = Intent(parentContext, DateHubNavigation::class.java)
                .putExtra(IntentConstants.FRAGMENT_TO_LOAD, IntentConstants.INBOX_FRAGMENT)
        parentContext.startActivity(intent)
    }


    private fun incrementDateCount(){
        userDocRef = dbReference.collection(DatabaseConstants.USER_DATA_COLLECTION).document(mAuth!!.currentUser!!.uid)
        userDocRef.get().addOnSuccessListener { documentSnapshot ->
            if(documentSnapshot.exists()){
                val user =  documentSnapshot.toObject(UserModel::class.java)
                Log.i("DateFinishedVM","Date count before date: ${user!!.getAvgDateStats().dateCount}")

                var currentDateCount: Int = user.getAvgDateStats().dateCount
                val updateDateCount = currentDateCount++
                Log.i("DateFinishedVM","Date count after date: $updateDateCount")


                userDocRef.update("avgDateStats.rating",updateDateCount).addOnSuccessListener {
                    Log.i("DateFinishedVM","Dates been on updated")
                }

            }
        }
    }

}