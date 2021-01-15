package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.dates.pending;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.modelfirestore.Date.DateModel;
import com.datenight_immersia_ltd.modelfirestore.Experience.ExperienceModel;
import com.datenight_immersia_ltd.modelfirestore.User.UserModel;
import com.datenight_immersia_ltd.network.api.User;
import com.datenight_immersia_ltd.utils.DownloadImageTask;
import com.datenight_immersia_ltd.utils.PendingAdapter;
import com.datenight_immersia_ltd.utils.RecyclerViewAdapterPending;
import com.datenight_immersia_ltd.views.date_schedule.InviteUserActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nimbusds.jose.util.ArrayUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PendingFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    FirebaseFirestore db;
    CollectionReference datesCollRef;
    CollectionReference userCollRef;
    DocumentReference datesRef, experienceRef;
    DocumentReference userdocRef, inviteeDocRef;
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

    private PendingAdapter adapter;

    HashMap<String, String> participants;
    String userFullName;//invitee
    String inviteeId;//invitee
    String dateId;

    ExperienceModel experienceModel;
    UserModel userModel;
    DateModel dateModel;

    String inviteeKey;
    String creator;

    String inv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_pending, container, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.date_list_pending_recyler_view);
        pendingHint = view.findViewById(R.id.pending_hint);

        userdocRef = db.collection("userData").document(mAuth.getCurrentUser().getUid());

        datesCollRef = db.collection("dates");
        userCollRef = db.collection("userData");
        experienceRef = db.collection("experiences").document("aNightInParis"); //move to coll reference in future

        dateList = new ArrayList<>();

//        if (dateList.size() >= 1) {
//            pendingHint = view.findViewById(R.id.pending_hint);
//            pendingHint.setVisibility(View.GONE);
//        } else {
//            pendingHint.setVisibility(View.VISIBLE);
//        }

        //Get Dates
        //getPendd();
        setUpRecyclerView();

        return view;
    }


    private void setUpRecyclerView() {
        userdocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                userModel = documentSnapshot.toObject(UserModel.class);
                Log.i(TAG, "The date ids:" + userModel.getDateId().toString());


                //where the date doc id is in the user array of dateids
                //Creates and returns a new Query with the additional filter that documents must contain the specified field and the value must equal one of the values from the provided list.
                //.whereIn("id", Collections.singletonList(userModel.getDateId())) .orderBy("timeCreated", Query.Direction.DESCENDING)

                Query query = datesCollRef.whereIn("id", Collections.singletonList(userModel.getDateId())).orderBy("timeCreated", Query.Direction.DESCENDING);


                //getting query into adapter
                FirestoreRecyclerOptions<DateModel> options = new FirestoreRecyclerOptions.Builder<DateModel>()
                        .setQuery(query, DateModel.class)
                        .setLifecycleOwner(this)
                        .build();


                adapter = new PendingAdapter(options);

                //populate recycler view
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

                adapter.startListening(); //start listening for db changes and populate the adapter
                recyclerView.setAdapter(adapter);


                //adapter.notifyDataSetChanged();

                adapter.setOnItemClickListener(new PendingAdapter.OnItemClickListener() {
                    @Override
                    public void onCancelInvite(int position) {

                    }

                    @Override
                    public void onEditInvite(int position) {

                    }
                });

                //populate recycler view


            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        //this line is supposed to start listening for db changes and populate the adapter, the view but throws an error
        //adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
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
        //if date creator deletes from dates collections or invitee rejects invite
        //delete date
        datesRef.delete();

        //
        //remove from dateCreator dateid[]
        HashMap<String, Object> updateUser = new HashMap<>();
        updateUser.put("dateId", FieldValue.arrayRemove(datesRef.getId()));
        userdocRef.update(updateUser);

        //remove from dateid[] in dateInvitee
        inviteeDocRef.update(updateUser);
        //
        pendingAdapter.notifyItemRemoved(pos);
    }


    public void editInvite(int pos) {
        alertDialogue(dateList.get(pos));
        pendingAdapter.notifyDataSetChanged(); //notify adapter with what is going on
    }

    public void alertDialogue(DateModel datemodel) {
        View view = getLayoutInflater().inflate(R.layout.edit_invite_dialogue, null);
        view.setBackgroundResource(android.R.color.transparent);

        TextView dateDescTitle = view.findViewById(R.id.date_descr_title);
        dateDescTitle.setText(String.format("Date night with %s", datemodel.getParticipants().get(inviteeKey)));

        Button startDate = view.findViewById(R.id.start_date_btn);
        Button editDate = view.findViewById(R.id.edit_date_btn);
        Button cancel = view.findViewById(R.id.cancel_invite_btn);

        AlertDialog alertDialog = new AlertDialog.Builder(requireContext()).create();

        startDate.setOnClickListener(v -> {
        }); //Start environment load

        editDate.setOnClickListener(v -> {
            scheduleDateTime(datemodel);
        });

        //cancel.setOnClickListener(v -> alertDialog.dismiss());

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


    public void getPendd() {
        userdocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                userModel = documentSnapshot.toObject(UserModel.class);
                Log.i(TAG, "The date ids:" + userModel.getDateId().toString());


                //where the date doc id is in the user array of dateids
                //Creates and returns a new Query with the additional filter that documents must contain the specified field and the value must equal one of the values from the provided list.
                //.whereIn("id", Collections.singletonList(userModel.getDateId())) .orderBy("timeCreated", Query.Direction.DESCENDING)

                datesCollRef.whereIn("id", Collections.singletonList(userModel.getDateId())).orderBy("timeCreated", Query.Direction.DESCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                        if (documentSnapshot1.exists()) {
                            DateModel dateModel = documentSnapshot1.toObject(DateModel.class);

                            for (String key : dateModel.getParticipants().keySet()) {
                                if (key.equals(dateModel.getCreator())) {
                                    creator = key;
                                    Log.i(TAG, "key 1: " + creator + ":" + dateModel.getParticipantStatus().get(creator));
                                } else {
                                    inviteeKey = key;
                                    Log.i(TAG, "key 2: " + inviteeKey + ":" + dateModel.getParticipantStatus().get(inviteeKey));
                                }
                            }

                            inviteeDocRef = db.collection("userData").document(inviteeKey); //to update invitee dateId[]
                            datesRef = db.collection("dates").document(dateModel.getId()); //to delete date

                            //Check status of date invitee is pending and id of the date is in the dateid array of the user\\
                            if (Objects.equals(dateModel.getParticipantStatus().get(inviteeKey), "PENDING")) {

                                Log.i(TAG, dateModel.getId());
                                dateList.add(new DateModel(dateModel.getId(), "86654", mAuth.getCurrentUser().getUid(), dateModel.getParticipants(), null, dateCreatedTime(), dateCreatedTime(), dateStringToTimestamp(""), dateModel.getLinkedExperienceId(), null, null));

                                populateRecyclerView();

                                if (dateList.size() >= 1) {
                                    pendingHint.setVisibility(View.GONE);
                                } else {
                                    pendingHint.setVisibility(View.VISIBLE);
                                }

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

                            } else {
                                Log.i(TAG, "Not found");
                            }
                        } else {
                            Log.i(TAG, "Not found 2");
                        }
                    }
                });
            }
        });
    }


}
