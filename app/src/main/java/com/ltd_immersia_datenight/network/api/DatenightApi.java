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

package com.ltd_immersia_datenight.network.api;

import com.ltd_immersia_datenight.*;
import com.ltd_immersia_datenight.modelfirestore.User.UserModel;
import com.ltd_immersia_datenight.modelfirestore.avatar.RenderObject;


import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DatenightApi {

    @POST("/create-customer")
    Call<UserObject> createStripeCustomer(@Body UserModel user);

    @FormUrlEncoded
    @POST("/stripe-customer/ephemeral")
    Call<UserObject> createCustomerEphemeralKey(@Query("id") String userId, @FieldMap Map<String, String> apiVersion);  //auth.getId()

    @POST("/create-payment-intent")
    Call<UserObject> getClientSecret(@Query("id") String userId, @Query("amount") double amount);

    @FormUrlEncoded
    @POST("/stripe-customer/ephemeral")
    Call<UserObject> createCustomerEphemeral(@Field("id") String userId);

    //Create 2D avatar URL
    @POST("/render")
    Call<RenderObject> getAvatarUrl(@Body Map<String, String> avatarParams);
}
