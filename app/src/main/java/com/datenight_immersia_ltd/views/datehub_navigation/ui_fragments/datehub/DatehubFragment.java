package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.datehub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.datenight_immersia_ltd.IntentConstants;
import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.views.datehub_navigation.DateHubNavigation;
import com.datenight_immersia_ltd.views.readyplayerweb.CreatAvatarActivity;

import java.text.NumberFormat;
import java.util.Locale;

public class DatehubFragment extends Fragment implements View.OnClickListener {
    ProgressBar ratings;
    TextView averageDateRatingText;
    TextView dtcBalance;
    TextView datesBeenOn;
    TextView kissesReceived;
    Button topUpDtc;
    TextView editAvatarBtn;


    int progressLevel1 = 20;
    int progressLevel2 = 40;
    int progressLevel3 = 60;
    int progressLevel4 = 80;
    int progressLevel5 = 100;

    DateHubFragmentViewModel dateHubFragmentViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_hub, container, false);

        dateHubFragmentViewModel = new ViewModelProvider(this).get(DateHubFragmentViewModel.class);

        ratings = view.findViewById(R.id.progressBar);
        averageDateRatingText = view.findViewById(R.id.avgDateRating);
        dtcBalance = view.findViewById(R.id.dtc_balance);
        datesBeenOn = view.findViewById(R.id.datesBeenOn);
        kissesReceived = view.findViewById(R.id.kissesReceived);
        topUpDtc = view.findViewById(R.id.top_up_btn);
        editAvatarBtn = view.findViewById(R.id.edit_avatar_btn);

        //Average Date Rating
        //Store in DB
        //GET from DB Snapshot

        if(ratings.getProgress() == 0){
            ratings.setForeground(ContextCompat.getDrawable(requireContext(), R.color.date_night_grey));
        }

        dateHubFragmentViewModel.getAvgDateRating().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer avgDateRating) {
                Log.i("Datehub Frag", "AverageDateR: "+ avgDateRating.toString());
                ratings.setProgress(0);
                averageDateRatingText.setText(avgDateRating.toString());

                if (avgDateRating == 1.0 || avgDateRating == 1.5) {
                    ratings.setProgress(progressLevel1);
                } else if (avgDateRating == 2.0 || avgDateRating == 2.5) {
                    ratings.setProgress(progressLevel2);
                } else if (avgDateRating == 3.0 || avgDateRating == 3.5) {
                    ratings.setProgress(progressLevel3);
                } else if (avgDateRating == 4.0 || avgDateRating == 4.5) {
                    ratings.setProgress(progressLevel4);
                } else if (avgDateRating == 5.0) {
                    ratings.setProgress(progressLevel5);
                }
            }
        });

        //dtc balance
        dateHubFragmentViewModel.getDtcBalance().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(Integer dtcValue) {
                //TODO: format string
                int num = Integer.parseInt(dtcValue.toString());
                String formattedDtcBalance = NumberFormat.getInstance(Locale.US).format(num);

                Log.i("DateHub Frag", dtcValue.toString());
                dtcBalance.setText(formattedDtcBalance);
            }
        });

        //kisses received
        dateHubFragmentViewModel.getKissesReceived().observe(getViewLifecycleOwner(), kissesReceivedVal -> kissesReceived.setText(kissesReceivedVal.toString()));

        //dates been on
        dateHubFragmentViewModel.getDatesBeenOn().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(Integer datesBeenOnVal) {
                datesBeenOn.setText(datesBeenOnVal.toString());
            }
        });

        //Top up
        topUpDtc.setOnClickListener(v -> {
//            Fragment buyDtc = new DateNightCoinFragment();
//            getActivity().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.nav_host_fragment, buyDtc, "finddtc")
//                    .addToBackStack(null)
//                    .commit();
            Intent intent = new Intent(requireContext(), DateHubNavigation.class);
            intent.putExtra(IntentConstants.FRAGMENT_TO_LOAD, IntentConstants.BUY_DTC_FRAGMENT);
            startActivity(intent);
        });

        //EditAvatar
        editAvatarBtn.setOnClickListener(this);


        return view;

    }

    public void startReadyPlayerMeLink() {
        Intent intent = new Intent(requireContext(), CreatAvatarActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        editAvatarBtn.setOnClickListener(j -> startReadyPlayerMeLink());
    }

    public void usingRatingBar() {
        //RatingBar stars;
        //stars = view.findViewById(R.id.dateRatingBar);
//        stars.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
//            averageDateRatingText.setText(getString(R.string.average_date_rating) + " " + stars.getRating() + "/" + stars.getNumStars());
//
//            float x = ratingBar.getRating(); //gets the current rating(number of stars filled)
//        });
    }
}
