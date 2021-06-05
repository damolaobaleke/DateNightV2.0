package com.ltd_immersia_datenight.views.datehub_navigation.ui_fragments.premium;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.ltd_immersia_datenight.R;
import com.ltd_immersia_datenight.views.date_schedule.DateScheduleActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PremiumFragment extends Fragment {

    private PremiumModel premiumViewModel;
    private CardView loveIntheClouds;
    TextView experienceName;
    TextView price;
    ImageView expImage;
    ProgressBar progressBarLove;
    String environmentVideoUrl;
    String experienceDescription;
    private final static String TAG = "Premium Fragment";


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Instance of ViewModel
        premiumViewModel = new ViewModelProvider(this).get(PremiumModel.class);

        View view = inflater.inflate(R.layout.fragment_premium, container, false);

        //binding
        loveIntheClouds = view.findViewById(R.id.cap_balloon_ride);
        experienceName = view.findViewById(R.id.experienceName);
        expImage = view.findViewById(R.id.love_in_clouds);
        price = view.findViewById(R.id.price);
        progressBarLove = view.findViewById(R.id.progressBarLove);

        loveIntheClouds.setCardElevation(1000f);


        premiumViewModel.getExpName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                experienceName.setText(s);
            }
        });

        premiumViewModel.getPrice().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer priceOfDate) {
                price.setText(priceOfDate.toString() + " ");
            }
        });

        premiumViewModel.getEnvironmentVideoUrl().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String videoUrl) {
                environmentVideoUrl = videoUrl;
                Log.i(TAG, environmentVideoUrl);
            }
        });

        premiumViewModel.getExperienceDescription().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String description) {
                experienceDescription = description;
            }
        });

        Picasso.get().load(premiumViewModel.environmentImage.getValue()).into(expImage, new Callback() {
            @Override
            public void onSuccess() {
                progressBarLove.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                progressBarLove.setVisibility(View.VISIBLE);
                Log.e(TAG, e.getMessage());
            }
        });


        loveIntheClouds.setOnClickListener(v -> startLoveInTheClouds());


        return view;
    }

    public void startLoveInTheClouds() {
        Intent intent = new Intent(requireContext(), DateScheduleActivity.class);
        intent.putExtra("experienceName", experienceName.getText());//OR experienceModel.getName()
        intent.putExtra("experienceCost", price.getText());
        intent.putExtra("experienceDesc", experienceDescription);
        intent.putExtra("videoUrl", environmentVideoUrl);
        startActivity(intent);
    }
}