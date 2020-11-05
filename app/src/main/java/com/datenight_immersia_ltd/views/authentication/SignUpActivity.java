package com.datenight_immersia_ltd.views.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.datenight_immersia_ltd.ActivityJ;
import com.datenight_immersia_ltd.model.User.UserObject;
import com.datenight_immersia_ltd.modelfirestore.User.UserModel;
import com.datenight_immersia_ltd.modelfirestore.User.UserStatsModel;
import com.datenight_immersia_ltd.views.datehub_navigation.DateHubNavigation;
import com.datenight_immersia_ltd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    FirebaseAuth mAuth;
    String userId;
    FirebaseDatabase database;
    EditText emailInput;
    EditText passwordInput;
    EditText username;
    EditText dateOfBirth;
    EditText confirmPasswordInput;
    EditText ageInput;
    Button signUp;
    CheckBox terms;
    TextView LogIn;
    ProgressBar load;
    FirebaseFirestore db;
    DocumentReference userRef;
    Task<AuthResult> task1;
    private static String TAG = "Sign Up";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //Firestore instance
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        username = findViewById(R.id.username);
        ageInput = findViewById(R.id.Age);

        signUp = findViewById(R.id.Sign_Up);
        LogIn = findViewById(R.id.loginText);
        terms = findViewById(R.id.checkTerms);

        LogIn.setOnClickListener(this);

        signUp.setOnClickListener(v -> {
            progressBarShown();
            validateForm();
            signUp();
        });


    }

    public void chooseAge(View v) {
        DatePickerDialog dateDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        dateDialog.show();
    }

    public String ageValidate() {
        /*Algorithm if age < 18 based on date picked throw error*/
        return "";
    }

    public void signUp() {
        boolean formValidated = validateForm();
        if (formValidated) {
            Log.i("User Details", emailInput.getText().toString() + passwordInput.getText().toString());

            mAuth.createUserWithEmailAndPassword(emailInput.getText().toString(), passwordInput.getText().toString())
                    .addOnSuccessListener(authResult -> Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show())

                    .addOnFailureListener(this, e -> {
                        Log.i("Failed", e.getLocalizedMessage().toString());
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
                                goToDatehub();

                            } else {
                                SignUpActivity.this.progressBarGone();
                                Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                SignUpActivity.this.updateUI(null);
                            }
                        }
                    });
        }
    }

    public void createUser() {
//        database = FirebaseDatabase.getInstance();
//        database.getReference("Users").child(task1.getResult().getUser().getUid()).child("email").setValue(emailInput.getText().toString());
        userId = mAuth.getCurrentUser().getUid();

        UserStatsModel userStats = new UserStatsModel(0, 0, 0);
        UserModel userModel = new UserModel(username.getText().toString(), null, null, emailInput.getText().toString(), dateStringToTimestamp(ageInput.getText().toString()), null, false,null,null, userStats);

        userRef = db.collection("users").document(userId);
        userRef.set(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(SignUpActivity.this, "created successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
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

//        if(email.getText().length() == 0){
//
//        }
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
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();

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

    public void startJsonActivity() {
        Intent intent = new Intent(this, ActivityJ.class);
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
