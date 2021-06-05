package com.ltd_immersia_datenight.views.datehub_navigation.ui_fragments.buy_dtc;

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
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ltd_immersia_datenight.R;
import com.ltd_immersia_datenight.databinding.FragmentDatenightcoinBinding;
import com.ltd_immersia_datenight.modelfirestore.User.UserModel;
import com.ltd_immersia_datenight.utils.constants.DatabaseConstants;
import com.ltd_immersia_datenight.utils.constants.PriceConstants;
import com.ltd_immersia_datenight.views.payment.PaymentActivity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class DateNightCoinFragment extends Fragment {

    private DateNightCoinViewModel dtcViewModel;
    CardView dtc100, dtc200;
    TextView cost1, cost2, cost3, cost4, cost5, cost6, cost7, cost8;
    Button payWithCard, payGoogle, cancel;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    DocumentReference userDocRef;

    FragmentDatenightcoinBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View Binding
        binding = FragmentDatenightcoinBinding.inflate(inflater, container, false);
        View view = binding.getRoot(); //R.layout.fragment_datenightcoin (Root view == CLayout)

        //Initialize db and auth
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userDocRef = db.collection(DatabaseConstants.USER_DATA_NODE).document(mAuth.getCurrentUser().getUid());

        //Initialize View Model
        dtcViewModel = new ViewModelProvider(this).get(DateNightCoinViewModel.class);


        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            UserModel user = documentSnapshot.toObject(UserModel.class);
            assert user != null;
            int num = Integer.parseInt(String.valueOf(user.getDtc()));
            String formattedDtcBalance = NumberFormat.getInstance(Locale.US).format(num);

            binding.availableCoins.setText(formattedDtcBalance);
        });

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
        payWithCard.setText(String.format("Pay %s with card", price));

        cancel = view.findViewById(R.id.cancel_pay);

        BottomSheetDialog bottomSheet = new BottomSheetDialog(requireContext());
        cancel.setOnClickListener(v -> bottomSheet.dismiss());

        View.OnClickListener pay = v -> {
            String p = price.toString();

            //OPTION 1 --Longer
            String[] split = p.split("£"); //remove pounds sign so can be changed to double for charge
            double refinedCost = Double.parseDouble(split[1]);
            Log.i("Payments", "cost: " + refinedCost);
            //

            if (p.equals(PriceConstants.PRICE_ZERO_99)) {
                payWithCard(refinedCost);
                //coinIncrement(100);
            } else if (p.equals(PriceConstants.PRICE_ONE_99)) {
                payWithCard(refinedCost);
            } else if (p.equals(PriceConstants.PRICE_THREE_99)) {
                payWithCard(dtcViewModel.getDtc500().getValue());
            } else if (price.equals(PriceConstants.PRICE_FIVE_99)) {
                payWithCard(dtcViewModel.getDtc1000().getValue());
            } else if (price.equals(PriceConstants.PRICE_SEVEN_99)) {
                payWithCard(dtcViewModel.getDtc2000().getValue());
            } else if (price.equals(PriceConstants.PRICE_TEN_99)) {
                payWithCard(dtcViewModel.getDtc5000().getValue());

            } else if (price.equals(PriceConstants.PRICE_FOURTEEN_99)) {
                payWithCard(dtcViewModel.getDtc10000().getValue());

            } else if (price.equals(PriceConstants.PRICE_NINETEEN_99)) {
                payWithCard(dtcViewModel.getDtc20000().getValue());

            } else {
                Log.i("DTC CARD: ", "Nothing clicked");
            }

        };

        //PAY ACTIVITY, parse intent extra key as price. in new activity get intent extra with key price
        payWithCard.setOnClickListener(pay);

        bottomSheet.setContentView(view);
        bottomSheet.show();
    }


    private void payWithCard(double price) {
        Intent intent = new Intent(requireContext(), PaymentActivity.class); //PaymentActivity - 1st Implementation

        //parse intent extra key as price. in new activity get intent extra with key price
        Log.i("DateNight Coin Fragment:", String.valueOf(price));
        intent.putExtra("dtcPrice", String.valueOf(price));
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



    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}