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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.datenight_immersia_ltd.DatabaseConstants;
import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.databinding.FragmentDatenightcoinBinding;
import com.datenight_immersia_ltd.modelfirestore.User.UserModel;
import com.datenight_immersia_ltd.views.payment.PaymentActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.stripe.android.PaymentSessionConfig;
import com.stripe.android.model.Address;
import com.stripe.android.model.ShippingInformation;
import com.stripe.android.model.ShippingMethod;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
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
        userDocRef = db.collection(DatabaseConstants.USER_DATA_COLLECTION).document(mAuth.getCurrentUser().getUid());

        //Initialize View Model
        dtcViewModel = new ViewModelProvider(this).get(DateNightCoinViewModel.class);

        /*STRIPE -- initialize customer session to retrieve ephemeral key from server side*/
        //CustomerSession.initCustomerSession(requireContext(), new DateNightEphemeralKeyProvider());
        //
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
        payWithCard.setText("Pay " + price + " with card");

        cancel = view.findViewById(R.id.cancel_pay);

        BottomSheetDialog bottomSheet = new BottomSheetDialog(requireContext());
        cancel.setOnClickListener(v -> bottomSheet.dismiss());

        View.OnClickListener pay = v -> {
            //int cardId = v.getId();
            String p = price.toString();

            //OPTION 1 --Longer
            String[] split = p.split("£"); //remove pounds sign for charge
            double refinedCost = Double.parseDouble(split[1]);
            Log.i("Payments",  split[1] + " "  + refinedCost);
            //

            if (p.equals("£0.99")) {
                payWithCard(refinedCost);
                //coinIncrement(100);
            } else if (p.equals("£1.99")) {
                payWithCard(refinedCost);
                //coinIncrement(200);
            } else if (price.equals("£3.99")) {
                payWithCard(dtcViewModel.getDtc500().getValue());
                coinIncrement(500);
            } else if (price.equals("£5.99")) {
                payWithCard(dtcViewModel.getDtc1000().getValue());
                coinIncrement(1000);
            } else if (price.equals("£7.99")) {
                payWithCard(dtcViewModel.getDtc2000().getValue());
                coinIncrement(2000);
            } else if (price.equals("£9.99")) {
                payWithCard(dtcViewModel.getDtc5000().getValue());
                coinIncrement(5000);
            } else if (price.equals("£14.99")) {
                payWithCard(dtcViewModel.getDtc10000().getValue());
                coinIncrement(10000);
            } else if (price.equals("£19.99")) {
                payWithCard(dtcViewModel.getDtc20000().getValue());
                coinIncrement(20000);
            } else {
                Log.i("DTC CARD: ", "Nothing clicked");
            }

        };

        //PAY ACTIVITY, parse intent extra key as price. in new activity get intent extra with key price
        payWithCard.setOnClickListener(pay);

        bottomSheet.setContentView(view);
        bottomSheet.show();
    }

    public void coinIncrement(int coinNumber) {
        //TODO: Move to After Payment Goes through-- Would be in call back
        userDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                if (value.exists()) {
                    UserModel user = value.toObject(UserModel.class);

                    if (user != null) {
                        int coinAmount = coinNumber;
                        coinAmount += user.getDtc();
                        //Log.i("Total", String.valueOf(coinAmount));


                        userDocRef.update("dtc", coinAmount).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(requireContext(), "You bought " + coinNumber + " coins", Toast.LENGTH_LONG).show();
                                /**intent to go back to DateHub*/
                                //Intent intent = new Intent(requireContext(), DateHubNavigation.class);
                                //intent.putExtra(IntentConstants.FRAGMENT_TO_LOAD, IntentConstants.DATE_HUB_FRAGMENT);
                                //startActivity(intent);
                            }
                        });

                    }
                }
            }
        });

    }

    private void payWithCard(double price) {
        //TODO: stripe payments sdk integration --DONE
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


    private static class AppShippingInformationValidator implements PaymentSessionConfig.ShippingInformationValidator {

        @Override
        public boolean isValid(@NonNull ShippingInformation shippingInformation) {
            final Address address = shippingInformation.getAddress();
            return address != null && Locale.US.getCountry() == address.getCountry();
        }

        @NonNull
        public String getErrorMessage(
                @NonNull ShippingInformation shippingInformation
        ) {
            return "A US address is required";
        }
    }

    private static class AppShippingMethodsFactory implements PaymentSessionConfig.ShippingMethodsFactory {

        @Override
        public List<ShippingMethod> create(@NonNull ShippingInformation shippingInformation) {
            return Arrays.asList(
                    new ShippingMethod(
                            "UPS Ground",
                            "ups-ground",
                            0,
                            "USD",
                            "Arrives in 3-5 days"
                    ),
                    new ShippingMethod(
                            "FedEx",
                            "fedex",
                            599,
                            "USD",
                            "Arrives tomorrow"
                    )
            );
        }

    }

    public void redundant(){
//        switch (v.getId()) {
//            case R.id.dtc100:
//                payWithCard(Double.parseDouble((String) binding.coinCost100.getText()));
//                coinIncrement(100);
//                break;
//            case R.id.dtc200:
//                payWithCard(Double.parseDouble((String) binding.coinCost200.getText()));
//                coinIncrement(200);
//                break;
//            case R.id.dtc500:
//                payWithCard(Double.parseDouble((String) binding.coinCost200.getText()));
//                coinIncrement(500);
//                break;
//            case R.id.dtc1000:
//                payWithCard(Double.parseDouble((String) binding.coinCost200.getText()));
//                coinIncrement(1000);
//                break;
//            case R.id.dtc10000:
//                payWithCard(Double.parseDouble((String) binding.coinCost200.getText()));
//                coinIncrement(10000);
//                break;
//            case R.id.dtc20000:
//                payWithCard(Double.parseDouble((String) binding.coinCost200.getText()));
//                coinIncrement(20000);
//                break;
//            default:
//                Log.i("DTC CARD: ", "Nothing clicked");
//        }
    }
}