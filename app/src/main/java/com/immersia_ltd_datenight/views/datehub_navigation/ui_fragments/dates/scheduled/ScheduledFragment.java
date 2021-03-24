package com.immersia_ltd_datenight.views.datehub_navigation.ui_fragments.dates.scheduled;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.immersia_ltd_datenight.utils.constants.DatabaseConstants;
import com.immersia_ltd_datenight.utils.constants.IntentConstants;
import com.immersia_ltd_datenight.MainActivity;
import com.immersia_ltd_datenight.R;
import com.immersia_ltd_datenight.modelfirestore.Date.DateModel;
import com.immersia_ltd_datenight.utils.stripe.config.DateNight;
import com.immersia_ltd_datenight.views.unity.UnityEnvironmentLoad;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ScheduledFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    static String TAG = "ScheduledFragment";
    // Firebase vars
    FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
    DocumentReference userdocRef;
    FirebaseAuth mAuth;
    String currentUserId = FirebaseAuth.getInstance().getUid();
    // Views
    TextView scheduledHint;
    TextView dateChosen;
    TextView timeChosen;
    // Recycler View
    private RecyclerView recyclerView;
    FirestoreRecyclerAdapter<DateModel, ScheduledDateViewHolder> theAdapter;
    FirestoreRecyclerOptions<DateModel> options;
    // App Data
    DateNight appState;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userdocRef = dbFirestore.collection("userData").document(currentUserId);
        appState = ((DateNight) getActivity().getApplication());
        if (appState.getAppData(currentUserId) == null){
            // Illegal state, navigate to main activity
            navigateToMainActivity();
        }
        setUpRecyclerView();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_scheduled, container, false);
        recyclerView = view.findViewById(R.id.date_list_scheduled_recyler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(theAdapter);
        scheduledHint = view.findViewById(R.id.scheduledHint);
        return view;
    }

    private void setUpRecyclerView() {
        Query query = userdocRef.collection(DatabaseConstants.DATES_COLLECTION)
                .orderBy(DatabaseConstants.DATE_CREATED_TIME_FIELD, Query.Direction.DESCENDING);

        //Set up recycler view options
        options = new FirestoreRecyclerOptions.Builder<DateModel>()
                .setQuery(query, DateModel.class)
                .setLifecycleOwner(getParentFragment())
                .build();

        theAdapter = new FirestoreRecyclerAdapter<DateModel, ScheduledDateViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ScheduledDateViewHolder holder, int position, @NonNull DateModel data) {

                Log.e(TAG, "Found Date " + data.getParticipantUsernames().toString());

                // Hide date if completed or if date has been not been accepted by both participants
                boolean hideView = false;
                if(data.getTimeCompleted() != null){ //Date has been completed
                    hideView = true;
                } else {
                    HashMap<String, String> participantStatus = data.getParticipantStatus();
                    for (String key : participantStatus.keySet()) {
                        if (!participantStatus.get(key).equals(DatabaseConstants.DATE_ACCEPTED)) {
                            hideView = true;
                            break;
                        }
                    }
                }

                // Only display pending dates - this is a hack, but there's no other option
                if (hideView) {
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                } else {
                    holder.itemView.setVisibility(View.VISIBLE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    holder.bind(data, holder.itemView);
                }

            }

            @NonNull
            @Override
            public ScheduledDateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                Log.e(TAG, "Attempting to create view holder for scheduled dates");
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_dates_sch_list_view, parent, false);
                return new ScheduledDateViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (getItemCount() > 0) {
                    scheduledHint.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e) {
                super.onError(e);
                Log.e(TAG, e.toString());
            }
        };
    }

    public class ScheduledDateViewHolder extends RecyclerView.ViewHolder {
        // View items
        public TextView dateTitle;
        TextView scheduledDate;
        TextView scheduledTime;
        TextView firstLetterName;

        // Data
        DateModel dateData;
        String dateParticipantId;

        public ScheduledDateViewHolder(@NonNull View itemView) {
            super(itemView);

            dateTitle = itemView.findViewById(R.id.dateTitle);
            scheduledDate = itemView.findViewById(R.id.dateDate);
            scheduledTime = itemView.findViewById(R.id.dateTime);
            firstLetterName = itemView.findViewById(R.id.first_letter_name_sch);
        }

        public void bind(DateModel data, View recyclerViewItemView) {

            Log.e(TAG, "Here attempting to bind");

            // TODO: bind data to actual views
            dateData = data;

            // Find who the date participant is
            Set<String> keys = data.getParticipants().keySet();
            for (String key : keys) {
                if (!currentUserId.equals(key)) {
                    dateParticipantId = key;
                    break;
                }
            }

            //pre-set the text to the current chosen time and date
            Calendar dateTime = Calendar.getInstance();
            dateTime.setTime(dateData.getDateTime().toDate());

            scheduledDate.setText(new SimpleDateFormat("MMM d", Locale.getDefault()).format(dateTime.getTime()));
            scheduledTime.setText(new SimpleDateFormat("h:mm a", Locale.getDefault()).format(dateTime.getTime()));
            String firstLetter = data.getParticipants().get(dateParticipantId).split("")[0];
            firstLetterName.setText(firstLetter);

            String expId = dateData.getLinkedExperienceId();
            if (appState.getAppData(currentUserId) != null) {
                dateTitle.setText(String.format("%s with %s", appState.getAppData(currentUserId).getExperienceName(expId), data.getParticipants().get(dateParticipantId)));
            }

            if (currentUserId.equals(data.getCreator())) {
                recyclerViewItemView.setOnClickListener(v -> alertDialogueDateCreator());

            } else {
                recyclerViewItemView.setOnClickListener(v -> alertDialogueDateParticipant(recyclerViewItemView));
            }
        }

        public void alertDialogueDateParticipant(View recyclerViewItemView) {
            View view = getLayoutInflater().inflate(R.layout.edit_invite_dialogue_date_participant, null);
            view.setBackgroundResource(android.R.color.transparent);
            String expId = dateData.getLinkedExperienceId();
            TextView dateDescTitle = view.findViewById(R.id.date_descr_title);

            if (appState.getAppData(currentUserId) != null) {
                dateDescTitle.setText(String.format("%s with %s", appState.getAppData(currentUserId).getExperienceName(expId), dateData.getParticipants().get(dateParticipantId)));
            }

            Button startDate = view.findViewById(R.id.start_date_btn);
            startDate.setText(R.string.join_date);
            Button cancel = view.findViewById(R.id.cancel_date_btn);

            AlertDialog alertDialog = new AlertDialog.Builder(requireContext()).create();

            startDate.setOnClickListener(v -> {
                //start unity environment
                startUnityScene();
                alertDialog.dismiss();

            }); //Start environment load
            cancel.setOnClickListener(v -> {
                rejectInvite(recyclerViewItemView);
                alertDialog.dismiss();
            });

            alertDialog.setView(view);
            alertDialog.show();
        }

        public void alertDialogueDateCreator() {
            View view = getLayoutInflater().inflate(R.layout.edit_invite_dialogue_date_creator, null);
            view.setBackgroundResource(android.R.color.transparent);

            TextView dateDescTitle = view.findViewById(R.id.date_descr_title);
            String expId = dateData.getLinkedExperienceId();

            if (appState.getAppData(currentUserId) != null) {
                dateDescTitle.setText(String.format("%s with %s", appState.getAppData(currentUserId).getExperienceName(expId), dateData.getParticipants().get(dateParticipantId)));
            }

            Button startDate = view.findViewById(R.id.start_date_btn);
            Button editDate = view.findViewById(R.id.edit_date_btn);
            Button cancelDate = view.findViewById(R.id.cancel_date_btn);

            AlertDialog alertDialog = new AlertDialog.Builder(requireContext()).create();

            // Set onClick listeners for buttons
            startDate.setOnClickListener(v -> {
                startUnityScene();
                alertDialog.dismiss();
            }); //Start environment load
            editDate.setOnClickListener(v -> {
                reScheduleDateTime();
                alertDialog.dismiss();
            });
            cancelDate.setOnClickListener(v -> {
                cancelInvite();
                alertDialog.dismiss();
            });

            alertDialog.setView(view);
            alertDialog.show();
        }

        private void startUnityScene() {
            Intent intent = new Intent(requireActivity(), UnityEnvironmentLoad.class)
                    .putExtra(IntentConstants.USER_ID_EXTRA, currentUserId)
                    .putExtra(IntentConstants.USER_FULL_NAME_EXTRA, appState.getAppData(currentUserId).getCurrentUser().getFullName())
                    .putExtra(IntentConstants.DATE_ID, dateData.getId())
                    .putExtra(IntentConstants.DATE_CREATOR_ID, dateData.getCreator())
                    .putExtra(IntentConstants.EXPERIENCE_ID, dateData.getLinkedExperienceId())
                    .putExtra(IntentConstants.PARTICIPANT_ID_EXTRA, dateParticipantId)
                    .putExtra(IntentConstants.PARTICIPANT_USER_NAME_EXTRA, dateData.getParticipantUsernames().get(dateParticipantId))
                    .putExtra(IntentConstants.PARTICIPANT_FULL_NAME_EXTRA, dateData.getParticipants().get(dateParticipantId));
            requireActivity().startActivity(intent);
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

        private void updateDateTime() {
            dateData.setDateTime(dateStringToTimestamp(dateChosen.getText() + " " + timeChosen.getText()));
            dateData.setTimeCreated(Timestamp.now());

            // Write update to db
            Map<String, Object> updateDateData = new HashMap<>();
            updateDateData.put(DatabaseConstants.DATE_TIME_FIELD, dateStringToTimestamp(dateChosen.getText() + " " + timeChosen.getText()));
            updateDateData.put(DatabaseConstants.DATE_CREATED_TIME_FIELD, Timestamp.now());
            userdocRef.collection(DatabaseConstants.DATES_COLLECTION).document(dateData.getId()).update(updateDateData)
                    .addOnFailureListener(e -> Log.w(TAG, "Error updating date - unable to update document: " + dateData.getId(), e));
        }

        private void cancelInvite() {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked - delete date
                        userdocRef.collection(DatabaseConstants.DATES_COLLECTION)
                                .document(dateData.getId())
                                .delete()
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error cancelling date - unable to delete document: " + dateData.getId(), e);
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


        public void rejectInvite(View v) {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        dateChosen.setText(String.format(Locale.getDefault(), "%02d-%02d-%02d", dayOfMonth, month + 1, year)); //add one to the month as array pos jan is 0
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String AM_PM = "";

        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.AM_PM) == Calendar.AM) {
            AM_PM = "am";
        } else if (calendar.get(Calendar.AM_PM) == Calendar.PM) {
            AM_PM = "pm";
        } else {
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

    private void navigateToMainActivity(){
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        startActivity(intent);
    }
}
