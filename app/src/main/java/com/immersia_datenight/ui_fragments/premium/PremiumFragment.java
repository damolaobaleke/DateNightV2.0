package com.immersia_datenight.ui_fragments.premium;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.immersia_datenight.R;

public class PremiumFragment extends Fragment {

    private PremiumModel homeViewModel;
    private CardView jetski;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //homeViewModel = new ViewModelProvider(this).get(PremiumModel.class);
        View view = inflater.inflate(R.layout.fragment_premium, container, false);
        jetski = view.findViewById(R.id.jetSki);
        jetski.setCardElevation(1000f);



//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return view;
    }
}