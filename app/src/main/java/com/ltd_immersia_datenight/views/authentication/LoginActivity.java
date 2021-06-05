package com.ltd_immersia_datenight.views.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ltd_immersia_datenight.R;
import com.ltd_immersia_datenight.modelfirestore.User.UserModel;
import com.ltd_immersia_datenight.network.api.DatenightApi;
import com.ltd_immersia_datenight.network.api.UserObject;
import com.ltd_immersia_datenight.utils.constants.DatabaseConstants;
import com.ltd_immersia_datenight.utils.DateNight;
import com.ltd_immersia_datenight.utils.stripe.config.DateNightEphemeralKeyProvider;
import com.ltd_immersia_datenight.views.datehub_navigation.DateHubNavigation;
import com.ltd_immersia_datenight.views.landing_screen.BoardingScreen;
import com.stripe.android.CustomerSession;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String BASE_URL = "https://api.immersia.co.uk"; //http://172.20.10.7:3000
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    ProgressBar load;
    public static EditText email;
    TextInputEditText password;
    Button LogIn;
    TextView SignUp;
    private DocumentReference userDocRef;
    private FirebaseFirestore db;
    private String fcmToken;

    String Username;
    String Password;

    DatenightApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Log In");

        email = findViewById(R.id.emailLogin);
        password = findViewById(R.id.pass);
        LogIn = findViewById(R.id.Log_In);
        SignUp = findViewById(R.id.create_account);

        SignUp.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        Auth();
        SignUp();

    }


    public void Auth() {
        //Firebase Auth
        LogIn.setOnClickListener(v -> {
            validateForm();
            progressBarShown();
            logIn();
        });
    }

    public void forgotPassword(View v) {
        Intent intent = new Intent(this, ForgotPassword.class);
        startActivity(intent);
    }


    public void SignUp() {
        SignUp.setOnClickListener(this);
    }

    public void Date() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time is => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        System.out.println(formattedDate);
    }


    public void logIn() {
        boolean formValidated = validateForm();
        try {
            if (formValidated) {

                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                if (mAuth.getCurrentUser().isEmailVerified()) {
                                    progressBarGone();

                                    Log.i("SignIn", "Successful");
                                    Toast.makeText(LoginActivity.this, "Signed In", Toast.LENGTH_SHORT).show();

                                    //update OnBoarded to true
                                    updateOnBoarded();
                                    //create stripe customer again, as iOS never sends a request - explicit check majorly for USECASE(iOS user logging in on android)
                                    setUpNetworkRequest();
                                    createStripeCustomer();

                                    //goToDatehub();

                                    //set FCM
                                    setFcm();
                                    /*STRIPE -- initialize customer session to retrieve ephemeral key from server side*/
                                    CustomerSession.initCustomerSession(this, new DateNightEphemeralKeyProvider());

                                    DateNight appState = ((DateNight) this.getApplication());
                                    if (appState.getAppData(mAuth.getUid()) == null) {
                                        // Fetch required launch data and then launch DateHubNavigation class
                                        appState.initializeAppData(mAuth.getUid(), LoginActivity.this);
                                    } else {
                                        goToDatehub();
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "Please verify your email address", Toast.LENGTH_SHORT).show();
                                    progressBarGone();
                                }
                            } else {
                                progressBarGone();
                                //Toast.makeText(LoginActivity.this, "", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(this, e -> {
                            Log.e("Failed Auth", e.getLocalizedMessage());
                        });
            }

        } catch (IllegalStateException e) {
            Log.e(TAG, e.getLocalizedMessage());
            Toast.makeText(LoginActivity.this, "Incorrect Email or Password", Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.i("Error", e.getLocalizedMessage());
            Toast.makeText(LoginActivity.this, "Enter Your Email & Password", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            Log.i("Error", e.getLocalizedMessage());
            progressBarGone();
            Toast.makeText(LoginActivity.this, "Enter Your Email & Password", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError("Required, enter your email");
            valid = false;
        } else {
            email.setError(null);
        }

        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError("Required.");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }

    private void goToDatehub() {
        Intent intent = new Intent(LoginActivity.this, DateHubNavigation.class);
        startActivity(intent);
    }

    private void updateOnBoarded() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userDocRef = db.collection(DatabaseConstants.USER_DATA_NODE).document(mAuth.getCurrentUser().getUid());

        userDocRef.update("onBoarded", true);
    }

    public void progressBarGone() {
        load = findViewById(R.id.progressBar3);
        load.setVisibility(View.INVISIBLE);
    }

    public void progressBarShown() {
        load = findViewById(R.id.progressBar3);
        load.setVisibility(View.VISIBLE);
    }


    public void updateUI(FirebaseUser user) {
        if (user != null) {
            String name = user.getDisplayName();
            Log.i("Name", name);

            Intent intent = new Intent(LoginActivity.this, DateHubNavigation.class);
            startActivity(intent);

            Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
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

    private void createStripeCustomer() {
        UserModel userModel = new UserModel(mAuth.getCurrentUser().getUid(), "", "", email.getText().toString(), null, null, "BASIC", null, null, null, null, "", Timestamp.now(), false, "");

        Call<UserObject> userObjectCall = api.createStripeCustomer(userModel);

        userObjectCall.enqueue(new Callback<UserObject>() {
            @Override
            public void onResponse(@NotNull Call<UserObject> call, @NotNull Response<UserObject> response) {
                if (!response.isSuccessful()) {
                    Log.e("Error", "The error code while getting the response " + response.code() + "\n" + response.message());
                    return; //Leave method, data would be null if response not successful
                }

                UserObject user = response.body();

                assert user != null;
                Log.i("LoginIn", user.isSuccess() + " " + user.getMessage() + " " + user.getUserData().getEphemeralKey());
            }

            @Override
            public void onFailure(Call<UserObject> call, Throwable e) {
                Log.e("Error", e.getMessage());
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, BoardingScreen.class);
        startActivity(intent);
        return false;
    }

    private String generateFcmToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.i(TAG, "Fetching FCM registration token failed", task.getException());
                return;
            }
            // Get new FCM registration token
            fcmToken = task.getResult();
            // Log
            Log.d(TAG, "The fcm: " + fcmToken);
        });
        return fcmToken;
    }

    public void setFcm() {
        /**Re-GENERATE and update FCM HERE*/
        userDocRef = db.collection(DatabaseConstants.USER_DATA_NODE).document(mAuth.getCurrentUser().getUid());
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.i(TAG, "Fetching FCM registration token failed", task.getException());
                return;
            }
            // Get new FCM registration token
            fcmToken = task.getResult();
            Log.d(TAG, "The fcm: " + fcmToken);
            userDocRef.update("fcmToken", fcmToken);
        });
    }
}