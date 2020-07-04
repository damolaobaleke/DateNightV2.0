package com.datenight;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {
    EditText emailInput;
    EditText passwordInput;
    EditText confirmPasswordInput;
    Spinner ageSpinner;
    Button signUp ;
    CheckBox terms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        ageSpinner = findViewById(R.id.spinnerAge);

        signUp = findViewById(R.id.Sign_Up);
        terms = findViewById(R.id.checkTerms);


        chooseAge();



    }

    public void chooseAge(){
        ArrayList<String> age = new ArrayList<>();
        age.add(0,"Choose your age range...");
        age.add(1, "14-17");
        age.add(2,"18-25");
        age.add(3,"25-30");
        age.add(4,"30-35");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,age);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ageSpinner.setAdapter(arrayAdapter);
        Log.i("Age", ageSpinner.getSelectedItem().toString());
    }
}