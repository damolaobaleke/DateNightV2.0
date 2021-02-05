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

package com.datenight_immersia_ltd.utils.stripe.config;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.annotations.NotNull;
import com.stripe.android.PaymentSession;
import com.stripe.android.PaymentSessionData;
import com.stripe.android.model.PaymentMethod;

public class PaymentSessionListener implements PaymentSession.PaymentSessionListener {
    // Called whenever the PaymentSession's data changes,
    // e.g. when the user selects a new `PaymentMethod`
    public static PaymentMethod paymentMethod;

    @Override
    public void onPaymentSessionDataChanged(@NonNull PaymentSessionData data) {
        if (data.getUseGooglePay()) {
            // customer intends to pay with Google Pay
            //set up google pay payment
        } else {
            paymentMethod = data.getPaymentMethod();
            if (paymentMethod != null) {
                // Display information about the selected payment method
                Log.i("Payment Session:", "pmthd id: " + paymentMethod.id);
            }
        }

        // Update your UI here with other data
        if (data.isPaymentReadyToCharge()) {
            // Use the data to complete your charge - see below.
        }
    }

    @Override
    public void onCommunicatingStateChanged(boolean isCommunicating) {
        //show progressbar
        // update UI, such as hiding or showing a progress bar
    }

    @Override
    public void onError(int errorCode, @NotNull String errorMessage) {
        Log.i("PaymentsessionListener", errorMessage);
        // handle error
    }
}
