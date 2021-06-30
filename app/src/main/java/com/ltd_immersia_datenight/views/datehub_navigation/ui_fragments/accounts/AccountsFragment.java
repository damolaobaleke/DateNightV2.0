package com.ltd_immersia_datenight.views.datehub_navigation.ui_fragments.accounts;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.ltd_immersia_datenight.R;
import com.ltd_immersia_datenight.modelfirestore.User.UserModel;
import com.ltd_immersia_datenight.utils.DateNight;
import com.ltd_immersia_datenight.views.landing_screen.BoardingScreen;
import com.stripe.android.CustomerSession;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AccountsFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    private final static String TAG = "AccountFragment";
    private AccountViewModel accountViewModel;
    FirebaseFirestore db;
    DocumentReference userRef;
    String userId;
    FirebaseAuth mAuth;
    FirebaseUser user;

    TextView email;
    EditText passwordInput;
    TextView username,fullName,topUsername;
    TextView dateOfBirth;
    TextView changePassword;
    ProgressBar load;
    static Date date;

    View view;
    private ListenerRegistration listenerRegister;
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_accounts, container, false);
        //Instance of ViewModel
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        //Initialize
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //bind
        email = view.findViewById(R.id.emailTextView);
        username = view.findViewById(R.id.username);
        fullName =view.findViewById(R.id.name);
        dateOfBirth = view.findViewById(R.id.Age);
        topUsername =  view.findViewById(R.id.top_username);
        changePassword = view.findViewById(R.id.changePasswordTextView);
        Button logOut = view.findViewById(R.id.log_out);

        logOut.setOnClickListener(v -> {
            progressBarShown();
            signOut();
            progressBarGone();
        });

        changePassword.setOnClickListener(v -> {
            launchChangePasswordActivity();
        });

        user = mAuth.getCurrentUser();
        if (user != null) {
            userId = mAuth.getCurrentUser().getUid();
            Log.i(TAG, userId);
            userRef = db.collection("userData").document(userId);
        } else {
            Toast.makeText(getContext(), "Not Logged In", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (user != null) {
            //requireActivity() = detaches listener when not in foreground in activity- not needed in fragments
            listenerRegister = userRef.addSnapshotListener((DocumentSnapshot value, FirebaseFirestoreException error) -> {
                if (value.exists()) {
                    listenerRegister.remove();
                    UserModel userModel = value.toObject(UserModel.class); //recreate doc object from class
                    assert userModel != null;
                    userModel.setId(userId);

                    dateOfBirth.setText(userModel.getDob() != null ? timeStamptoString(userModel.getDob()) : "");
                    username.setText(userModel.getUsername());
                    topUsername.setText(userModel.getUsername());
                    email.setText(userModel.getEmail());
                    fullName.setText(userModel.getFullName());

                    Log.i(TAG, "The id of the user:" + userModel.getId());
                }
            });
        } else {
            Toast.makeText(getContext(), "No info found", Toast.LENGTH_LONG).show();
        }
    }

    public void updateUser() {
        Map<String, Object> updateUser = new HashMap<>();
        //updateUser.put("username", username.getText().toString());
        updateUser.put("email", email.getText().toString());

        userRef.update(updateUser).addOnSuccessListener(aVoid -> {
            progressBarGone();
            Toast.makeText(getContext(), "Your profile has been updated", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            progressBarGone();
            Log.i(TAG, "Didn't update user");
        }).addOnCompleteListener(task -> progressBarGone());;
    }

    public void progressBarShown() {
        load = view.findViewById(R.id.progressBar2);
        load.setVisibility(View.VISIBLE);
    }

    public void progressBarGone() {
        load = view.findViewById(R.id.progressBar2);
        load.setVisibility(View.INVISIBLE);
    }


    public void chooseAge() {
        DatePickerDialog dateDialog = new DatePickerDialog(
                requireContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        dateDialog.show();
    }

    public static Timestamp dateStringToTimestamp(String dateStr) {
        try {
            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
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
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        if(timestamp != null) {
           date = timestamp.toDate();
        }
        return formatter.format(date);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        dateOfBirth.setText(String.format(Locale.getDefault(), "%d-%d-%d", dayOfMonth, month, year));
    }

    private void signOut() {
        DateNight appState = ((DateNight)this.requireContext().getApplicationContext());
        appState.clearAppData();

        //Update fcm token to empty string, if someone else logs in you don't want the person to be receiving another users notifications
        userRef.update("fcmToken","").addOnSuccessListener(aVoid -> Log.i(TAG,"fcm token set to empty string"));

        mAuth.signOut();
        user = null;
        //stripe
        CustomerSession.endCustomerSession();
        //stripe
        updateUI(null);
        Intent intent = new Intent(requireContext(), BoardingScreen.class);
        startActivity(intent);
    }

    public void launchChangePasswordActivity(){
        Intent intent = new Intent(requireContext(), ChangePasswordActivity.class);
        startActivity(intent);
    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            String name = user.getDisplayName();
            Log.i("Name", name);
        } else {
            Log.i(TAG, "user logged out");
        }
    }
}
