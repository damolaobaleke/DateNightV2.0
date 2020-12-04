package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.accounts;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.modelfirestore.User.UserModel;
import com.datenight_immersia_ltd.modelfirestore.User.UserStatsModel;
import com.datenight_immersia_ltd.network.api.User;
import com.datenight_immersia_ltd.views.authentication.LoginActivity;
import com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.premium.PremiumModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

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
    EditText username;
    EditText dateOfBirth;
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
        dateOfBirth = view.findViewById(R.id.Age);
        saveChanges = view.findViewById(R.id.save_changes);
        Button logOut = view.findViewById(R.id.log_out);

        dateOfBirth.setOnClickListener(v -> chooseAge());
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
        Log.i(TAG, user.toString());
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
            userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot value, FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.e(TAG, Objects.requireNonNull(error.getMessage()));
                        return;
                    }

                    assert value != null;
                    if (value.exists()) {
                        UserModel userModel = value.toObject(UserModel.class); //recreate doc object from class
                        assert userModel != null;
                        userModel.setId(userId);

                        dateOfBirth.setText(timeStamptoString(userModel.getDateOfBirth()));
                        username.setText(userModel.getUsername());
                        emailInput.setText(userModel.getEmail());

                        Log.i(TAG, "The id of the user:" + userModel.getId());
                    }
                }
            });
        } else {
            Toast.makeText(getContext(), "No info found", Toast.LENGTH_LONG).show();
        }
    }

    public void updateUser() {
        UserStatsModel userStats = new UserStatsModel(0, 0, 0);
        UserModel userModel = new UserModel(username.getText().toString(), null, emailInput.getText().toString(), dateStringToTimestamp(dateOfBirth.getText().toString()), 0, null, null, null,userStats ,"",username.getText().toString().toLowerCase());
        userRef.set(userModel).addOnSuccessListener(aVoid -> {
            progressBarGone();
            Toast.makeText(getContext(), "Your profile has been updated", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            progressBarGone();
            Log.i(TAG, "Didn't update user");
        }).addOnCompleteListener(task -> progressBarGone());
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
        mAuth.signOut();
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
