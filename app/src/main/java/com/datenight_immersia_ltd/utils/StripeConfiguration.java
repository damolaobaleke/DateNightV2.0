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

package com.datenight_immersia_ltd.utils;

import android.app.Application;

import com.stripe.android.PaymentConfiguration;

public class StripeConfiguration extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PaymentConfiguration.init(
                getApplicationContext(), "pk_test_51GxxDoK2qfQUDJH6Wu9e3mV1nK4TBdxvAsAlODQIzX3FfgVtgxigPRT18ZAMSGjJJv5wMYWbl8fCKLgGjEy0A0rW00yCDyfpYo");
    }
}
