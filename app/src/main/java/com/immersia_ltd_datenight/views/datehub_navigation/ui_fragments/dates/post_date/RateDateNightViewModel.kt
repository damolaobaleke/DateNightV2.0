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

import android.util.Log
import androidx.lifecycle.ViewModel
import com.immersia_ltd_datenight.DatabaseConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RateDateNightViewModel: ViewModel() {
    private val dbReference = FirebaseFirestore.getInstance()
    private val currentUserId: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val TAG = "RateDateNightActivity"

    fun addRating(experienceId: String, numericalRating: Int, positiveFeedback: String, negativeFeedback: String) {
        if(currentUserId != null){
            val data = hashMapOf(
                    DatabaseConstants.RATING_FIELD to numericalRating,
                    DatabaseConstants.LIKED_TEXT_FIELD to positiveFeedback,
                    DatabaseConstants.DISLIKED_TEXT_FIELD to negativeFeedback
            )

            dbReference.collection(DatabaseConstants.FEEDBACK_NODE)
                    .document(experienceId)
                    .collection(DatabaseConstants.USERS_NODE)
                    .document(currentUserId)
                    .set(data)
                    .addOnFailureListener{ exception -> Log.e(TAG, exception.message!!) }
        }

    }
}