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

package com.immersia_ltd_datenight.utils.stripe.config;

import android.app.Application;
import android.content.Context;

import com.immersia_ltd_datenight.modelfirestore.DateNightAppData;
import com.stripe.android.PaymentConfiguration;

/*
This class will hold information that's intended to be persisted throughout the app lifecycle
 */
public class DateNight extends Application {
    private DateNightAppData appData ;

    @Override
    public void onCreate() {
        super.onCreate();
        //Configure stripe
        PaymentConfiguration.init(getApplicationContext(), "pk_live_51IEOyvKzXEZpc2WIJ0yUpc6PIdoqK3vLyr0ePNiGNG3NGDZBJu6XXA0OXMCGjHdlKudRXxWoK5ZwPWIWD70uXdyt00jliMQrn9");
        //Immersia PK
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
