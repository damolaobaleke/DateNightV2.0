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

package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.dates.post_date

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Database
import com.datenight_immersia_ltd.DatabaseConstants
import com.datenight_immersia_ltd.IntentConstants
import com.datenight_immersia_ltd.views.datehub_navigation.DateHubNavigation
import com.google.firebase.firestore.FirebaseFirestore

class  DateFinishedViewModel : ViewModel() {
    private val dbReference = FirebaseFirestore.getInstance()
    var userRating: Int? = null

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
        parentContext.startActivity(intent)
    }

    fun backToInboxFragment(parentContext: DateFinishedActivity){
        val intent = Intent(parentContext, DateHubNavigation::class.java)
                .putExtra(IntentConstants.FRAGMENT_TO_LOAD, IntentConstants.INBOX_FRAGMENT)
        parentContext.startActivity(intent)
    }

}