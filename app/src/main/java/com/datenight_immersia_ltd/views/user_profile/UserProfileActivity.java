package com.datenight_immersia_ltd.views.user_profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.model.User.UserObject;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileActivity extends AppCompatActivity {
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //Get ViewModel instance
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getUserObject().observe(this, new Observer<UserObject>() {
            @Override
            public void onChanged(UserObject userObject) { //only called when activity is in foreground
                //Update recycler view or any view

                Toast.makeText(UserProfileActivity.this, "On changed", Toast.LENGTH_SHORT).show();
                //Log.i("First Name", userObject.getFirstName());
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}