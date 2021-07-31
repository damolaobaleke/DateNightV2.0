package com.ltd_immersia_datenight.views.authentication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ltd_immersia_datenight.R;
import com.ltd_immersia_datenight.modelfirestore.User.UserModel;
import com.ltd_immersia_datenight.modelfirestore.User.UserStatsModel;
import com.ltd_immersia_datenight.network.api.DatenightApi;
import com.ltd_immersia_datenight.network.api.UserObject;
import com.ltd_immersia_datenight.utils.constants.DatabaseConstants;
import com.ltd_immersia_datenight.views.datehub_navigation.DateHubNavigation;
import com.ltd_immersia_datenight.views.landing_screen.BoardingScreen;

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
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.util.Patterns;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    FirebaseAuth mAuth;
    String userId;
    FirebaseDatabase database;
    EditText emailInput, fullNameInput;
    EditText passwordInput;
    EditText verifyPasswordInput;
    EditText usernameInput;
    EditText ageInput;
    Button signUp;
    CheckBox terms;
    TextView LogIn, termsText;
    ProgressBar load;
    FirebaseFirestore db;
    DocumentReference userRef, userNameRef;
    Task<AuthResult> task1;
    private static final String TAG = "SignUpActivity";

    private static final String BASE_URL = "https://api.immersia.co.uk"; //https://api.immersia.co.uk http://172.20.10.7:3000
    DatenightApi api;

    CollectionReference usernames;
    Editable mUserNameText;
    CharSequence mUserName;
    boolean usernameAvailable = true;
    static String fcmToken;

    View.OnFocusChangeListener ageFocusListener;

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
        verifyPasswordInput = findViewById(R.id.verifyPasswordInput);
        usernameInput = findViewById(R.id.username);
        fullNameInput = findViewById(R.id.fullNameInput);
        ageInput = findViewById(R.id.Age);
        ageFocusListener = (v, hasFocus) -> {
            if (hasFocus){
                chooseAge(ageInput);
            } else {
                hideKeyboardAfterDobSelected();
            }
        };
        ageInput.setOnFocusChangeListener(ageFocusListener);

        signUp = findViewById(R.id.Sign_Up);
        LogIn = findViewById(R.id.loginText);
        terms = findViewById(R.id.checkTerms);
        termsText = findViewById(R.id.Terms);

        usernames = db.collection("usernames");

        LogIn.setOnClickListener(this);

        signUp.setOnClickListener(v -> {
            signUp();
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
            progressBarShown();
            Log.e(TAG, "Within signUp()");
            // Check if username is already taken
            usernameAvailable = true;
            usernames.whereEqualTo("username", usernameInput.getText().toString().toLowerCase().trim())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                if (task.getResult().isEmpty()){
                                    createUser();
                                } else {
                                    usernameInput.setError("Username taken. Please try a different username");
                                    toast("Username taken. Please try a different username");
                                    progressBarGone();
                                }
                            }
                        }
            });
        }
    }

    public void createUser(){
        progressBarShown();
        Log.i("User Details", emailInput.getText().toString().trim().toLowerCase() + passwordInput.getText().toString());
        mAuth.createUserWithEmailAndPassword(emailInput.getText().toString().trim().toLowerCase(), passwordInput.getText().toString())
                .addOnSuccessListener(authResult -> Toast.makeText(this, "Sign up successful, please verify your email address", Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(this, task -> {
                    progressBarGone();
                    Log.e(TAG, "user creation complete");
                    task1 = task;
                    if (task1.isSuccessful()) {
                        Log.e(TAG, "user creation complete and successful");
                        addUserDetailsToDb();

                        // Verify user
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        user.sendEmailVerification().addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "Email sent.", Toast.LENGTH_SHORT).show();
                                emailInput.setText("");
                                passwordInput.setText("");
                                goToLogin();
                            } else {
                                Log.e(TAG, task2.getException().getMessage());
                            }
                        });
                    } else {
                        if(task.getException() instanceof FirebaseAuthUserCollisionException){
                            // Email already in use by another user
                            emailInput.setError("This email address is already in use by another user");
                            Toast.makeText(SignUpActivity.this, "Email address entered is already in use by another user.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                        Log.e(TAG, "Error while trying to create user" + task.getException().getMessage());
                        task.getException().printStackTrace();
                        updateUI(null);
                    }
                });
    }

    public void addUserDetailsToDb() {
        userId = mAuth.getCurrentUser().getUid();
        List<String> dateIds = new ArrayList<>();
        List<String> purchasedExperiences = new ArrayList<>();
        purchasedExperiences.add("");

        HashMap<String, String> avatar = new HashMap<>();
        avatar.put("avatarUrl", "");

        Log.d(TAG, "The fcm generated: " + generateFcmToken());

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.i(TAG, "Fetching FCM registration token failed", task.getException());
                return;
            }
            // Get new FCM registration token
            fcmToken = task.getResult();
        });

        UserStatsModel userStats = new UserStatsModel(0, 0, 0);
        UserModel userModel = new UserModel(mAuth.getCurrentUser().getUid(),
                                            usernameInput.getText().toString().trim().toLowerCase(),
                                            fullNameInput.getText().toString().trim().toLowerCase(),
                                            emailInput.getText().toString().trim().toLowerCase(),
                                            dateStringToTimestamp(ageInput.getText().toString()), avatar,
                                            "BASIC", DatabaseConstants.LOCAL_AUTH,
                                            dateIds, userStats, null,
                                            "",
                                            new Timestamp(mAuth.getCurrentUser().getMetadata().getCreationTimestamp() / 1000, 0),
                                            false, fcmToken);

        userRef = db.collection("userData").document(userId);
        userNameRef = db.collection("usernames").document(usernameInput.getText().toString().trim().toLowerCase());

        userRef.set(userModel).addOnSuccessListener(aVoid -> {
            Toast.makeText(SignUpActivity.this, "created successfully", Toast.LENGTH_SHORT).show();
            /*create stripe customer --POST REQUEST TO ENDPOINT*/
            setUpNetworkRequest();
            createStripeCustomer();
            /*create stripe customer --POST REQUEST TO ENDPOINT*/

        }).addOnFailureListener(e -> {
            Log.e(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
        });

        Map<String, String> username = new HashMap<>();
        username.put("username", usernameInput.getText().toString().trim().toLowerCase());
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
        UserModel userModel = new UserModel(mAuth.getCurrentUser().getUid(), usernameInput.getText().toString().toLowerCase(), fullNameInput.getText().toString(), emailInput.getText().toString(), dateStringToTimestamp(ageInput.getText().toString()), null, "BASIC", DatabaseConstants.LOCAL_AUTH, null, null, null, "", new Timestamp(mAuth.getCurrentUser().getMetadata().getCreationTimestamp() / 1000, 0), false, "");


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
                Log.e(TAG, e.getMessage());
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        // Validate email
        String emailErr = validateEmail(emailInput.getText().toString().trim().toLowerCase());
        emailInput.setError(emailErr);
        if (emailErr != null){
            valid = false;
        }

        // Validate username
        String userNameErr = validateUsername(usernameInput.getText().toString().trim().toLowerCase());
        usernameInput.setError(userNameErr);
        if (userNameErr != null){
            valid = false;
        }

        if (TextUtils.isEmpty(fullNameInput.getText().toString().trim())) {
            fullNameInput.setError("Please enter a display name to proceed");
            valid = false;

        } else {
            fullNameInput.setError(null);
        }

        // Validate Password
        String passErr = validatePassword(passwordInput.getText().toString());
        passwordInput.setError(passErr);
        if (passErr != null){
            valid = false;
        }

        // Verify Password
        String verPassErr = validatePasswordVerification(passwordInput.getText().toString(), verifyPasswordInput.getText().toString());
        verifyPasswordInput.setError(verPassErr);
        if (verPassErr != null){
            valid = false;
        }

        // Validate age
        String errDob;
        try{
            errDob = validateAgeIsGreaterThan18(ageInput.getText().toString());
            ageInput.setError(errDob);
            if (errDob != null){
                valid = false;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error while trying to validate age");
            ageInput.setText("");
            errDob = "Please enter your date of birth to proceed";
            ageInput.setError(errDob);
            valid = false;
        }

        // Check terms
        if(!terms.isChecked() && valid){
            toast("Please accept terms and conditions to proceed");
            valid = false;
        }

        //returns false when fields are empty
        return valid;
    }

    private String validateUsername(String username){
        String errorMessage = null;

        final String USERNAME_PATTERN = "^[a-z-A-Z-0-9_]+";
        if (TextUtils.isEmpty(username)) {
            errorMessage = "Please enter a username to proceed";
        } else if(!username.matches(USERNAME_PATTERN)){
            errorMessage = "Your username can only contain letters, numbers, and _";
        }
        return errorMessage;
    }

    private String validateEmail (String email){
        String errorMessage = null;
        if(TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            errorMessage = "Please enter a valid email address to proceed";
        }
        return errorMessage;
    }

    private String validatePassword(String password){
        String errorMessage = null;
        final String PASSWORD_PATTERN = ".*[a-z]+.*";
        final String PASSWORD_PATTERN_2 = ".*[A-Z]+.*";
        final String PASSWORD_PATTERN_3 = ".*[0-9]+.*";
        final String PASSWORD_PATTERN_4 = ".*[!@#$&*_\\\\-]+.*";
        if(password.length() < 8 ||
            !(password.matches(PASSWORD_PATTERN) && password.matches(PASSWORD_PATTERN_2) &&
            password.matches(PASSWORD_PATTERN_3) && password.matches(PASSWORD_PATTERN_4)))
        {
            Log.e(TAG, String.valueOf(password.matches(PASSWORD_PATTERN)));
            Log.e(TAG, String.valueOf(password.matches(PASSWORD_PATTERN_2)));
            Log.e(TAG, String.valueOf(password.matches(PASSWORD_PATTERN_3)));
            Log.e(TAG, String.valueOf(password.matches(PASSWORD_PATTERN_4)));

            errorMessage = "Password must be a minimum of 8 characters and must contain uppercase, lowercase, number and one of !, @, #, $, &, *, _, \\, -";
        }
        return errorMessage;
    }

    private String validatePasswordVerification(String password, String passwordReentered){
        String errorMessage = null;
        if (password.isEmpty()){
            errorMessage = "Please enter a valid password";
        } else if (passwordReentered.isEmpty()){
            errorMessage = "Please re-enter password";
        } else if (!password.equals(passwordReentered)){
            errorMessage = "Passwords do not match";
        }
        return errorMessage;
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
        load = findViewById(R.id.progressBar5);
        load.setVisibility(View.VISIBLE);
    }

    public void progressBarGone() {
        load = findViewById(R.id.progressBar5);
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
        ageInput.clearFocus();
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

    private String validateAgeIsGreaterThan18(String ageInput) throws ParseException {
        String errorMessage = null;

        Calendar date18YearsAgo = Calendar.getInstance();
        date18YearsAgo.add(Calendar.YEAR, -18);

        Calendar calendarBirthday = Calendar.getInstance();
        calendarBirthday.setTime(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(ageInput));

        if (ageInput.isEmpty()){
            errorMessage = "Please enter your birthday to proceed";
        } else if(calendarBirthday.after(date18YearsAgo)){
            errorMessage = "You must be 18 to use DateNight";
        }

        return errorMessage;
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

    public void toast(String message) {
        View view = getLayoutInflater().inflate(R.layout.create_date_toast, null);
        Button b = view.findViewById(R.id.toast_btn);

        Toast toast = new Toast(this);
        b.setText(message);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    private static String generateFcmToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                return;
            }
            // Get new FCM registration token
            fcmToken = task.getResult();
            // Log
            Log.d(TAG, "The fcm: " + fcmToken);
        });
        return fcmToken;
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(SignUpActivity.this, BoardingScreen.class);
        startActivity(intent);
        return false;
    }

    public void hideKeyboardAfterDobSelected() {
        // InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ageInput.getWindowToken(), 0);
    }
}
