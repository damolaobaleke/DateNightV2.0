package com.immersia_ltd_datenight.views.datehub_navigation.ui_fragments.dates.pending;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.immersia_ltd_datenight.DatabaseConstants;
import com.immersia_ltd_datenight.R;
import com.immersia_ltd_datenight.modelfirestore.Date.DateModel;
import com.immersia_ltd_datenight.utils.stripe.config.DateNight;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class PendingFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    static String TAG = "PendingFragment";
    // Firebase vars
    private FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance() ;
    private String currentUserId  = FirebaseAuth.getInstance().getUid();
    private DocumentReference userdocRef;
    FirebaseAuth mAuth;
    // Views
    private TextView dateChosen;
    private TextView timeChosen;
    private TextView pendingHint;
    // Recycler View
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<DateModel, PendingDateViewHolder> theAdapter;
    private FirestoreRecyclerOptions<DateModel> options;
    //App Data
    DateNight appData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        userdocRef = dbFirestore.collection("userData").document(currentUserId);

        appData = ((DateNight)getActivity().getApplication());

        setUpRecyclerView();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_pending, container, false);
        recyclerView = view.findViewById(R.id.date_list_pending_recyler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(theAdapter);
        pendingHint = view.findViewById(R.id.pending_hint);
        appData = ((DateNight)getActivity().getApplication());
        return view;
    }


    private void setUpRecyclerView() {
        //where the date doc id is in the user array of dateids
        Query query = userdocRef.collection(DatabaseConstants.DATES_COLLECTION).orderBy("timeCreated");

        //Set up recycler view options
        options = new FirestoreRecyclerOptions.Builder<DateModel>()
                .setQuery(query, DateModel.class)
                .setLifecycleOwner(getParentFragment())
                .build();

        theAdapter = new FirestoreRecyclerAdapter<DateModel, PendingDateViewHolder>(options) {
            int numHiddenViews = 0;

            @Override
            protected void onBindViewHolder(@NonNull PendingDateViewHolder holder, int position, @NonNull DateModel data) {
                Log.e(TAG, "Found Date " + data.getParticipantUsernames().toString());

                boolean hideView = true;
                HashMap<String, String> participantStatus = data.getParticipantStatus();
                for(String key : participantStatus.keySet()){
                    if (participantStatus.get(key).equals(DatabaseConstants.DATE_PENDING)){
                        hideView = false;
                        break;
                    }
                }

                // Only display pending dates - this is a hack, but there's no other option
                if(hideView){
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                    numHiddenViews++;
                } else{
                    holder.itemView.setVisibility(View.VISIBLE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    holder.bind(data, holder.itemView);
                }
            }

            @NonNull
            @Override
            public PendingDateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                Log.e(TAG, "Attempting to create view holder for pending dates");
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_dates_pending_lv, parent, false);
                return new PendingDateViewHolder(view);
            }


            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if(getItemCount() - numHiddenViews > 0){
                    pendingHint.setVisibility(View.GONE);
                } else {
                    pendingHint.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e) {
                super.onError(e);
                Log.e(TAG, e.toString());
            }
        };
    }

    public class PendingDateViewHolder extends RecyclerView.ViewHolder {
        // View items
        public TextView inviteDescription;
        Button editInviteButton;
        Button cancelInviteButton;
        TextView timeCreated;

        // Data
        DateModel dateData;

        public PendingDateViewHolder(@NonNull View itemView) {
            super(itemView);

            inviteDescription = itemView.findViewById(R.id.invite_description);
            editInviteButton = itemView.findViewById(R.id.edit_invite_btn);
            cancelInviteButton = itemView.findViewById(R.id.cancel_invite_btn);
            timeCreated = itemView.findViewById(R.id.time_created);
        }

        public void bind(DateModel data, View recyclerViewItemView){
            String dateParticipantId = "";
            Log.e(TAG, "Here attempting to bind");

            // TODO: bind data to actual views
            dateData = data;

            // Find who the date participant is
            Set<String> keys = data.getParticipants().keySet();
            for(String key: keys){
                if (!currentUserId.equals(key)){
                    dateParticipantId = key;
                    break;
                }
            }
            Log.e(TAG, "after loop");

            // When current user is date creator
            String expId = dateData.getLinkedExperienceId();
            if(currentUserId.equals(data.getCreator())){
                if (appData.getAppData(currentUserId) != null) {
                    inviteDescription.setText(String.format("Waiting for %s to accept your invite to %s", data.getParticipants().get(dateParticipantId), appData.getAppData(currentUserId).getExperienceName(expId))); // TODO: set experience name
                }

                // Set onclick listeners
                editInviteButton.setOnClickListener(v -> reScheduleDateTime());
                cancelInviteButton.setOnClickListener(v -> cancelInvite());
            } else {
                Log.e(TAG, "Here within not the creator");
                if (appData.getAppData(currentUserId) != null) {
                    inviteDescription.setText(String.format("%s is inviting you to %s", data.getParticipants().get(dateParticipantId), appData.getAppData(currentUserId).getExperienceName(expId))); // TODO: set experience
                }
                editInviteButton.setText(R.string.accept_invite);
                cancelInviteButton.setText(R.string.reject_invite);

                // Set onClick listeners
                editInviteButton.setOnClickListener(v -> acceptInvite(recyclerViewItemView));
                cancelInviteButton.setOnClickListener(v -> rejectInvite(recyclerViewItemView));
            }
            timeCreated.setText(constructRelativeDateTime(dateData.getTimeCreated()));
            Log.e(TAG, "All done");
        }

        private void reScheduleDateTime() {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
            View view = getLayoutInflater().inflate(R.layout.custom_date_schedule_view, null);

            TextView bottomSheetTitle = view.findViewById(R.id.dateTitle);
            Button createDateSchedule = view.findViewById(R.id.create_date_schedule);
            createDateSchedule.setText(R.string.re_schedule_date); //TODO Set title
            Button pickDate = view.findViewById(R.id.pick_date);
            Button pickTime = view.findViewById(R.id.pick_time);
            dateChosen = view.findViewById(R.id.dateChosen);
            timeChosen = view.findViewById(R.id.timeChosen);
            Button cancelCreation = view.findViewById(R.id.cancel_schedule_creation);

            //pre-set the text to the current chosen time and date
            Calendar dateTime = Calendar.getInstance();
            dateTime.setTime(dateData.getDateTime().toDate());
            dateChosen.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(dateTime.getTime()));
            timeChosen.setText(new SimpleDateFormat("h:mm a", Locale.getDefault()).format(dateTime.getTime()));

            pickDate.setOnClickListener(v -> pickDate());
            pickTime.setOnClickListener(v -> pickTime());
            cancelCreation.setOnClickListener(v -> bottomSheetDialog.dismiss());

            createDateSchedule.setOnClickListener(v -> {
                updateDateTime();
                bottomSheetDialog.dismiss();
            });

            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.create();
            bottomSheetDialog.show();
        }

        private void updateDateTime(){
            dateData.setDateTime(dateStringToTimestamp(dateChosen.getText() +" "+ timeChosen.getText()));
            dateData.setTimeCreated(Timestamp.now());

            // Write update to db
            Map<String, Object> updateDateData = new HashMap<>();
            updateDateData.put(DatabaseConstants.DATE_TIME_FIELD, dateStringToTimestamp(dateChosen.getText() +" "+ timeChosen.getText()));
            updateDateData.put(DatabaseConstants.DATE_CREATED_TIME_FIELD, Timestamp.now());
            userdocRef.collection(DatabaseConstants.DATES_COLLECTION)
                    .document(dateData.getId())
                    .update(updateDateData)
                    .addOnFailureListener(e -> Log.w(TAG, "Error updating date - unable to update document: " + dateData.getId() , e));
        }

        private void cancelInvite(){
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked - delete date
                        userdocRef.collection(DatabaseConstants.DATES_COLLECTION)
                                .document(dateData.getId())
                                .delete()
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error cancelling date - unable to delete document: " + dateData.getId() , e);
                                    }
                                });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        dialog.dismiss();
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Confirmation")
                    .setMessage("Are you sure you want to cancel date?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener)
                    .show();
        }

        private void acceptInvite(View v){
            HashMap<String, String> participantStatus = dateData.getParticipantStatus();
            participantStatus.replace(currentUserId, DatabaseConstants.DATE_ACCEPTED);

            Map<String, Object> updateData = new HashMap<>();
            updateData.put(DatabaseConstants.PARTICIPANT_STATUS_FIELD, participantStatus);

            userdocRef.collection(DatabaseConstants.DATES_COLLECTION)
                    .document(dateData.getId())
                    .update(updateData);

            v.setVisibility(View.GONE);
            ViewGroup.LayoutParams params = v.getLayoutParams();
            params.height = 0;
            v.setLayoutParams(params); //hack to hide view
        }

        public void rejectInvite(View v){
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked - reject date
                        HashMap<String, String> participantStatus = dateData.getParticipantStatus();
                        participantStatus.replace(currentUserId, DatabaseConstants.DATE_REJECTED);

                        Map<String, Object> updateData = new HashMap<>();
                        updateData.put(DatabaseConstants.PARTICIPANT_STATUS_FIELD, participantStatus);

                        userdocRef.collection(DatabaseConstants.DATES_COLLECTION)
                                .document(dateData.getId())
                                .update(updateData);

                        v.setVisibility(View.GONE);
                        ViewGroup.LayoutParams params = v.getLayoutParams();
                        params.height = 0;
                        v.setLayoutParams(params); //hack to hide view
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        dialog.dismiss();
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Confirmation")
                    .setMessage("Are you sure you want to reject invite?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener)
                    .show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        theAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        theAdapter.stopListening();
    }

    private void pickDate() {
        DatePickerDialog dateDialog = new DatePickerDialog(requireContext(), this,
                                                           Calendar.getInstance().get(Calendar.YEAR),
                                                           Calendar.getInstance().get(Calendar.MONTH),
                                                           Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        dateDialog.show();
    }

    private void pickTime() {
        TimePickerDialog timeDialog = new TimePickerDialog(requireContext(), this,
                                                           Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                                                           Calendar.getInstance().get(Calendar.MINUTE), true);
        timeDialog.show();
    }

    private String constructRelativeDateTime(Timestamp time){
        String rVal = "";
        Calendar dateTime = Calendar.getInstance();
        dateTime.setTime(time.toDate());
        Calendar currentTime = Calendar.getInstance();

        if(dateTime.get(Calendar.YEAR) == currentTime.get(Calendar.YEAR) &&
                dateTime.get(Calendar.MONTH) == currentTime.get(Calendar.MONTH) &&
                dateTime.get(Calendar.WEEK_OF_MONTH) == currentTime.get(Calendar.WEEK_OF_MONTH)){

            if (dateTime.get(Calendar.DAY_OF_WEEK) == currentTime.get(Calendar.DAY_OF_WEEK)){
                // Format time to show hours ago or minutes ago
                if(dateTime.get(Calendar.HOUR_OF_DAY) == currentTime.get(Calendar.HOUR_OF_DAY)){
                    int minDiff = currentTime.get(Calendar.MINUTE) - dateTime.get(Calendar.MINUTE);
                    rVal = minDiff + (minDiff == 1 ? " minute " : " minutes ") + "ago";
                } else {
                    int hourDiff = currentTime.get(Calendar.HOUR_OF_DAY) - dateTime.get(Calendar.HOUR_OF_DAY);
                    rVal = hourDiff + (hourDiff == 1 ? " hour " : " hours ") + "ago";
                }
            } else if(currentTime.get(Calendar.DAY_OF_WEEK) - dateTime.get(Calendar.DAY_OF_WEEK) == 1){
                rVal = "Yesterday";
            } else{
                // Format time to show day of week as in Tuesday
                rVal = new SimpleDateFormat("EEEE", Locale.getDefault()).format(dateTime.getTime());
            }

        } else{
            // Format time to show only date
            rVal = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dateTime.getTime());
        }
        return rVal;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        dateChosen.setText(String.format(Locale.getDefault(), "%02d-%02d-%02d", dayOfMonth, month + 1, year)); //add one to the month as array pos jan is 0
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String AM_PM="";

        Calendar calendar = Calendar.getInstance();
        if(calendar.get(Calendar.AM_PM) == Calendar.AM){
            AM_PM = "am";
        }else if(calendar.get(Calendar.AM_PM) == Calendar.PM){
            AM_PM = "pm";
        }else{
            AM_PM = "";
        }
        timeChosen.setText(String.format(Locale.getDefault(), "%01d:%02d %s", hourOfDay, minute, AM_PM));
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
}
