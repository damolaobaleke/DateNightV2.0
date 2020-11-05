package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.premium;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.datenight_immersia_ltd.R;

public class PremiumFragment extends Fragment {

    private PremiumModel premiumViewModel;
    private CardView paris;
    TextView experienceName;
    TextView price;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Instance of ViewModel
        premiumViewModel = new ViewModelProvider(this).get(PremiumModel.class);

        View view = inflater.inflate(R.layout.fragment_premium, container, false);

        paris = view.findViewById(R.id.paris);
        experienceName =view.findViewById(R.id.experienceName);
        paris.setCardElevation(1000f);


        premiumViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                experienceName.setText(s);
            }
        });

        premiumViewModel.getPrice().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {

            }
        });
        return view;
    }
}