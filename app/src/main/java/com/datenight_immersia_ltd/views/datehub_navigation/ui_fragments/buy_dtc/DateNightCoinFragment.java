package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.buy_dtc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.datenight_immersia_ltd.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class DateNightCoinFragment extends Fragment {

    private DateNightCoinModel slideshowViewModel;
    CardView dtc100, dtc200;
    Button payWithCard, payGoogle, cancel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_datenightcoin, container, false);
        final TextView textView = view.findViewById(R.id.textView7);
//        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        dtc100 = view.findViewById(R.id.dtc100);
        dtc200 = view.findViewById(R.id.dtc200);

        //hard coded, would eventually get $price parsed in from pay dialogue and card dtc
        dtc100.setOnClickListener(v -> payBottomSheet());
        dtc200.setOnClickListener(v -> payDialogue());


        return view;
    }

    public void payBottomSheet() {
        View view = getLayoutInflater().inflate(R.layout.pay_dialogue, null);
        payWithCard = view.findViewById(R.id.pay_with_card);
        cancel = view.findViewById(R.id.cancel_pay);

        BottomSheetDialog bottomSheet = new BottomSheetDialog(requireContext());
        cancel.setOnClickListener(v-> bottomSheet.dismiss());

        bottomSheet.setContentView(view);
        bottomSheet.show();
    }


    public void payDialogue() {
        View view = getLayoutInflater().inflate(R.layout.pay_dialogue, null);
        payWithCard = view.findViewById(R.id.pay_with_card);
        cancel = view.findViewById(R.id.cancel_pay);

        AlertDialog alertDialog = new AlertDialog.Builder(requireContext()).create();
        //Set dialogue to bottom of screen
        setDailoguePosition(alertDialog);
        cancel.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        alertDialog.setView(view);
        alertDialog.show();

    }

    public void setDailoguePosition(AlertDialog dialog) {
        Window window = dialog.getWindow();
        assert window != null;
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
    }

    public void alertTest() {
        //No dismiss method using the Builder
        new AlertDialog.Builder(requireContext()).create().show();
    }
}