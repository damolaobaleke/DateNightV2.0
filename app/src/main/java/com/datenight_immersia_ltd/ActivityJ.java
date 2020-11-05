package com.datenight_immersia_ltd;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.datenight_immersia_ltd.network.api.AuthUser;
import com.datenight_immersia_ltd.network.api.FundstrtrApi;
import com.datenight_immersia_ltd.network.api.Pitch;
import com.datenight_immersia_ltd.network.api.PitchLists;
import com.datenight_immersia_ltd.network.api.PitchObject;
import com.datenight_immersia_ltd.network.api.PostAuthUser;
import com.datenight_immersia_ltd.network.api.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityJ extends AppCompatActivity {
    public EditText email;
    private EditText password;
    private EditText username;
    TextView teamMember;
    FundstrtrApi api;
    public static String BASE_URL = "http://192.168.88.24:3002/"; //http://192.168.88.24:3002/ http://api.fundstrtr.com/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_pitches);

        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextPassword);
        username = findViewById(R.id.editTextTextPersonName);
        teamMember = findViewById(R.id.textView9);

        Gson gson = new GsonBuilder().serializeNulls().create();//====

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
        api = retrofit.create(FundstrtrApi.class);

        getAllPitches();
        //getIndividualPitch();

        Button SignUp = findViewById(R.id.button);
        SignUp.setOnClickListener(v -> {
            signUp();
            //logIn();
        });

    }

    public void getAllPitches() {

        Call<PitchLists> call = api.getPitches();

        call.enqueue(new Callback<PitchLists>() {
            @Override
            public void onResponse(Call<PitchLists> call, Response<PitchLists> response) {
                if (!response.isSuccessful()) {
                    Log.i("Error", "The error code while getting the response" + response.code());
                    return; //Leave method, data would be null if response not successful
                }
                PitchLists lists = response.body();
                Log.i("RESPONSE", "The response body is " + lists);

                assert lists != null;
                Log.i("Pitch response", lists.getMessage() + " === " + lists.isSuccess() + "\n");

                List<Pitch> pitch = lists.getPitchLists();
                Log.i("Array Of Pitches", "" + pitch.size());

                for (Pitch p : pitch) {
                    Log.i("Pitch emails", p.getEmail() + "\n");

                    //cant log for each really yet, in earlier pitches no team members had been created
                    //Log.i("Pitch Team Members", p.getTeamMember1().getName() + " " + p.getTeamMember1().getDetails() + "\n");
                    teamMember.setText(pitch.get(2).getTeamMember1().getName() + "\n" + pitch.get(2).getTeamMember1().getPosition());
                }
            }

            @Override
            public void onFailure(Call<PitchLists> call, Throwable t) {
                Log.i("Error getting response", t.getMessage());
            }
        });

    }

    public void getIndividualPitch() {

        Call<PitchObject> call = api.getPitch("5efa0b405c660a2828420bc0");

        call.enqueue(new Callback<PitchObject>() {
            @Override
            public void onResponse(Call<PitchObject> call, Response<PitchObject> response) {
                if (!response.isSuccessful()) {
                    Log.i("Error", "The error code while getting the response" + response.code());
                    return; //Leave method, data would be null if response not successful
                }
                PitchObject pitchObject = response.body();

                assert pitchObject != null;
                Pitch p = pitchObject.getPitch();


                Log.i("Pitch response", p.getEmail() + " " + p.getFirstname() + "\n");

            }

            @Override
            public void onFailure(Call<PitchObject> call, Throwable t) {
                Log.i("Error getting response", t.getMessage());
            }
        });


    }

    public void signUp() {
        User user = new User(null, "Tomi@gmail.com", "Tomi", "rolly2u", null, null, null, null, null, false, false, null, null, 0);

        AuthUser postUser = new AuthUser(false, null, user);

        PostAuthUser user1 = new PostAuthUser(null, postUser);

        Call<PostAuthUser> call = api.signUp("mdfmio34s234",user1);

        call.enqueue(new Callback<PostAuthUser>() {
            @Override
            public void onResponse(Call<PostAuthUser> call, Response<PostAuthUser> response) {
                if (!response.isSuccessful()) {
                    Log.i("Error", "The error code while getting the response " + response.code() + "\n" + response.message());
                    return; //Leave method, data would be null if response not successful
                }

                PostAuthUser postResponse = response.body(); //post response body

                assert postResponse != null;
                Log.i("POST RESPONSE SIGNUP:", "User authenticated = " + postResponse.getMessage() + "\n" + "The response code " + response.code());

                Log.i("POST SIGNUP ", postResponse.getUserData().getUser().getEmail() + postResponse.getUserData().getUser().getUsername());
            }

            @Override
            public void onFailure(Call<PostAuthUser> call, Throwable t) {
                Toast.makeText(ActivityJ.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    public void logIn() {
        Call<PostAuthUser> call = api.logIn(null,"bezos@gmail.com", "rolly2u");

        call.enqueue(new Callback<PostAuthUser>() {
            @Override
            public void onResponse(Call<PostAuthUser> call, Response<PostAuthUser> response) {
                if (!response.isSuccessful()) {
                    Log.i("Error", "The error code while getting the response " + response.code() + "\n" + response.message());
                    return; //Leave method, data would be null if response not successful
                }

                PostAuthUser userResponse = response.body();

                assert userResponse != null;
                AuthUser user = userResponse.getUserData();

                Toast.makeText(ActivityJ.this, userResponse.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("RESPONSE", userResponse.getMessage() + "\n" + userResponse.getUserData().getUser().getEmail() + " " + user.getUser().getUsername());

                TextView Username = findViewById(R.id.textView12);
                Username.setText(user.getUser().getUsername());
            }

            @Override
            public void onFailure(Call<PostAuthUser> call, Throwable t) {
                Log.i("Error getting response", t.getMessage());
            }
        });
    }
}
