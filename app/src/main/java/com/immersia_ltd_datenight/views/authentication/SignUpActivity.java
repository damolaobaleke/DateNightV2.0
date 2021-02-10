package com.immersia_ltd_datenight.views.authentication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.immersia_ltd_datenight.R;
import com.immersia_ltd_datenight.modelfirestore.User.UserModel;
import com.immersia_ltd_datenight.modelfirestore.User.UserStatsModel;
import com.immersia_ltd_datenight.network.api.DatenightApi;
import com.immersia_ltd_datenight.network.api.UserObject;
import com.immersia_ltd_datenight.views.datehub_navigation.DateHubNavigation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    FirebaseAuth mAuth;
    String userId;
    FirebaseDatabase database;
    EditText emailInput, fullNameInput;
    EditText passwordInput;
    EditText usernameInput;
    EditText dateOfBirth;
    EditText confirmPasswordInput;
    EditText ageInput;
    Button signUp;
    CheckBox terms;
    TextView LogIn, termsText, usernameLabel;
    ProgressBar load;
    FirebaseFirestore db;
    DocumentReference userRef, userNameRef;
    Task<AuthResult> task1;
    private static final String TAG = "Sign Up";

    private static final String BASE_URL = "https://api.immersia.co.uk"; //https://api.immersia.co.uk http://172.20.10.7:3000
    DatenightApi api;
    //final CompositeDisposable compositeDisposable;  //to handle async response Reactive Java

    CollectionReference usernames;
    Editable mUserNameText;
    CharSequence mUserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Sign Up");

        //Firestore instance
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        usernameInput = findViewById(R.id.username);
        fullNameInput = findViewById(R.id.fullNameInput);
        ageInput = findViewById(R.id.Age);

        signUp = findViewById(R.id.Sign_Up);
        LogIn = findViewById(R.id.loginText);
        terms = findViewById(R.id.checkTerms);
        termsText = findViewById(R.id.Terms);

        usernames = db.collection("usernames");

        LogIn.setOnClickListener(this);

        checkAge();

        signUp.setOnClickListener(v -> {
            progressBarShown();
            validateForm();
            //checkUserAlreadyExists();
            signUp();
        });
    }

    private void checkUserAlreadyExists() {
        final boolean[] userDoesntExists = {true};

        usernames.whereEqualTo("username", usernameInput.getText().toString().toLowerCase().trim()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                if (queryDocumentSnapshot.exists()) {

                    //toast
                    Toast.makeText(SignUpActivity.this, R.string.valid_username, Toast.LENGTH_SHORT).show();

                    //show error
                    usernameLabel = findViewById(R.id.username_label);
                    usernameLabel.setText(R.string.valid_username);
                    usernameLabel.setTextColor(ContextCompat.getColor(SignUpActivity.this, android.R.color.holo_red_light));

                    //disable button
                    signUp.setEnabled(false);
                    //signUp.setVisibility(View.INVISIBLE);
                    //signUp.setOnClickListener(v -> finish());

                    userDoesntExists[0] = false;
                } else {

                    usernameLabel.setText("Give yourself a username");
                    usernameLabel.setTextColor(ContextCompat.getColor(SignUpActivity.this, android.R.color.black));
                    signUp.setVisibility(View.VISIBLE);
                    signUp.setEnabled(true);
                }
            }
        });

    }

    private void checkAge() {
        ageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    checkAgeIsGreaterThan18(s.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void chooseAge(View v) {
        DatePickerDialog dateDialog = new DatePickerDialog(this, this, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        dateDialog.show();
    }

    public void loadTerms(View v) {
        String url = "https://www.immersia.co.uk/files/Terms%20and%20conditions%20for%20supply%20of%20services%20to%20consumers%20via%20a%20website.pdf";
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent intent = builder.build();
        intent.launchUrl(this, Uri.parse(url));

        //color of addressbar
        int pink = Color.parseColor("#Fc3e7a"); //datenight pink
        int white = Color.parseColor("#Fc3e7a"); //datenight white

        CustomTabColorSchemeParams.Builder colorBuilder = new CustomTabColorSchemeParams.Builder();
        colorBuilder.setToolbarColor(pink);
        colorBuilder.setNavigationBarDividerColor(white);
        colorBuilder.build();

        //builder.setDefaultColorSchemeParams()

    }


    public void signUp() {
        boolean formValidated = validateForm();
        if (formValidated) {
            Log.i("User Details", emailInput.getText().toString() + passwordInput.getText().toString());

            mAuth.createUserWithEmailAndPassword(emailInput.getText().toString(), passwordInput.getText().toString())
                    .addOnSuccessListener(authResult -> Toast.makeText(this, "Registered Successfully, please verify your email address", Toast.LENGTH_SHORT).show())

                    .addOnFailureListener(this, e -> {
                        Log.i("Failed", e.getLocalizedMessage());
                        Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    })

                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            task1 = task;
                            if (task1.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                progressBarGone();

                                //Add User to db
                                createUser();

                                //verify email before going to login
                                FirebaseUser user = mAuth.getCurrentUser();
                                assert user != null;
                                user.sendEmailVerification().addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        Toast.makeText(SignUpActivity.this, "Email sent.", Toast.LENGTH_SHORT).show();
                                        emailInput.setText("");
                                        passwordInput.setText("");

                                        //go to LogIn, then to datehub
                                        goToLogin();
                                    } else {
                                        Toast.makeText(SignUpActivity.this, task2.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });


                            } else {
                                progressBarGone();
                                Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        }
    }

    public void createUser() {
        userId = mAuth.getCurrentUser().getUid();

        List<String> dateIds = new ArrayList<>();

        HashMap<String, String> avatar = new HashMap<>();
        avatar.put("avatarUrl", "");

        UserStatsModel userStats = new UserStatsModel(0, 0, 0, 0);
        UserModel userModel = new UserModel(mAuth.getCurrentUser().getUid(), usernameInput.getText().toString().toLowerCase(),
                                            fullNameInput.getText().toString(), emailInput.getText().toString(),
                                            dateStringToTimestamp(ageInput.getText().toString()), avatar, "BASIC", dateIds, userStats,
                                            "", new Timestamp(mAuth.getCurrentUser().getMetadata().getCreationTimestamp()/1000, 0),false);

        userRef = db.collection("userData").document(userId);
        userNameRef = db.collection("usernames").document(usernameInput.getText().toString().toLowerCase());

        userRef.set(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(SignUpActivity.this, "created successfully", Toast.LENGTH_SHORT).show();
                /*create stripe customer --POST REQUEST TO ENDPOINT*/
                setUpNetworkRequest();
                createStripeCustomer();
                /*create stripe customer --POST REQUEST TO ENDPOINT*/
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
            }
        });

        Map<String, String> username = new HashMap<>();
        username.put("username", usernameInput.getText().toString().toLowerCase());
        userNameRef.set(username);

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
        UserModel userModel = new UserModel(mAuth.getCurrentUser().getUid(), usernameInput.getText().toString().toLowerCase(), fullNameInput.getText().toString(), emailInput.getText().toString(), dateStringToTimestamp(ageInput.getText().toString()), null, "BASIC", null, null, "", new Timestamp(mAuth.getCurrentUser().getMetadata().getCreationTimestamp()/1000, 0),false);


        Call<UserObject> userObjectCall = api.createStripeCustomer(userModel);

        userObjectCall.enqueue(new Callback<UserObject>() {
            @Override
            public void onResponse(@NotNull Call<UserObject> call, @NotNull Response<UserObject> response) {
                if (!response.isSuccessful()) {
                    Log.i("Error", "The error code while getting the response " + response.code() + "\n" + response.message());
                    return; //Leave method, data would be null if response not successful
                }

                UserObject user = response.body();

                assert user != null;
                Log.i(TAG, user.isSuccess() + " " + user.getMessage() + " " + user.getUserData().getEphemeralKey());
            }

            @Override
            public void onFailure(Call<UserObject> call, Throwable e) {
                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        if (TextUtils.isEmpty(emailInput.getText().toString())) {
            emailInput.setError("Required.");
            progressBarGone();
            valid = false;
        } else {
            emailInput.setError(null);
            progressBarShown();
        }

        if (TextUtils.isEmpty(passwordInput.getText().toString()) || passwordInput.getText().length() < 8) {
            passwordInput.setError("Required." + "You must have a minimum of 8 characters in your password");
            progressBarGone();
            valid = false;
        } else {
            passwordInput.setError(null);
            progressBarShown();
        }

        //returns false when fields are empty
        return valid;
    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            String name = user.getDisplayName();
            Log.i("Name", name);

            goToDatehub();

            Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();

        }
    }

    public void progressBarShown() {
        load = findViewById(R.id.progressBar2);
        load.setVisibility(View.VISIBLE);
    }

    public void progressBarGone() {
        load = findViewById(R.id.progressBar2);
        load.setVisibility(View.INVISIBLE);
    }

    public void goToDatehub() {
        Intent intent = new Intent(SignUpActivity.this, DateHubNavigation.class);
        SignUpActivity.this.startActivity(intent);

    }

    private void goToLogin() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        ageInput.setText(String.format(Locale.US, "%d-%d-%d", dayOfMonth, month + 1, year)); //due to january in index pos is 0
    }

    public static Timestamp dateStringToTimestamp(String dateStr) {
        try {
            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Date date = formatter.parse(dateStr);
            assert date != null;
            //convert date to timestamp
            return new Timestamp(date);
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
            return null;
        }
    }

    private void checkAgeIsGreaterThan18(String ageInput) throws ParseException {
        Calendar calendarBirthday = Calendar.getInstance();
        Calendar calendarToday = Calendar.getInstance();

        calendarBirthday.setTime(new SimpleDateFormat("dd-MM-yyyy", Locale.US).parse(ageInput));

        int yearOfToday = calendarToday.get(Calendar.YEAR);
        int yearOfBirthday = calendarBirthday.get(Calendar.YEAR);

        if (yearOfToday - yearOfBirthday > 18) {
            signUp.setEnabled(true);

        } else if (yearOfToday - yearOfBirthday == 18) {

            int monthOfToday = calendarToday.get(Calendar.MONTH);
            int monthOfBirthday = calendarBirthday.get(Calendar.MONTH);

            if (monthOfToday > monthOfBirthday) {
                signUp.setEnabled(true);
            } else if (monthOfToday == monthOfBirthday) {

                if (calendarToday.get(Calendar.DAY_OF_MONTH) >= calendarBirthday.get(Calendar.DAY_OF_MONTH)) {
                    signUp.setEnabled(true);

                } else {
                    signUp.setEnabled(false);
                    Toast.makeText(this, "You have to be 18", Toast.LENGTH_SHORT).show();
                }
            } else {
                signUp.setEnabled(false);
                Toast.makeText(this, "You have to be 18", Toast.LENGTH_SHORT).show();
            }
        } else {
            signUp.setFocusable(false);
            signUp.setEnabled(false);
            Toast.makeText(this, "You have to be 18", Toast.LENGTH_SHORT).show();
        }
    }

    public static Timestamp dateCreated() {
        try {
            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Date date = formatter.parse(String.valueOf(Calendar.getInstance().getTime()));
            Log.i("", "Today is " + date);

            //convert date to timestamp
            return new Timestamp(date);

        } catch (ParseException e) {
            System.out.println("Exception :" + e);
            return null;
        }
    }

    public static String timeStamptoString(Timestamp timestamp) {
        // hours*minutes*seconds*milliseconds  int oneDay = 24 * 60 * 60 * 1000;
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        Date date = timestamp.toDate();
        String dateofbirth = formatter.format(date);
        return dateofbirth;
    }

    public static Date stringToDate(String dateStr) throws ParseException {
        Date date = DateFormat.getInstance().parse(dateStr);
        return date;
    }


}
