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

package com.ltd_immersia_datenight.views.datehub_navigation.ui_fragments.dates.post_date

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.ltd_immersia_datenight.utils.constants.DatabaseConstants
import com.ltd_immersia_datenight.utils.constants.IntentConstants
import com.ltd_immersia_datenight.modelfirestore.User.UserModel
import com.ltd_immersia_datenight.views.datehub_navigation.DateHubNavigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlin.math.round

class  DateFinishedViewModel : ViewModel() {
    private val dbReference = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    var userRating: Int? = null
    var mAuth : FirebaseAuth? = null
    lateinit var userDocRef: DocumentReference


    init{
        mAuth = FirebaseAuth.getInstance()
    }

    fun launchRateDateNightActivity(parentContext: DateFinishedActivity){
        //TODO: Uncomment
        val intent = Intent(parentContext, RateDateNightActivity::class.java)
        //        .putExtra(IntentConstants.EXPERIENCE_ID, parentContext.dateExperienceId)
        parentContext.startActivity(intent)
    }

    fun submitUserRating(dateId: String, dateParticipantId: String, parentContext: DateFinishedActivity){
        // Update other user date statistics
        val data: Map<String, Any?> = mapOf(DatabaseConstants.DATE_COMPLETED_TIME_FIELD to Timestamp.now(),
                                            DatabaseConstants.LINKED_EXPERIENCE_ID to parentContext.dateExperienceId,
                                            DatabaseConstants.RATING_FIELD to userRating,
                                            DatabaseConstants.KISS_COUNT to parentContext.kissCount
        )

        if (userRating != null){
            data.plus(DatabaseConstants.USER_RATING_FIELD to userRating)
        }

        dbReference.collection(DatabaseConstants.USER_DATA_NODE)
                .document(dateParticipantId)
                .collection(DatabaseConstants.DATES_COLLECTION)
                .document(dateId)
                .collection(DatabaseConstants.STATISTICS_NODE)
                .document(dateParticipantId)
                .set(data, SetOptions.merge())

        // Update other user average stats
        val userRefDoc = dbReference.collection(DatabaseConstants.USER_DATA_NODE)
                .document(dateParticipantId)
        if (userRating != null){
            userRefDoc.get().addOnSuccessListener { documentSnapshot ->
                var numRatedDates = 1
                if (documentSnapshot.get(DatabaseConstants.AVG_DATE_STATS_DOC) != null && documentSnapshot.get("dateCount") as Int > 0){
                    val user = documentSnapshot.toObject(UserModel::class.java)
                    numRatedDates = user!!.avgDateStats.numRatedDates + 1
                    var rating = user.avgDateStats.rating.toDouble()
                    rating -= rating/ numRatedDates
                    rating += userRating!! / numRatedDates
                    rating = round(rating)
                    // TODO: Double-check this works as expected
                    val dataAvgStats = mapOf(DatabaseConstants.AVG_DATE_STATS_DOC to
                            mapOf(DatabaseConstants.NUM_RATED_DATES to numRatedDates,
                            DatabaseConstants.RATING_FIELD to rating)
                    )
                    userRefDoc.set(dataAvgStats, SetOptions.merge())
                } else {
                    val dataAvgStats = mapOf(DatabaseConstants.AVG_DATE_STATS_DOC to
                            mapOf(DatabaseConstants.NUM_RATED_DATES to numRatedDates,
                            DatabaseConstants.RATING_FIELD to userRating)
                    )
                    userRefDoc.set(dataAvgStats, SetOptions.merge())
                }
                userRating = null
            }

        } else {
            userRating = null
        }
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


    fun incrementDateCount(){
        userDocRef = dbReference.collection(DatabaseConstants.USER_DATA_NODE).document(mAuth!!.currentUser!!.uid)
        userDocRef.get().addOnSuccessListener { documentSnapshot ->
            if(documentSnapshot.exists()){
                val user =  documentSnapshot.toObject(UserModel::class.java)
                Log.i("DateFinishedVM","Date count before date: ${user!!.avgDateStats.dateCount}")

                var currentDateCount: Int = user.avgDateStats.dateCount
                val updateDateCount = currentDateCount+1
                Log.i("DateFinishedVM","Date count after date: $updateDateCount")


                userDocRef.update("avgDateStats.dateCount",updateDateCount).addOnSuccessListener {
                    Log.i("DateFinishedVM","Dates been on updated")
                }

            }
        }
    }

}