package com.datenight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {
    EditText username;
    EditText password;
    Button LogIn;

    String Username;
    String Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.emailLogin);
        password = findViewById(R.id.password);
        LogIn = findViewById(R.id.Log_In);

        Auth();

    }

    public void Auth() {
        //Firebase Auth
        LogIn.setOnClickListener(v->{
                Toast.makeText(this, "Username is" + username.getText().toString() + "\n" + "Password is " + encrypt(), Toast.LENGTH_LONG).show();
                Log.i("User", username.getText().toString() +" " +password.getText().toString());
                Intent intent = new Intent(this, GetBitmojiActivity.class);
                startActivity(intent);
        });
    }

    public void Date(){
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        System.out.println(formattedDate);
    }

    public double encrypt(){
        if(password.getText().toString() != null){
           return Math.random();
        }else{
            return 1.0;
        }
    }
}