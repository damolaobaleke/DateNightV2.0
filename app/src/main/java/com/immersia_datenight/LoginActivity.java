package com.immersia_datenight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    ProgressBar load;
    public static EditText email;
    EditText password;
    Button LogIn, SignUp;

    String Username;
    String Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.emailLogin);
        password = findViewById(R.id.password);
        LogIn = findViewById(R.id.Log_In);
        SignUp = findViewById(R.id.create_account);

        Auth();
        SignUp();

    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
//    }


    public void Auth() {
        //Firebase Auth
        LogIn.setOnClickListener(v -> {
            validateForm();
            progressBarShown();
            logIn();
        });
    }

    public void SignUp() {
        SignUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    public void Date() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time is => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        System.out.println(formattedDate);
    }

    public double encrypt() {
        if (password.getText().toString() != null) {
            return Math.random();
        } else {
            return 1.0;
        }
    }

    public void logIn() {
        boolean formValidated = validateForm();
        try {
            mAuth = FirebaseAuth.getInstance();
            if (formValidated) {
                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                progressBarGone();

                                Log.i("SignIn", "Successful");
                                Toast.makeText(LoginActivity.this, "Signed In", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginActivity.this, DateHubNavigation.class);
                                startActivity(intent);
                            } else {
                                progressBarGone();
                                //Toast.makeText(LoginActivity.this, "", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(this, e -> {
                            Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            Log.i("Failed Auth", e.getLocalizedMessage().toString());
                        });
            }

        } catch (IllegalStateException e) {
            Toast.makeText(LoginActivity.this, "Incorrect Email or Password" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }

//        if(email.getText().length() == 0){
//
//        }
        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError("Required.");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
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
}