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

package com.ltd_immersia_datenight.utils.stripe.config;

import android.app.Application;

import com.stripe.android.PaymentConfiguration;

public class StripeConfiguration extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PaymentConfiguration.init(
                getApplicationContext(), "pk_test_51IEOyvKzXEZpc2WIIhHL4e7Z9885t8iFLNgPgBnQSyz70BNKoYEhHcfhUYtwShJgl1tmqJRV3BEHQ0luhRUbPQ4c00iW4180mZ");
        //Immersia TEST PK
    }
}
