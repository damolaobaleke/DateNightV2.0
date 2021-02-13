package com.immersia_ltd_datenight.views.datehub_navigation.ui_fragments.dates.post_date

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.immersia_ltd_datenight.R
import kotlinx.android.synthetic.main.activity_rate_date_night.*


class RateDateNightActivity : AppCompatActivity() {
    // Views
    private lateinit var ratingStarArray: List<ImageView>
    private lateinit var positiveFeedbackEditText: EditText
    private lateinit var negativeFeedbackEditText: EditText
    // Data
    private var starRating = 0;
    private lateinit var viewModel: RateDateNightViewModel;
    // Intent extras
    private lateinit var dateExperienceId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rate_date_night)
        setSupportActionBar(findViewById(R.id.rateDateNightToolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        ratingStarArray = listOf(findViewById(R.id.star1),
                findViewById(R.id.star2),
                findViewById(R.id.star3),
                findViewById(R.id.star4),
                findViewById(R.id.star5)
        )
        positiveFeedbackEditText = findViewById(R.id.positiveReviewEditText)
        negativeFeedbackEditText = findViewById(R.id.negativeReviewEditText)

        viewModel = ViewModelProvider(this).get(RateDateNightViewModel::class.java)
    }

    fun setRatingOnStarClick(v: View){
        when(v.id){
            R.id.star1 -> {
                updateStars(0, starRating)
                starRating = 1
            }
            R.id.star2 -> {
                updateStars(1, starRating)
                starRating = 2
            }
            R.id.star3 -> {
                updateStars(2, starRating)
                starRating = 3
            }
            R.id.star4 -> {
                updateStars(3, starRating)
                starRating = 4
            }
            R.id.star5 -> {
                updateStars(4, starRating)
                starRating = 5
            }
        }

    }

    // Fills stars up to fillUpToIdx, then clears stars up to clearToIdx
    private fun updateStars(fillUpToIdx: Int, clearUpToIdx: Int){
        for(i in ratingStarArray.indices){
            if (i in 0 .. fillUpToIdx){
                ratingStarArray[i].setImageResource(R.drawable.ic_closed_star)
            } else if (i in (fillUpToIdx + 1 until clearUpToIdx)) {
                ratingStarArray[i].setImageResource(R.drawable.ic_open_star)
            }
        }
    }

    fun onSubmit(v: View){
        // TODO: Implement
        val positiveReview = positiveReviewEditText.text.toString().trim()
        val negativeReview = negativeFeedbackEditText.text.toString().trim()
        if (starRating > 0 || positiveReview.isNotBlank() || negativeReview.isNotBlank()){
            viewModel.addRating(dateExperienceId,
                                starRating,
                                positiveReviewEditText.text.toString(),
                                negativeFeedbackEditText.text.toString()
            )
        }
    }

    @Override
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}