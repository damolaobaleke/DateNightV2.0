package com.immersia_ltd_datenight.views.datehub_navigation.ui_fragments.accounts;

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

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.immersia_ltd_datenight.R;
import com.immersia_ltd_datenight.modelfirestore.User.UserModel;
import com.immersia_ltd_datenight.utils.stripe.config.DateNight;
import com.immersia_ltd_datenight.views.authentication.LoginActivity;
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

    EditText emailInput;
    EditText passwordInput;
    TextView username,fullName,topUsername;
    TextView dateOfBirth;
    Button saveChanges;
    ProgressBar load;
    static Date date;

    View view;

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
        emailInput = view.findViewById(R.id.emailInput);
        username = view.findViewById(R.id.username);
        fullName =view.findViewById(R.id.name);
        dateOfBirth = view.findViewById(R.id.Age);
        topUsername =  view.findViewById(R.id.top_username);
        saveChanges = view.findViewById(R.id.save_changes);
        Button logOut = view.findViewById(R.id.log_out);

        saveChanges.setOnClickListener(v -> {
            progressBarShown();
            updateUser();
        });

        logOut.setOnClickListener(v -> {
            progressBarShown();
            signOut();
            progressBarGone();
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
            userRef.addSnapshotListener((DocumentSnapshot value, FirebaseFirestoreException error) -> {

                if (value.exists()) {
                    UserModel userModel = value.toObject(UserModel.class); //recreate doc object from class
                    assert userModel != null;
                    userModel.setId(userId);

                    dateOfBirth.setText(userModel.getDob() != null ? timeStamptoString(userModel.getDob()) : "");
                    username.setText(userModel.getUsername());
                    topUsername.setText(userModel.getUsername());
                    emailInput.setText(userModel.getEmail());
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
        updateUser.put("email", emailInput.getText().toString());

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
        if(timestamp != null) {
           date = timestamp.toDate();
        }
        return formatter.format(date);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        dateOfBirth.setText(String.format(Locale.US, "%d-%d-%d", dayOfMonth, month, year));
    }

    private void signOut() {
        DateNight appState = ((DateNight)this.requireContext().getApplicationContext());
        appState.clearAppData();

        //update fcm token to empty string, if someone else logs in you don't want the person to be receiving another users notifications
        userRef.update("fcmToken","");
        mAuth.signOut();
        //stripe
        CustomerSession.endCustomerSession();
        //stripe

        updateUI(null);
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        startActivity(intent);
    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            String name = user.getDisplayName();
            Log.i("Name", name);
        } else {
            Log.i(TAG, "iser logeed out");
        }
    }
}
