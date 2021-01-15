/*
 * Copyright 2021 Damola Obaleke. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datenight_immersia_ltd.views.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.databinding.ActivityForgotPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgotPassword extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText forgotPasswordEmail;
    Button resetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        forgotPasswordEmail = findViewById(R.id.editTextForgotPassword);
        resetPassword = findViewById(R.id.reset_password);

        mAuth = FirebaseAuth.getInstance();


        forgotPasswordEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String forgotPasswordInput = forgotPasswordEmail.getText().toString().trim();

                resetPassword.setEnabled(!forgotPasswordInput.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        resetPassword.setOnClickListener(v -> {
            mAuth.sendPasswordResetEmail(forgotPasswordEmail.getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPassword.this, "Check your email for the link to\n reset your password", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ForgotPassword.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        });
    }


}