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

package com.datenight_immersia_ltd.network.api;

import com.datenight_immersia_ltd.modelfirestore.User.UserModel;
import com.datenight_immersia_ltd.utils.stripe.config.PaymentIntentObject;

public class UserObject {
    boolean success;
    String message;
    UserModel data;
    UserModelStripe stripeData;
    PaymentIntentObject paymentIntentData;

    public UserObject(boolean success, String message, UserModel data, UserModelStripe stripeData) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.stripeData = stripeData;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public UserModel getData() {
        return data;
    }

    public UserModelStripe getStripeData() {
        return stripeData;
    }

    public PaymentIntentObject getPaymentIntentData() {
        return paymentIntentData;
    }
}
