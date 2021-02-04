package com.datenight_immersia_ltd.network.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface FundstrtrApi {

    //Retrieve Unity Object , each uid represents the Environment
    //@GET("/unity-environment/:id/paris")
    //Call<EnvironmentObject> getEnvironment(@Path("id") String environmentId); //annotate to tell retrofit to parse variable into block

    //Retrieve all pitches
    @GET("/investopp")
    Call<PitchLists> getPitches();
    //Call<List<Pitch>> getPitches(); If the response was just an array of object, but its object which has an array of objects

    //Retrieve each individual pitch
    @GET("/investopp/pitches/{id}/details")
    Call<PitchObject> getPitch(@Path("id") String pitchId); //annotate to tell retrofit to parse variable into block

    //Retrieve array of comments for pitch
    @GET("/investopp/pitch/{id}/discussion")
    Call<List<Pitch>> getDiscussions(@Path("id") String pitchId);

//    @POST("/signup")
//    Call<PostAuthUser> signUp(@Header("token") String token, @Body PostAuthUser user);


    //Another method to post to server

    //@Headers({"Static-Header: 123", ""})

    //form url encoding
//    @FormUrlEncoded
//    @POST("/login")
//    Call<PostAuthUser> logIn(@Header ("token") String token, @Field("email") String email, @Field("password") String password);
//
//    @PUT("/my-profile/complete-form/{id}")
//    Call<User> updateUser(@Path("id") String userId, @Body User userbody);


    //--Testing consuming endpoint with or (if) query string e.g /post?userId=1
    //@GET("/investopp")
    //Call<PitchLists> getPosts(@Query("userId") int userId, @Query("_sort") String sort, @Query("order") String order);

    //@GET("/investopp")
    //Call<PitchLists> getPosts(@QueryMap Map<String, String> parameters);

    //@FormUrlEncoded
    //@POST("/login")  //form url encoding
    //Call<AuthUser>  logIn(@Field("email") String email, @Field("password") String password);

//    @FormUrlEncoded
//    @POST("/login")  //form url encoding
//    Call<AuthUser>  logIn(@FieldMap Map<String, ?> fields);

}
