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

package com.datenight_immersia_ltd.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.datenight_immersia_ltd.DatabaseConstants;
import com.datenight_immersia_ltd.modelfirestore.DateNightAppData;
import com.datenight_immersia_ltd.modelfirestore.Experience.ExperienceModel;
import com.datenight_immersia_ltd.modelfirestore.User.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stripe.android.PaymentConfiguration;

import java.util.HashMap;

/*
This class will hold information that's intended to be persisted throughout the app lifecycle
 */
public class DateNight extends Application {
    public DateNightAppData appData = null;

    @Override
    public void onCreate() {
        super.onCreate();
        //Configure stripe
        PaymentConfiguration.init(
                getApplicationContext(), "pk_test_51GxxDoK2qfQUDJH6Wu9e3mV1nK4TBdxvAsAlODQIzX3FfgVtgxigPRT18ZAMSGjJJv5wMYWbl8fCKLgGjEy0A0rW00yCDyfpYo");
    }

    public DateNightAppData getAppData(String userId){
        /*
        if (appData != null && !appData.validateUserAccessingData(userId)) {
            // Nullify appData if data that exists is for another user
            appData = null;
        }
        */
        return appData;
    }
    public void initializeAppData (String currentUserId, Context parentContext){
        appData = new DateNightAppData(currentUserId, parentContext);
    }

    public void clearAppData(){
        appData = null;
    }
}
