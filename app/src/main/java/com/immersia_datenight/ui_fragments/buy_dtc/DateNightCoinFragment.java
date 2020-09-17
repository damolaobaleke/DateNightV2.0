package com.immersia_datenight.ui_fragments.buy_dtc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.immersia_datenight.R;

public class DateNightCoinFragment extends Fragment {

    private DateNightCoinModel slideshowViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_datenightcoin, container, false);
        final TextView textView = view.findViewById(R.id.textView7);

//        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return view;
    }
}