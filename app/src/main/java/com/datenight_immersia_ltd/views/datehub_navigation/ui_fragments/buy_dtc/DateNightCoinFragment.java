package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.buy_dtc;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.databinding.FragmentDatenightcoinBinding;
import com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.datehub.DateHubFragmentViewModel;
import com.datenight_immersia_ltd.views.payment.PaymentActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class DateNightCoinFragment extends Fragment {

    private DateNightCoinViewModel dtcViewModel;
    CardView dtc100, dtc200;
    TextView cost1, cost2, cost3, cost4, cost5, cost6, cost7, cost8;
    Button payWithCard, payGoogle, cancel;

    FragmentDatenightcoinBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View Binding
        binding = FragmentDatenightcoinBinding.inflate(inflater, container, false);
        View view = binding.getRoot(); //R.layout.fragment_datenightcoin (Root view == CLayout)

        //Initialize View Model
        dtcViewModel = new ViewModelProvider(this).get(DateNightCoinViewModel.class);

        //set prices
        dtcViewModel.getdtc100().observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double dtcValue) {
                binding.coinCost100.setText(String.format("£%s", dtcValue.toString())); //$
                Log.i("DTC100: ", binding.coinCost100.getText().toString());
            }
        });

        dtcViewModel.getdtc200().observe(getViewLifecycleOwner(), dtcValue -> binding.coinCost200.setText(String.format("£%s", dtcValue.toString())));
        dtcViewModel.getDtc500().observe(getViewLifecycleOwner(), dtcValue -> binding.coinCost500.setText(String.format("£%s", dtcValue.toString())));
        dtcViewModel.getDtc1000().observe(getViewLifecycleOwner(), dtcValue -> binding.coinCost1000.setText(String.format("£%s", dtcValue.toString())));
        dtcViewModel.getDtc2000().observe(getViewLifecycleOwner(), dtcValue -> binding.coinCost2000.setText(String.format("£%s", dtcValue.toString())));
        dtcViewModel.getDtc5000().observe(getViewLifecycleOwner(), dtcValue -> binding.coinCost5000.setText(String.format("£%s", dtcValue.toString())));
        dtcViewModel.getDtc10000().observe(getViewLifecycleOwner(), dtcValue -> binding.coinCost10000.setText(String.format("£%s", dtcValue.toString())));
        dtcViewModel.getDtc20000().observe(getViewLifecycleOwner(), dtcValue -> binding.coinCost20000.setText(String.format("£%s", dtcValue.toString())));

        //hard coded, would eventually get $price parsed in from pay bottom sheet and card dtc

        //Independent on click listener for each dtc value
        View.OnClickListener dateNightCoinListener = v -> {
            switch (v.getId()) {
                case R.id.dtc100:
                    payBottomSheet(binding.coinCost100.getText());
                    break;
                case R.id.dtc200:
                    payBottomSheet(binding.coinCost200.getText());
                    break;
                case R.id.dtc500:
                    payBottomSheet(binding.coinCost500.getText());
                    break;
                case R.id.dtc1000:
                    payBottomSheet(binding.coinCost1000.getText());
                    break;
                case R.id.dtc2000:
                    payBottomSheet(binding.coinCost2000.getText());
                    break;
                case R.id.dtc5000:
                    payBottomSheet(binding.coinCost5000.getText());
                    break;
                case R.id.dtc10000:
                    payBottomSheet(binding.coinCost10000.getText());
                    break;
                case R.id.dtc20000:
                    payBottomSheet(binding.coinCost20000.getText());
                    break;
                default:
                    return;

            }
        };

        binding.dtc100.setOnClickListener(dateNightCoinListener);
        binding.dtc200.setOnClickListener(dateNightCoinListener);
        binding.dtc500.setOnClickListener(dateNightCoinListener);
        binding.dtc1000.setOnClickListener(dateNightCoinListener);
        binding.dtc2000.setOnClickListener(dateNightCoinListener);
        binding.dtc5000.setOnClickListener(dateNightCoinListener);
        binding.dtc10000.setOnClickListener(dateNightCoinListener);
        binding.dtc20000.setOnClickListener(dateNightCoinListener);



        return view;
    }

    public void payBottomSheet(CharSequence price) {
        View view = getLayoutInflater().inflate(R.layout.pay_dialogue, null);
        payWithCard = view.findViewById(R.id.pay_with_card);
        payWithCard.setText("Pay " + price + " with card");

        cancel = view.findViewById(R.id.cancel_pay);

        BottomSheetDialog bottomSheet = new BottomSheetDialog(requireContext());
        cancel.setOnClickListener(v -> bottomSheet.dismiss());

        View.OnClickListener pay = v -> {
            int cardId = v.getId();

            if (cardId == R.id.dtc100) {
                payWithCard(Double.parseDouble((String) binding.coinCost100.getText()));
            } else if (cardId == R.id.dtc200) {
                payWithCard(Double.parseDouble((String) binding.coinCost200.getText()));
            } else if (cardId == R.id.dtc500) {
                payWithCard(Double.parseDouble((String) binding.coinCost200.getText()));
            } else if (cardId == R.id.dtc1000) {
                payWithCard(Double.parseDouble((String) binding.coinCost200.getText()));
            } else if (cardId == R.id.dtc10000) {
                payWithCard(Double.parseDouble((String) binding.coinCost200.getText()));
            } else if (cardId == R.id.dtc20000) {
                payWithCard(Double.parseDouble((String) binding.coinCost200.getText()));
            } else {
                Log.i("DTC CARD: ", "Nothing clicked");
            }
        };

        //PAY ACTIVITY, parse intent extra key as price. in new activity get intent extra with key price
        payWithCard.setOnClickListener(pay);

        bottomSheet.setContentView(view);
        bottomSheet.show();
    }

    public void payWithCard(double price) {
        Intent intent = new Intent(requireContext(), PaymentActivity.class);
        //parse intent extra key as price. in new activity get intent extra with key price
        intent.putExtra("dtcPrice", price);
        startActivity(intent);
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