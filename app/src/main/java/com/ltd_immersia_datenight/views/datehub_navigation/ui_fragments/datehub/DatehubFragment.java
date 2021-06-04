package com.ltd_immersia_datenight.views.datehub_navigation.ui_fragments.datehub;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ltd_immersia_datenight.utils.constants.IntentConstants;
import com.ltd_immersia_datenight.R;
import com.ltd_immersia_datenight.views.datehub_navigation.DateHubNavigation;
import com.ltd_immersia_datenight.views.readyplayerweb.CreateAvatarInstructionsActivity;

import static java.lang.Math.round;

public class DatehubFragment extends Fragment implements View.OnClickListener {
    ProgressBar ratings;
    TextView averageDateRatingText;
    TextView dtcBalance;
    TextView datesBeenOn;
    TextView kissesReceived;
    Button topUpDtc;
    TextView editAvatarBtn;

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

        dateHubFragmentViewModel
                .initializeViews(dtcBalance, datesBeenOn, averageDateRatingText, ratings, kissesReceived);

//        //dtc balance
//        dateHubFragmentViewModel.getDtcBalance().observe(getViewLifecycleOwner(), new Observer<Integer>() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onChanged(Integer dtcValue) {
//                int num = Integer.parseInt(dtcValue.toString());
//                String formattedDtcBalance = NumberFormat.getInstance(Locale.US).format(num);
//
//                Log.i("DateHub Frag", dtcValue.toString());
//                dtcBalance.setText(formattedDtcBalance);
//            }
//        });

        //Top up
        topUpDtc.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), DateHubNavigation.class);
            intent.putExtra(IntentConstants.FRAGMENT_TO_LOAD, IntentConstants.BUY_DTC_FRAGMENT);
            startActivity(intent);
        });

        //EditAvatar
        editAvatarBtn.setOnClickListener(this);

        return view;

    }

    public void startReadyPlayerMeLink() {
        // Launch activity that shows instructions for creating avatar
        Intent intent = new Intent(requireContext(), CreateAvatarInstructionsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        startReadyPlayerMeLink();
    }

    @Override
    public void onResume() {
        super.onResume();
        dateHubFragmentViewModel
                .initializeViews(dtcBalance, datesBeenOn, averageDateRatingText, ratings, kissesReceived);
    }
}