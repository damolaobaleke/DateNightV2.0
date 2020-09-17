package com.immersia_datenight;

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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    EditText emailInput;
    EditText passwordInput;
    EditText confirmPasswordInput;
    EditText ageInput;
    Button signUp;
    CheckBox terms;
    Button LogIn;
    ProgressBar load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        ageInput = findViewById(R.id.Age);

        signUp = findViewById(R.id.Sign_Up);
        LogIn = findViewById(R.id.Log_Up);
        terms = findViewById(R.id.checkTerms);

        LogIn.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        signUp.setOnClickListener(v -> {
            progressBarShown();
            validateForm();
            signUp();
        });


    }

    public void chooseAge(View v) {
        /*Date of Birth*/
        Calendar c = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {

            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            ageInput.setText(String.format(Locale.US, "%d-%d-%d", dayOfMonth, month, year));
        };

        ageInput.setOnClickListener(d ->
                new DatePickerDialog(this, dateSetListener,
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)).show()
        );

        //        ArrayList<String> age = new ArrayList<>();
        //        age.add(0,"Choose your age range...");
        //        age.add(1, "14-17");
        //        age.add(2,"18-25");
        //        age.add(3,"25-30");
        //        age.add(4,"30-35");
        //
        //        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,age);
        //        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //
        //        ageSpinner.setAdapter(arrayAdapter);
        //        Log.i("Age", ageSpinner.getSelectedItem().toString());

    }

    public String ageValidate() {
        /*Algorithm if age < 18 based on date picked throw error*/

        return "";
    }

    public void signUp() {
        boolean formValidated = validateForm();
        if (formValidated) {
            Log.i("User Details", emailInput.getText().toString() + passwordInput.getText().toString());

            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(emailInput.getText().toString(), passwordInput.getText().toString())
                    .addOnSuccessListener(authResult -> Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show())

                    .addOnFailureListener(this, e -> {
                        Log.i("Failed", e.getLocalizedMessage().toString());
                        Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    })
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressBarGone();

                            //ADD USER TO DB
                            database = FirebaseDatabase.getInstance();
                            database.getReference("Users").child(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid()).child("email").setValue(emailInput.getText().toString());

                            Intent intent = new Intent(SignUpActivity.this, DateHubNavigation.class);
                            startActivity(intent);

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                        } else {
                            progressBarGone();
                            Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    });
        }
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

            Intent intent = new Intent(SignUpActivity.this, DateHubNavigation.class);
            startActivity(intent);

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
}
