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
import com.datenight_immersia_ltd.utils.stripe.config.DateNightEphemeralKeyProvider;
import com.datenight_immersia_ltd.views.payment.PaymentActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.stripe.android.CustomerSession;
import com.stripe.android.PaymentSessionConfig;
import com.stripe.android.model.Address;
import com.stripe.android.model.PaymentMethod;
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
        CustomerSession.initCustomerSession(requireContext(), new DateNightEphemeralKeyProvider());
        createPaymentSessionConfig();
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
            int cardId = v.getId();

            switch (cardId) {
                case R.id.dtc100:
                    payWithCard(Double.parseDouble((String) binding.coinCost100.getText()));
                    coinIncrement(100);
                    break;
                case R.id.dtc200:
                    payWithCard(Double.parseDouble((String) binding.coinCost200.getText()));
                    coinIncrement(200);
                    break;
                case R.id.dtc500:
                    payWithCard(Double.parseDouble((String) binding.coinCost200.getText()));
                    coinIncrement(500);
                    break;
                case R.id.dtc1000:
                    payWithCard(Double.parseDouble((String) binding.coinCost200.getText()));
                    coinIncrement(1000);
                    break;
                case R.id.dtc10000:
                    payWithCard(Double.parseDouble((String) binding.coinCost200.getText()));
                    coinIncrement(10000);
                    break;
                case R.id.dtc20000:
                    payWithCard(Double.parseDouble((String) binding.coinCost200.getText()));
                    coinIncrement(20000);
                    break;
                default:
                    Log.i("DTC CARD: ", "Nothing clicked");
            }
        };

        //PAY ACTIVITY, parse intent extra key as price. in new activity get intent extra with key price
        payWithCard.setOnClickListener(pay);

        bottomSheet.setContentView(view);
        bottomSheet.show();
    }

    public void coinIncrement(int coinNumber) {
        //TODO: After Payment Goes through-- Would be in call back
        userDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                if (value.exists()) {
                    UserModel user = value.toObject(UserModel.class);

                    if (user != null) {
                        int coinAmount = coinNumber;
                        coinAmount += user.getDtc();
                        //user.setDtc(coinAmount);
                        userDocRef.update("dtc", coinAmount).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(requireContext(), "", Toast.LENGTH_LONG).show();
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
        Intent intent = new Intent(requireContext(), PaymentActivity.class);

        //parse intent extra key as price. in new activity get intent extra with key price
        Log.i("DateNight Coin Fragment:", String.valueOf(price));
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

    @NonNull
    private PaymentSessionConfig createPaymentSessionConfig() {
        return new PaymentSessionConfig.Builder()

                //Don't collect shipping info
                .setShippingMethodsRequired(false)
                .setShippingInfoRequired(false)

                //controls whether the user can delete a payment method by swiping on it
                .setCanDeletePaymentMethods(true)


                // specify the payment method types that the customer can use;
                // defaults to PaymentMethod.Type.Card
                .setPaymentMethodTypes(Arrays.asList(PaymentMethod.Type.Card))

                // specify a layout to display under the payment collection form
                //.setAddPaymentMethodFooter(R.layout.add_payment_method_footer)

                // if `true`, will show "Google Pay" as an option on the
                // Payment Methods selection screen
                .setShouldShowGooglePay(true)

                .build();
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


}