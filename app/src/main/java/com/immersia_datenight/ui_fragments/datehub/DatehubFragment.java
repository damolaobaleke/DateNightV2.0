package com.immersia_datenight.ui_fragments.datehub;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.immersia_datenight.R;

public class DatehubFragment extends Fragment {
    ProgressBar ratings;
    RatingBar stars;
    TextView averageDateRatingText;
    int progressLevel1 = 20;
    int progressLevel2 = 40;
    int progressLevel3 = 60;
    int progressLevel4 = 80;
    int progressLevel5 = 100;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_hub, container, false);

        ratings = view.findViewById(R.id.progressBar);
        stars = view.findViewById(R.id.dateRatingBar);
        averageDateRatingText = view.findViewById(R.id.avgtext);

        //Average Date Rating
        //Total amount of dates gone on with ratings/ total amounts of dates gone on
        //Store in DB
        //GET from DB Snapshot

        stars.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            averageDateRatingText.setText(getString(R.string.average_date_rating) +" "+stars.getRating() + "/" + stars.getNumStars());

            float x = ratingBar.getRating(); //gets the current rating(number of stars filled)

            if (x == 1.0 || x == 1.5) {
                ratings.setProgress(progressLevel1);
            } else if (x == 2.0 || x == 2.5) {
                ratings.setProgress(progressLevel2);
            } else if (x == 3.0 || x == 3.5) {
                ratings.setProgress(progressLevel3);
            } else if (x == 4.0 || x == 4.5) {
                ratings.setProgress(progressLevel4);
            } else if (ratingBar.getRating() == 5.0) {
                ratings.setProgress(progressLevel5);
            }
        });



        return view;

    }
}
