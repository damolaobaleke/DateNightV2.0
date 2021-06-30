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
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.ltd_immersia_datenight.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.ltd_immersia_datenight.utils.constants.IntentConstants
import com.ltd_immersia_datenight.utils.DateNight
import com.ltd_immersia_datenight.views.unity.UnityEnvironmentLoad
import de.hdodenhof.circleimageview.CircleImageView

class DateFinishedActivity : AppCompatActivity() {
    //Views
    private lateinit var viewModel: DateFinishedViewModel
    private lateinit var howWasDateTextView: TextView
    private lateinit var emojiRatingsCircleImageViewArray: List<CircleImageView>
    private lateinit var submitUserRatingButton: Button
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetDialogView: View
    // App Data
    private lateinit var appState: DateNight
    // Extras
    private val currentUserId = FirebaseAuth.getInstance().uid
    private lateinit var dateParticipantId: String
    private lateinit var dateParticipantName: String
    lateinit var dateExperienceId: String
    var kissCount: Int? = null
    private lateinit var dateId: String
    // Data
    private var emojiRating: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date_finished)

        bottomSheetDialog = BottomSheetDialog(this)
        viewModel = ViewModelProvider(this).get(DateFinishedViewModel::class.java)

        //Get user data
        appState = this.application as DateNight
        // Get intent extras
        dateParticipantId = intent.getStringExtra(IntentConstants.PARTICIPANT_ID_EXTRA)!!
        dateParticipantName = intent.getStringExtra(IntentConstants.PARTICIPANT_FULL_NAME_EXTRA)!!
        dateExperienceId = intent.getStringExtra(IntentConstants.EXPERIENCE_ID)!!
        dateId = intent.getStringExtra(IntentConstants.DATE_ID)!!
        kissCount = intent.getIntExtra(IntentConstants.DATE_KISS_COUNT, 0)!!


    }

    override fun onStop() {
        super.onStop()
        // TODO: Uncomment
        //viewModel.submitUserRating(dateId, dateParticipantId)
    }

    fun launchRateDateActivity(v: View){
       viewModel.launchRateDateNightActivity(this)
    }

    fun backToInboxFragment(v: View){
        //TODO: Update ratings and stuff
        viewModel.backToInboxFragment(this)
    }

    fun backToDateHubFragment(v: View){
        //TODO: Update ratings and stuff
        viewModel.backToDateHubFragment(this)
        viewModel.incrementDateCount()
    }

    fun repeatDate(v: View){
        //TODO: Update ratings and stuff
        val intent: Intent = Intent(this, UnityEnvironmentLoad::class.java)
                .putExtra(IntentConstants.USER_ID_EXTRA, currentUserId)
                .putExtra(IntentConstants.USER_FULL_NAME_EXTRA, appState.getAppData(currentUserId).currentUser.fullName)
                .putExtra(IntentConstants.DATE_ID, dateId)
                .putExtra(IntentConstants.EXPERIENCE_ID, dateExperienceId)
                .putExtra(IntentConstants.PARTICIPANT_ID_EXTRA, dateParticipantId)
                .putExtra(IntentConstants.PARTICIPANT_FULL_NAME_EXTRA, dateParticipantName)
        startActivity(intent)
    }

    fun unveilRateUserBottomSheet(v: View){
        // Show bottom sheet
        bottomSheetDialogView = layoutInflater.inflate(R.layout.custom_rate_user_bottomsheet, null)
        howWasDateTextView = bottomSheetDialogView.findViewById(R.id.how_was_date_textview)
        val titleText = "How was your date with $dateParticipantName?"
        howWasDateTextView.text = titleText
        submitUserRatingButton = bottomSheetDialogView.findViewById(R.id.submitUserRatingButton)
        emojiRatingsCircleImageViewArray = listOf(bottomSheetDialogView.findViewById(R.id.emoji_rating_1),
                bottomSheetDialogView.findViewById(R.id.emoji_rating_2),
                bottomSheetDialogView.findViewById(R.id.emoji_rating_3),
                bottomSheetDialogView.findViewById(R.id.emoji_rating_4),
                bottomSheetDialogView.findViewById(R.id.emoji_rating_5)
        )

        // Create and show dialog
        bottomSheetDialog.setContentView(bottomSheetDialogView)
        bottomSheetDialog.create()
        bottomSheetDialog.show()
    }

    fun setEmojiRatingOnClick(v: View){
        when(v.id){
            R.id.emoji_rating_1 -> {
                updateSelectedEmoji(0)
                emojiRating = 1
            }
            R.id.emoji_rating_2 -> {
                updateSelectedEmoji(1)
                emojiRating = 2
            }
            R.id.emoji_rating_3 -> {
                updateSelectedEmoji(2)
                emojiRating = 3
            }
            R.id.emoji_rating_4 -> {
                updateSelectedEmoji(3)
                emojiRating = 4
            }
            R.id.emoji_rating_5 -> {
                updateSelectedEmoji(4)
                emojiRating = 5
            }
        }

    }

     fun updateSelectedEmoji(selectedPosition: Int){
        if(viewModel.userRating == null ){
            // Highlight selected pos
            emojiRatingsCircleImageViewArray[selectedPosition].borderColor =  ContextCompat.getColor(this, R.color.date_night_purple)
            emojiRatingsCircleImageViewArray[selectedPosition].circleBackgroundColor =  ContextCompat.getColor(this, R.color.date_night_purple)
        } else {
            val currentSelectedPosition = viewModel.userRating!! - 1
            if (currentSelectedPosition != selectedPosition){
                // Update viewModel rating
                viewModel.userRating = selectedPosition + 1
                // Remove purple tint and border
                emojiRatingsCircleImageViewArray[currentSelectedPosition].borderColor =  ContextCompat.getColor(this, R.color.date_night_transparent)
                emojiRatingsCircleImageViewArray[currentSelectedPosition].circleBackgroundColor =  ContextCompat.getColor(this, R.color.date_night_transparent)
                // Highlight selected pos
                emojiRatingsCircleImageViewArray[selectedPosition].borderColor =  ContextCompat.getColor(this, R.color.date_night_purple)
                emojiRatingsCircleImageViewArray[selectedPosition].circleBackgroundColor =  ContextCompat.getColor(this, R.color.date_night_purple)
            }
        }
        viewModel.userRating = selectedPosition + 1
    }

    fun submitUserRatingOnClick(v: View){
        bottomSheetDialog.dismiss()
    }


    fun onCancel(v: View){
        bottomSheetDialog.dismiss()
        viewModel.userRating = null
    }


}