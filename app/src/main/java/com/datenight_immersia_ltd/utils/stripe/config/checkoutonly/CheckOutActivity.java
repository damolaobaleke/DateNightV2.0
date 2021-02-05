/*
 * Copyright 2021 Damola Obaleke. All rights reserved.
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

package com.datenight_immersia_ltd.utils.stripe.config.checkoutonly;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.datenight_immersia_ltd.IntentConstants;
import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.views.datehub_navigation.DateHubNavigation;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CheckOutActivity extends AppCompatActivity {
    private static final String BASEURL = "http://172.20.10.7:3000/";
    private final OkHttpClient httpClient = new OkHttpClient();
    private String paymentIntentClientSecret;
    private Stripe stripe;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        mAuth = FirebaseAuth.getInstance();

        // Configure the SDK with Stripe publishable key to make requests to Stripe
        stripe = new Stripe(getApplicationContext(),
                Objects.requireNonNull("pk_test_51GzuBdBtgQNloHVuBZpRpRodyKuZAj4sc5QpafRMlAmXxC0geL2W4LvovlfMn2pEgeooyOSZ27IKmrYW3GoIQaXd00kbGDoj7n")
        );
        startCheckout();
    }
    private void startCheckout() {
        // call to server to create payment Intent
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        Map<String, Object> payMap = new HashMap<>();

        payMap.put("currency", "usd");

        Intent intent = getIntent();
        String cost = intent.getStringExtra("dtcPrice");

        String[] split = cost.split("£"); //remove pounds sign for charge
        double refinedCost = Double.parseDouble(split[1]);
        Log.i("Payments", cost + " " + split[1] + refinedCost);

        payMap.put("amount", cost);
        payMap.put("item", "dtc");
        String jsonBody = new Gson().toJson(payMap);


        //Adding query parameters
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(BASEURL)
                .addQueryParameter("id", mAuth.getCurrentUser().getUid())
                .addQueryParameter("amount", cost)
                .build();

        RequestBody body = RequestBody.create(mediaType, jsonBody);
        Request request = new Request.Builder()
                .url(BASEURL + "create-payment-intent")
                .post(body) //post request body
                .url(url) //query params
                .build();
        httpClient.newCall(request).enqueue(new PayCallback(this));

        // Hook up the pay button to the card widget and stripe instance
        Button payButton = findViewById(R.id.payButton);
        payButton.setOnClickListener(v -> {
           new AlertDialog.Builder(this).setTitle("Get more dtc").setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   pay();
               }
           }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   backToDtc();
               }
           }).create().show();
        });
    }


   private void pay(){
       CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
       PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();

       if (params != null) {
           ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                   .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
           stripe.confirmPayment(this, confirmParams);
       }
   }

   private void backToDtc(){
       Intent intent = new Intent(this, DateHubNavigation.class);
       intent.putExtra(IntentConstants.FRAGMENT_TO_LOAD, IntentConstants.BUY_DTC_FRAGMENT);
       startActivity(intent);
   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }


    private void onPaymentSuccess(@NonNull final Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();

        Map<String, String> responseMap = gson.fromJson(Objects.requireNonNull(response.body()).string(), type);
        paymentIntentClientSecret = responseMap.get("clientSecret");

    }


    private static final class PayCallback implements Callback {
        WeakReference<CheckOutActivity> activityRef;
        CheckOutActivity activity = activityRef.get();

        PayCallback(@NonNull CheckOutActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onFailure(Request request, IOException e) {

        }

        @Override
        public void onResponse(Response response) throws IOException {
            if (!response.isSuccessful()) {
                activity.runOnUiThread(() -> Toast.makeText(activity, "Error: " + response.toString(), Toast.LENGTH_LONG).show());
            } else {
                activity.onPaymentSuccess(response);
            }
        }
    }


    private final class PaymentResultCallback implements ApiResultCallback<PaymentIntentResult> {
        @NonNull private final WeakReference<CheckOutActivity> activityRef;

        PaymentResultCallback(CheckOutActivity activity) {
            activityRef = new WeakReference<>(activity);
        }


        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();

            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Toast.makeText(CheckOutActivity.this, "Payment Succeeded", Toast.LENGTH_LONG).show();

            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                Toast.makeText(CheckOutActivity.this, "Payment failed" +Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage() , Toast.LENGTH_LONG).show();
            }
        }
        @Override
        public void onError(@NonNull Exception e) {
            // Payment request failed – allow retrying using the same payment method
            Toast.makeText(CheckOutActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}