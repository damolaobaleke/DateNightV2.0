package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.dates.pending;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.model.DateDataModel;
import com.datenight_immersia_ltd.modelfirestore.Date.DateModel;
import com.datenight_immersia_ltd.modelfirestore.User.UserModel;
import com.datenight_immersia_ltd.utils.DownloadImageTask;
import com.datenight_immersia_ltd.utils.RecyclerViewAdapterPending;
import com.datenight_immersia_ltd.utils.RecylerViewAdapter;
import com.datenight_immersia_ltd.views.date_schedule.InviteUserActivity;
import com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.dates.CustomDatesAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PendingFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    FirebaseFirestore db;
    CollectionReference datesCollRef;
    CollectionReference userCollRef;
    DocumentReference datesRef;
    DocumentReference userdocRef;
    FirebaseAuth mAuth;
    FirebaseUser user;
    TextView dateChosen;
    TextView timeChosen;
    TextView pendingHint;
    static String TAG = "PendingFragment";

    private RecyclerView recyclerView;
    private RecyclerViewAdapterPending pendingAdapter; //bridge
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<DateModel> dateList;

    HashMap<String, String> participants;
    String userFullName;//invitee
    String userId;//invitee


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_pending, container, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.date_list_pending_recyler_view);
        pendingHint = view.findViewById(R.id.pending_hint);

        userdocRef = db.collection("userData").document(mAuth.getCurrentUser().getUid());
        datesRef = db.collection("dates").document();
        datesCollRef = db.collection("dates");
        userCollRef = db.collection("userData");

        userId = getActivity().getIntent().getStringExtra("userId"); //invitee
        userFullName = getActivity().getIntent().getStringExtra("userFullName"); //invitee
        Log.i(TAG, "The user id:" + userId + " " + userFullName);

        dateList = new ArrayList<>();
        //dateList.add(new DateModel(datesRef.getId(), null, mAuth.getCurrentUser().getUid(), userFullName, participants, dateCreatedTime(), dateCreatedTime(), dateStringToTimestamp(""), "", "", "https://datenight.co.uk/34f6784F234001", null));

        if (dateList.size() >= 1) {
            pendingHint = view.findViewById(R.id.pending_hint);
            pendingHint.setVisibility(View.GONE);
        } else {
            pendingHint.setVisibility(View.VISIBLE);
        }

        //Get Dates
        //getPendingDatesFiltered();
        getPendingDates();

        return view;
    }

    //POPULATE BASED ON FILTER, COMPOUND QUERY
    public void getPendingDatesFiltered() {
        Task query1 = datesCollRef.whereEqualTo("dateId", datesRef.getId()).get();  //snapshot1 where in collection dateId === dateDocId explicitly
        Task query2 = userCollRef.whereArrayContains("dateId", datesRef.getId()).get();  //snapshot 2 where in user coll arr dateId == dateDocId
        //3rd condition, where the participant status of the invitee(userId) id is pending
        if (userId != null) {
            Task query3 = datesCollRef.whereEqualTo(userId, "PENDING").get();
        }

        Task<List<QuerySnapshot>> allQueries = Tasks.whenAllSuccess(query1, query2);
        allQueries.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                for (QuerySnapshot querySnapshot : querySnapshots) {
                    Log.i(TAG, querySnapshot.getMetadata().toString());

                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                        if (documentSnapshot.exists()) { //if doc exists that meets all query requirements
                            DateModel dateModel = documentSnapshot.toObject(DateModel.class); //recreate doc object from class

                            Log.i(TAG, "The information: " + dateModel.getId());

                            dateList.add(new DateModel(dateModel.getId(), null, mAuth.getCurrentUser().getUid(), dateModel.getDateInvitee(), participants, dateCreatedTime(), dateCreatedTime(), dateStringToTimestamp(""), "", "", "https://datenight.co.uk/34f6784F234001", null));

                            if (dateList.size() >= 1) {
                                pendingHint.setVisibility(View.GONE);
                            } else {
                                pendingHint.setVisibility(View.VISIBLE);
                            }

                            populateRecyclerView();

                            //remove Date /Edit Date
                            pendingAdapter.setOnItemClickListener(new RecyclerViewAdapterPending.OnItemClickListener() {
                                @Override
                                public void onCancelInvite(int position) {
                                    removeDate(position);
                                }

                                @Override
                                public void onEditInvite(int position) {
                                    //Alert Dialogue
                                    editInvite(position);
                                }
                            });

                        }
                    }
                }
            }
        });
        //POPULATE BASED ON FILTER, COMPOUND QUERY
    }

    //UNFILTERED
    public void getPendingDates() {
        //Get and filter query
        datesCollRef.orderBy("timeCreated", Query.Direction.DESCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                if (documentSnapshot.exists()) {
                    DateModel dateModel = documentSnapshot.toObject(DateModel.class); //recreate doc object from class

                    dateList.add(new DateModel(dateModel.getId(), null, mAuth.getCurrentUser().getUid(), dateModel.getDateInvitee(), participants, dateCreatedTime(), dateCreatedTime(), dateStringToTimestamp(""), "", "", "https://datenight.co.uk/34f6784F234001", null));

                    if (dateList.size() >= 1) {
                        pendingHint.setVisibility(View.GONE);
                    } else {
                        pendingHint.setVisibility(View.VISIBLE);
                    }

                    populateRecyclerView();

                    //remove Date /Edit Date
                    pendingAdapter.setOnItemClickListener(new RecyclerViewAdapterPending.OnItemClickListener() {
                        @Override
                        public void onCancelInvite(int position) {
                            removeDate(position);
                        }

                        @Override
                        public void onEditInvite(int position) {
                            //Alert Dialogue
                            editInvite(position);
                        }
                    });

                }
            }
        });
    }

    public void populateRecyclerView() {
        //populate recycler view
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(requireContext());
        pendingAdapter = new RecyclerViewAdapterPending(dateList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(pendingAdapter);
        pendingAdapter.notifyDataSetChanged();
        //populate recycler view
    }

    public void removeDate(int pos) {
        dateList.remove(pos);
        pendingAdapter.notifyItemRemoved(pos);
    }


    public void editInvite(int pos) {
        alertDialogue(dateList.get(pos));
        pendingAdapter.notifyDataSetChanged(); //notify adapter with what is going on
    }

    public void alertDialogue(DateModel pos) {
        View view = getLayoutInflater().inflate(R.layout.edit_invite_dialogue, null);
        view.setBackgroundResource(android.R.color.transparent);

        TextView dateDescTitle = view.findViewById(R.id.date_descr_title);
        dateDescTitle.setText("Date night with " + pos.getDateInvitee());

        Button startDate = view.findViewById(R.id.start_date_btn);
        Button editDate = view.findViewById(R.id.edit_date_btn);
        Button cancel = view.findViewById(R.id.cancel_invite_btn);

        AlertDialog alertDialog = new AlertDialog.Builder(requireContext()).create();

        startDate.setOnClickListener(v -> {
        }); //Start environment load

        editDate.setOnClickListener(v -> {
            scheduleDateTime(pos);
        });

        cancel.setOnClickListener(v -> alertDialog.dismiss());

        pendingAdapter.notifyDataSetChanged();
        alertDialog.setView(view);
        alertDialog.show();
    }

    public void scheduleDateTime(DateModel pos) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.custom_date_schedule_view, null);

        Button createDateSchedule = view.findViewById(R.id.create_date_schedule);
        Button pickDate = view.findViewById(R.id.pick_date);
        Button pickTime = view.findViewById(R.id.pick_time);
        dateChosen = view.findViewById(R.id.dateChosen);
        timeChosen = view.findViewById(R.id.timeChosen);
        Button cancelCreation = view.findViewById(R.id.cancel_schedule_creation);

        //pre-set the text to the current chosen time and date
        dateChosen.setText(pos.getDateTime().toString());
        timeChosen.setText(pos.getDateTime().toString());


        pickDate.setOnClickListener(v -> pickDate());
        pickTime.setOnClickListener(v -> pickTime());
        cancelCreation.setOnClickListener(v -> bottomSheetDialog.dismiss());

        createDateSchedule.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "date rescheduled", Toast.LENGTH_LONG).show();
            Log.i(TAG, dateChosen.getText().toString() + "::" + timeChosen.getText());
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.create();
        bottomSheetDialog.show();
    }

    private void pickDate() {
        DatePickerDialog dateDialog = new DatePickerDialog(requireContext(), this, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        dateDialog.show();
    }

    private void pickTime() {
        TimePickerDialog timeDialog = new TimePickerDialog(requireContext(), this, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);
        timeDialog.show();

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        dateChosen.setText(String.format(Locale.UK, "%02d-%02d-%02d", dayOfMonth, month + 1, year)); //add one to the month as array pos jan is 0
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        timeChosen.setText(String.format(Locale.UK, "%d:%01d", hourOfDay, minute));
    }

    public Timestamp dateCreatedTime() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.UK);
        Date date = Calendar.getInstance().getTime();
        String formattedDate = dateFormatter.format(date);
        try {
            date = dateFormatter.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Timestamp(date);
    }

    public static Timestamp dateStringToTimestamp(String dateStr) {
        try {
            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Date date = formatter.parse(dateStr);
            assert date != null;
            //convert date to timestamp
            return new Timestamp(date);
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
            return null;
        }
    }

    public String dateTime() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time is => " + c);

        SimpleDateFormat df = new SimpleDateFormat("HH:mm a", Locale.ENGLISH);
        String formattedTime = df.format(c);
        System.out.println(formattedTime);

        return formattedTime;
    }

    public String dateDay() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time is => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd -MMM", Locale.ENGLISH);
        String formattedDate = df.format(c);
        System.out.println(formattedDate);

        return formattedDate;
    }

    private Bitmap getImageBitmap() {
        ArrayList<Bitmap> avatarBitmap = null;
        try {
            DownloadImageTask task = new DownloadImageTask();

            //execute() method calls the do in background method
            avatarBitmap = task.execute("https://res.cloudinary.com/dayvbcxai/image/upload/v1597180001/DateNight/DateNight-Logo-Icon-Transparetn_ms1kfn.png").get();

            DownloadImageTask task2 = new DownloadImageTask();

        } catch (Exception e) {
            Log.i("getting Images", "Error \n" + e);
        }

        assert avatarBitmap != null;
        return avatarBitmap.get(0); //gets image in position 0 based on url }
    }

}
