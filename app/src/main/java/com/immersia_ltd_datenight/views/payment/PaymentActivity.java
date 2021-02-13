/*
 * Copyright 2020 Damola Obaleke. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.immersia_ltd_datenight.views.payment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.immersia_ltd_datenight.utils.constants.DatabaseConstants;
import com.immersia_ltd_datenight.utils.constants.IntentConstants;
import com.immersia_ltd_datenight.R;
import com.immersia_ltd_datenight.modelfirestore.User.UserModel;
import com.immersia_ltd_datenight.network.api.DatenightApi;
import com.immersia_ltd_datenight.network.api.UserObject;
import com.immersia_ltd_datenight.utils.stripe.config.DateNightEphemeralKeyProvider;
import com.immersia_ltd_datenight.views.datehub_navigation.DateHubNavigation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.CustomerSession;
import com.stripe.android.PaymentAuthConfig;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.PaymentSession;
import com.stripe.android.PaymentSessionConfig;
import com.stripe.android.PaymentSessionData;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.StripeIntent;

import org.jetbrains.annotations.NotNull;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentActivity extends AppCompatActivity {
    private PaymentSession paymentSession;
    DatenightApi api;
    private static final String BASE_URL = "https://api.immersia.co.uk"; //https://api.immersia.co.uk khttp://172.20.10.7:3000
    FirebaseAuth mAuth;
    private static final int REQUEST_CODE = 245;
    Button confirmPayment;
    TextView methodChosen, payPrice;
    PaymentMethod selectedPaymentMethod;
    Intent intent, mData;
    String cost;
    FirebaseFirestore db;
    DocumentReference userDocRef;
    ProgressBar progressBarPay;
    int reqCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //paymentUI();
        confirmPayment = findViewById(R.id.button_confirm_payment);
        methodChosen = findViewById(R.id.button_add_payment_method);
        payPrice = findViewById(R.id.pay_price);
        progressBarPay = findViewById(R.id.progress_bar_payment);

        intent = getIntent();
        cost = intent.getStringExtra("dtcPrice");
        payPrice.setText(String.format("You're about to pay £%s", cost));


        /*STRIPE -- initialize customer session to retrieve ephemeral key from server side*/
        CustomerSession.initCustomerSession(this, new DateNightEphemeralKeyProvider());

        //initialize and set up payment session
        paymentSession = new PaymentSession(this, createPaymentSessionConfig());

        setupPaymentSession();

        //on Confirm button clicked:
        confirmPayment.setOnClickListener(v -> {
            Toast.makeText(this, "Initializing payment", Toast.LENGTH_SHORT).show();
            getClientSecret();
        });

    }

    private void paymentUI() {
        PaymentAuthConfig.Stripe3ds2ButtonCustomization selectCustomization = new PaymentAuthConfig.Stripe3ds2ButtonCustomization.Builder()
                .setBackgroundColor("#EC4847")
                .setTextColor("#B048D1")
                .build();

        PaymentAuthConfig.Stripe3ds2UiCustomization uiCustomization = PaymentAuthConfig.Stripe3ds2UiCustomization.Builder.createWithAppTheme(this)
                .setButtonCustomization(selectCustomization, PaymentAuthConfig.Stripe3ds2UiCustomization.ButtonType.SELECT)
                .build();

        PaymentAuthConfig.init(new PaymentAuthConfig.Builder().set3ds2Config(new PaymentAuthConfig.Stripe3ds2Config.Builder().setUiCustomization(uiCustomization).build())
                .build());
    }

    //on add card clicked
    public void setAddPaymentMethod(View view) {
        paymentSession.presentPaymentMethodSelection("");
    }

    //payment session listener
    private void setupPaymentSession() {
        paymentSession.init(new PaymentSession.PaymentSessionListener() {
            @Override
            public void onPaymentSessionDataChanged(@NotNull PaymentSessionData data) {
                // use paymentMethod
                if (data.getUseGooglePay()) {
                    // customer intends to pay with Google Pay
                    //set up google pay payment
                }

                Log.i("Payment Session", "Payment session changed " + data);
                Log.i("Payment Session", "Payment session ==> " + data.isPaymentReadyToCharge() + "\n" + data.getPaymentMethod());

                if (data.isPaymentReadyToCharge()) {
                    Log.i("Payment session", "Ready to charge");
                    confirmPayment.setEnabled(true); //only when isPaymentReadyTo charge true

                    methodChosen.setText(data.getPaymentMethod().component8().brand + " ends with " + data.getPaymentMethod().component8().last4);
                    selectedPaymentMethod = data.getPaymentMethod();

                } else {
                    //When payment is not ready to charge- the case when no payment method has been chosen
                }

            }

            @Override
            public void onCommunicatingStateChanged(boolean b) {
                // update UI, such as hiding or showing a progress bar
                progressBarPay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(int i, @NotNull String s) {
                // handle error
                Log.e("Payment Session", "error: " + s);
            }
        });
    }

    //payment session handler: In order to get updates for the PaymentSessionData object and to handle state during Activity lifecycle
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        paymentSession.handlePaymentData(requestCode, resultCode, data);//get update of paymentSessionData from stripe activities return to PaySessListener

        //
        Stripe stripe = new Stripe(this, PaymentConfiguration.getInstance(this).getPublishableKey());
        stripe.onPaymentResult(requestCode, data, new ApiResultCallback<PaymentIntentResult>() {
            @Override
            public void onSuccess(@NotNull PaymentIntentResult paymentIntentResult) {
                PaymentIntent paymentIntent = paymentIntentResult.getIntent();
                StripeIntent.Status status = paymentIntent.getStatus();

                if (status == StripeIntent.Status.Succeeded) {
                    Toast.makeText(PaymentActivity.this, "Payment Succeeded", Toast.LENGTH_LONG).show();
                    Log.i("Payments", "Payment of " + cost + "went through");
                    //Add coin
                    addCoin();
                    progressBarPay.setVisibility(View.INVISIBLE);
                    //end payment session
                    paymentSession.onCompleted();

                } else if (status == StripeIntent.Status.RequiresPaymentMethod) {
                    Toast.makeText(PaymentActivity.this, "Please enter a payment method", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(@NotNull Exception e) {
                Toast.makeText(PaymentActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    private void setUpNetworkRequest() {
        //Logging (Http)REQUEST and RESPONSE
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(loggingInterceptor)
                .build();
        //Logging Request and Response

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) //GSON convert java object to JSON
                .client(okHttpClient)
                .build();
        api = retrofit.create(DatenightApi.class);
    }

    private void getClientSecret() {
        setUpNetworkRequest();
        String cost = intent.getStringExtra("dtcPrice");
        Log.i("PaymentActivity", "The " + cost);


        Call<UserObject> userObjectCall = api.getClientSecret(mAuth.getCurrentUser().getUid(), Double.parseDouble(cost)); //Double.parseDouble(cost)

        userObjectCall.enqueue(new Callback<UserObject>() {
            @Override
            public void onResponse(Call<UserObject> call, Response<UserObject> response) {
                //confirm payment with client secret
                if (!response.isSuccessful()) {
                    Toast.makeText(PaymentActivity.this, "response not gotten", Toast.LENGTH_LONG).show();

                    return;
                }

                UserObject user = response.body();
                Log.i("Payment Activity", user.isSuccess() + "\n" + user.getMessage() + "\n" + user.getPaymentIntentData().getClientSecret() + "\n" + selectedPaymentMethod.id);

                confirmPay(user.getPaymentIntentData().getClientSecret(), selectedPaymentMethod.id);
            }

            @Override
            public void onFailure(Call<UserObject> call, Throwable t) {
                Log.i("Payment Activity", t.getMessage());
            }
        });

    }

    //charges the card and executes payment
    private void confirmPay(String clientSecret, String paymentMethodId) {
        Stripe stripe = new Stripe(this, PaymentConfiguration.getInstance(this).getPublishableKey());
        stripe.confirmPayment(this,
                ConfirmPaymentIntentParams.createWithPaymentMethodId(
                        paymentMethodId,
                        clientSecret
                )
        );

        Toast.makeText(PaymentActivity.this, "Payment Succeeded", Toast.LENGTH_LONG).show();
        progressBarPay.setVisibility(View.INVISIBLE);

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

                // specify a layout to display under the payment collection form
                //.setAddPaymentMethodFooter(R.layout.add_payment_method_footer)

                // if `true`, will show "Google Pay" as an option on the
                // Payment Methods selection screen
                .setShouldShowGooglePay(true)

                .build();
    }

    private void addCoin() {
        switch (cost) {
            case "0.99":
                coinIncrement(100);
                break;
            case "1.99":
                coinIncrement(200);
                break;
            case "3.99":
                coinIncrement(500);
                break;
            case "5.99":
                coinIncrement(1000);
                break;
            case "7.99":
                coinIncrement(2000);
                break;
            case "9.99":
                coinIncrement(5000);
                break;
            case "14.99":
                coinIncrement(10000);
                break;
            case "19.99":
                coinIncrement(20000);
                break;
            default:
                Log.i("DTC CARD: ", "Nothing clicked");
                break;
        }
    }

    public void coinIncrement(int coinNumber) {
        userDocRef = db.collection(DatabaseConstants.USER_DATA_NODE).document(mAuth.getCurrentUser().getUid());

        userDocRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            UserModel user = documentSnapshot.toObject(UserModel.class);

                            if (user != null) {
                                int updatedAmount = coinNumber + user.getDtc();

                                userDocRef.update("dtc", updatedAmount).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(PaymentActivity.this, "You bought " + coinNumber + " coins", Toast.LENGTH_LONG).show();
                                        /*intent to go back to DateHub*/
                                        Intent intent = new Intent(PaymentActivity.this, DateHubNavigation.class);
                                        intent.putExtra(IntentConstants.FRAGMENT_TO_LOAD, IntentConstants.DATE_HUB_FRAGMENT);
                                        startActivity(intent);
                                    }
                                });

                            }
                        }
                    }
                });

    }

}
