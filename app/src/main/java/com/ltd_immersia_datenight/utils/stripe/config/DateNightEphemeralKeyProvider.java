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

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.ltd_immersia_datenight.network.api.DatenightApi;
import com.ltd_immersia_datenight.network.api.UserObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stripe.android.EphemeralKeyProvider;
import com.stripe.android.EphemeralKeyUpdateListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DateNightEphemeralKeyProvider implements EphemeralKeyProvider {
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    private static final String TAG = "EphProvider";

    private static final String BASE_URL = "https://api.immersia.co.uk"; //https://api.immersia.co.uk http://172.20.10.7:3000
    DatenightApi api;


    @Override
    public void createEphemeralKey(@NonNull @Size(min = 4) String apiVersion, @NonNull final EphemeralKeyUpdateListener keyUpdateListener) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Log.i(TAG, mAuth.getCurrentUser().getUid());

        //get instance of api
        api = RetrofitFactory.getInstance().create(DatenightApi.class);

        final Map<String, String> apiParamMap = new HashMap<>();
        apiParamMap.put("api_version", apiVersion);

        Call<UserObject> userObjectCall = api.createCustomerEphemeralKey(mAuth.getCurrentUser().getUid(), apiParamMap);

        userObjectCall.enqueue(new Callback<UserObject>() {
            @Override
            public void onResponse(Call<UserObject> call, Response<UserObject> response) {
                if (!response.isSuccessful()) {
                    Log.i("Error", "The error code while getting the response " + response.code() + "\n" + response.message());
                    return;
                }

                UserObject user = response.body();
                assert user != null;
                Log.i(TAG, user.getMessage() + "\n" + user.getData() + "\n" + user.isSuccess() + "\n" + user.getData().getId());

                Map<String, Object> EphObjMap = new HashMap<>();

                EphObjMap.put("id", user.getData().getId());
                EphObjMap.put("object", user.getData().getObject());
                EphObjMap.put("created", user.getData().getCreated());
                EphObjMap.put("expires", user.getData().getExpires());
                EphObjMap.put("livemode", user.getData().isLivemode());
                EphObjMap.put("secret", user.getData().getSecret());

                List<Map<String, Object>> associated_objects = new ArrayList<>();

                Map<String, Object> assMap = new HashMap<>();
                assMap.put("type",user.getData().getAssociated_objects().get(0).getType());
                assMap.put("id",user.getData().getAssociated_objects().get(0).getId());

                associated_objects.add(assMap);

                EphObjMap.put("associated_objects", associated_objects);

                String jsonBody = new Gson().toJson(EphObjMap);
                Log.i(TAG, jsonBody);

                final String rawKey = user.getData().toString(); //data gotten in this case is ephemeralKey Object
                keyUpdateListener.onKeyUpdate(jsonBody);


            }

            @Override
            public void onFailure(Call<UserObject> call, Throwable t) {
                Log.i("EphProvider", t.getMessage());
            }
        });
    }

    private void setUpNetworkRequest() {
        Gson gson = new GsonBuilder().serializeNulls().create();//to be able to see null value fields

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
}
