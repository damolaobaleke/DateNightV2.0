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

package com.datenight_immersia_ltd.views.payment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.network.api.DatenightApi;
import com.datenight_immersia_ltd.network.api.UserObject;
import com.datenight_immersia_ltd.utils.stripe.config.DateNightEphemeralKeyProvider;
import com.google.firebase.auth.FirebaseAuth;
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
import com.stripe.android.model.ShippingInformation;
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
    private Button startPaymentFlowButton;
    DatenightApi api;
    private static final String BASE_URL = "http://172.20.10.7:3000"; //http://api.datenight.com
    FirebaseAuth mAuth;
    private static final int REQUEST_CODE = 245;
    Button confirmPayment;
    TextView addPaymentMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        paymentUI();
        confirmPayment = findViewById(R.id.button_confirm_payment);

        mAuth = FirebaseAuth.getInstance();

        //initialize customer session
        CustomerSession.initCustomerSession(this, new DateNightEphemeralKeyProvider());

        //init payment session
        paymentSession = new PaymentSession(this, new PaymentSessionConfig.Builder()
                .setShippingInfoRequired(false)
                .build());
        setupPaymentSession();

        //On add payment method clicked
        //add payment method


        setUpNetworkRequest();

        //on Confirm button clicked:
        confirmPayment.setOnClickListener(v->getClientSecret());

    }

    private void paymentUI(){
        PaymentAuthConfig.Stripe3ds2ButtonCustomization selectCustomization = new PaymentAuthConfig.Stripe3ds2ButtonCustomization.Builder()
                .setBackgroundColor("#EC4847")
                .setTextColor("#000000")
                .build();

        PaymentAuthConfig.Stripe3ds2UiCustomization uiCustomization = PaymentAuthConfig.Stripe3ds2UiCustomization.Builder.createWithAppTheme(this)
                        .setButtonCustomization(selectCustomization, PaymentAuthConfig.Stripe3ds2UiCustomization.ButtonType.SELECT)
                        .build();

        PaymentAuthConfig.init(new PaymentAuthConfig.Builder().set3ds2Config(new PaymentAuthConfig.Stripe3ds2Config.Builder().setUiCustomization(uiCustomization).build())
                        .build());
    }

    public void setAddPaymentMethod(View view){
        paymentSession.presentPaymentMethodSelection(null);
    }

    private void setupPaymentSession() {
        paymentSession.init(new PaymentSession.PaymentSessionListener() {
            @Override
            public void onCommunicatingStateChanged(boolean b) {

            }

            @Override
            public void onError(int i, @NotNull String s) {

            }

            @Override
            public void onPaymentSessionDataChanged(@NotNull PaymentSessionData paymentSessionData) {

            }
        });
        startPaymentFlowButton.setEnabled(true);
    }

    //payment session handler:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == 1) { //1 ==OK

                if (data != null) {
                    paymentSession.handlePaymentData(requestCode, resultCode, data);

                    //Adding of Payments Listener
                    Stripe stripe = new Stripe(this, PaymentConfiguration.getInstance(this).getPublishableKey());

                    stripe.onPaymentResult(requestCode, data, new ApiResultCallback<PaymentIntentResult>() {
                        @Override
                        public void onSuccess(@NotNull PaymentIntentResult paymentIntentResult) {
                            PaymentIntent paymentIntent = paymentIntentResult.getIntent();
                            StripeIntent.Status status = paymentIntent.getStatus();

                            if (status == StripeIntent.Status.Succeeded) {
                                Toast.makeText(PaymentActivity.this, "Payment Succeeded", Toast.LENGTH_LONG).show();
                                //Add coin increment method here
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
            }
        }

    }

    @NonNull
    private ShippingInformation getDefaultShippingInfo() {
        // optionally specify default shipping address
        return new ShippingInformation();
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
        //TODO: amount passed from dtc fragment --DONE
        Intent intent = getIntent();
        String cost = intent.getStringExtra("dtcPrice");

        String[] split = cost.split("£"); //remove pounds sign for charge
        int refinedCost = Integer.parseInt(split[1]);
        Log.i("Payments", cost + " " + split[1] + refinedCost);

        Call<UserObject> userObjectCall = api.getClientSecret(mAuth.getCurrentUser().getUid(), 100);

        userObjectCall.enqueue(new Callback<UserObject>() {
            @Override
            public void onResponse(Call<UserObject> call, Response<UserObject> response) {
                //confirm payment with client secret
                if (!response.isSuccessful()) {
                    Toast.makeText(PaymentActivity.this, "response not gotten",Toast.LENGTH_LONG).show();

                    return;
                }

                UserObject user = response.body();
                Log.i("Payment Activity", user.getMessage() + user.getPaymentIntentData().getClientSecret());

                pay(user.getPaymentIntentData().getStripeCustomerId(), user.getPaymentIntentData().getClientSecret());
            }

            @Override
            public void onFailure(Call<UserObject> call, Throwable t) {
                Log.i("Payment Activity", t.getMessage());
            }
        });

    }

    private void pay(String stripeAccountId, String clientSecret) {
        Stripe stripe = new Stripe(this, PaymentConfiguration.getInstance(this).getPublishableKey(), stripeAccountId);
        stripe.confirmPayment(this, ConfirmPaymentIntentParams.create(clientSecret), stripeAccountId);
    }

}
