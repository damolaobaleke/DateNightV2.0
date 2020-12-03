package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.datehub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.buy_dtc.DateNightCoinFragment;
import com.datenight_immersia_ltd.views.readyplayerweb.CreatAvatarActivity;

public class DatehubFragment extends Fragment implements View.OnClickListener {
    ProgressBar ratings;
    RatingBar stars;
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
//        stars = view.findViewById(R.id.dateRatingBar);
        averageDateRatingText = view.findViewById(R.id.avgtext);
        dtcBalance = view.findViewById(R.id.dtc_balance);
        datesBeenOn = view.findViewById(R.id.datesBeenOn);
        kissesReceived = view.findViewById(R.id.kissesReceived);
        topUpDtc = view.findViewById(R.id.top_up_btn);
        editAvatarBtn = view.findViewById(R.id.edit_avatar_btn);

        //Average Date Rating
        //Total amount of dates gone on with ratings/ total amounts of dates gone on
        //Store in DB
        //GET from DB Snapshot

//        stars.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
//            averageDateRatingText.setText(getString(R.string.average_date_rating) + " " + stars.getRating() + "/" + stars.getNumStars());
//
//            float x = ratingBar.getRating(); //gets the current rating(number of stars filled)
//
//            if (x == 1.0 || x == 1.5) {
//                ratings.setProgress(progressLevel1);
//            } else if (x == 2.0 || x == 2.5) {
//                ratings.setProgress(progressLevel2);
//            } else if (x == 3.0 || x == 3.5) {
//                ratings.setProgress(progressLevel3);
//            } else if (x == 4.0 || x == 4.5) {
//                ratings.setProgress(progressLevel4);
//            } else if (ratingBar.getRating() == 5.0) {
//                ratings.setProgress(progressLevel5);
//            }
//        });

        //dtc balance
        dateHubFragmentViewModel.getDtcBalance().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(Integer dtcValue) {
                dtcBalance.setText(dtcValue.toString());
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
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.drawer_layout, new DateNightCoinFragment()).addToBackStack(null).commit();
        });

        //EditAvatar
        editAvatarBtn.setOnClickListener(this);


        return view;

    }

    public void buyDtc() {
        topUpDtc.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new DateNightCoinFragment()).addToBackStack(null).commit();
        });
    }

    public void startReadyPlayerMeLink() {
        Intent intent = new Intent(requireContext(), CreatAvatarActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        editAvatarBtn.setOnClickListener(j->startReadyPlayerMeLink());
    }
}
