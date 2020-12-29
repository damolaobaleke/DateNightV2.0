package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.dates.scheduled;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.modelfirestore.Date.DateModel;
import com.datenight_immersia_ltd.modelfirestore.User.UserModel;
import com.datenight_immersia_ltd.utils.DownloadImageTask;
import com.datenight_immersia_ltd.utils.RecyclerViewAdapterScheduled;
import com.datenight_immersia_ltd.views.unity.UnityEnvironmentLoad;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ScheduledFragment extends Fragment {
    FirebaseFirestore db;
    CollectionReference datesCollRef;
    CollectionReference userCollRef;
    DocumentReference datesRef;
    DocumentReference userdocRef, inviteeDocRef;
    FirebaseAuth mAuth;
    FirebaseUser user;
    TextView dateChosen;
    TextView timeChosen;
    TextView scheduledHint;
    static String TAG = "ScheduledFragment";

    private RecyclerView recyclerView;
    private RecyclerViewAdapterScheduled scheduledAdapter; //bridge
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<DateModel> dateList;

    HashMap<String, String> participants;
    String userFullName;//invitee
    String inviteeId;//invitee

    DateModel dateModel;
    UserModel userModel;

    QueryDocumentSnapshot mDocumentSnapshot;

    String creator;
    String inviteeKey;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_scheduled, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.date_list_scheduled_recyler_view);
        scheduledHint = view.findViewById(R.id.scheduledHint);

        mapCollectionsMapDocuments();


        dateList = new ArrayList<>();

        if (dateList.size() >= 1) {
            scheduledHint = view.findViewById(R.id.pending_hint);
            scheduledHint.setVisibility(View.GONE);
        } else {
            scheduledHint.setVisibility(View.VISIBLE);
        }

        getScheduledDates();

        return view;
    }

    public void getScheduledDates() {
        //Add Filter, using Query
        //condition 1= where (userId)invitee or participant status == accepted and datecreator status  == accepted

        datesCollRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                if (documentSnapshot.exists()) {
                    dateModel = documentSnapshot.toObject(DateModel.class); //recreate doc object from class

                    for (String key : dateModel.getParticipants().keySet()) {
                        if (key.equals(dateModel.getCreator())) {
                            creator = key;
                        } else {
                            inviteeKey = key;
                        }
                    }


                    //if (dateModel.getDateInviteeId() != null) {
                    inviteeDocRef = db.collection("userData").document(inviteeKey);

                    datesRef = db.collection("dates").document(dateModel.getId());

                    //if date id is in array
                    Task check2 = userCollRef.whereArrayContains("dateId", datesRef.getId()).get().addOnSuccessListener(queryDocumentSnapshots1 -> queryDocumentSnapshots1.forEach(documentSnapshot1 -> {
                        if (documentSnapshot1.exists()) {
                            userModel = documentSnapshot1.toObject(UserModel.class);
                            Log.i(TAG, "The date id's " + dateModel.getId() + "--get dates keeps returning--" + userModel.getDateId() + documentSnapshot1.get("dateId"));
                        }
                    }));

                    check2.addOnSuccessListener(o -> {

                        if (Objects.equals(dateModel.getParticipantStatus().get(inviteeKey), "ACCEPTED")) {
                            Log.i(TAG, inviteeKey + " " + dateModel.getParticipantStatus().get(inviteeKey));

                            dateList.add(new DateModel(dateModel.getId(),"86654", mAuth.getCurrentUser().getUid(), participants, null,dateCreatedTime(), dateCreatedTime(), dateStringToTimestamp(""), dateModel.getLinkedexperienceId(), null, null));

                            if (dateList.size() >= 1) {
                                scheduledHint.setVisibility(View.GONE);
                            } else {
                                scheduledHint.setVisibility(View.VISIBLE);
                            }

                            populateRecyclerView();

                            //remove Date /Edit Date
                            scheduledAdapter.setOnItemClickListener(new RecyclerViewAdapterScheduled.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    alertDialogue(position);
                                }

                                @Override
                                public void onCancelInvite(int position) {
                                    //reject Invite
                                    removeDate(position);
                                }

                                @Override
                                public void onStartDate(int position) {
                                    //startScene();
                                }
                            });
                        }
                    });
                    //}
                }
            }
        });
    }

    public List<String> getPendingDatess() {
        userCollRef.whereArrayContains("dateId", datesRef.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                queryDocumentSnapshots.forEach(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        mDocumentSnapshot = documentSnapshot;
                        userModel = documentSnapshot.toObject(UserModel.class); //recreate doc obj from model class
                        Log.i(TAG, userModel.getDateId().toString());
                    }
                });
            }
        });
        return (List<String>) mDocumentSnapshot.get("dateId"); //should be this==>userModel.getDates(), but using alternative
    }


    public void startScene() {
        Intent intent = new Intent(requireContext(), UnityEnvironmentLoad.class);
        startActivity(intent);
    }

    public void mapCollectionsMapDocuments() {
        userdocRef = db.collection("userData").document(mAuth.getCurrentUser().getUid());
        datesRef = db.collection("dates").document();
        datesCollRef = db.collection("dates");
        userCollRef = db.collection("userData");
    }

    public void populateRecyclerView() {
        //populate recycler view
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(requireContext());
        scheduledAdapter = new RecyclerViewAdapterScheduled(dateList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(scheduledAdapter);
        scheduledAdapter.notifyDataSetChanged();
        //populate recycler view
    }

    public void removeDate(int pos) {
        //remove from position in recyler view
        dateList.remove(pos);
        //delete date from db
        datesRef.delete();
        //remove from dateCreator dateid[]
        HashMap<String, Object> updateUser = new HashMap<>();
        updateUser.put("dateId", FieldValue.arrayRemove(datesRef.getId()));
        userdocRef.update(updateUser);

        //remove from dateid[] in dateInvitee
        inviteeDocRef.update(updateUser);
        //
        scheduledAdapter.notifyItemRemoved(pos);
    }

    public void alertDialogue(int pos) {
        View view = getLayoutInflater().inflate(R.layout.edit_invite_dialogue, null);
        view.setBackgroundResource(android.R.color.transparent);

        dateModel = dateList.get(pos);

        TextView dateDescTitle = view.findViewById(R.id.date_descr_title);
        dateDescTitle.setText(String.format("Datenight with %s", dateModel.getParticipants().get(inviteeKey)));

        AlertDialog alertDialog = new AlertDialog.Builder(requireContext()).create();

        alertDialog.setView(view);
        alertDialog.show();
    }

    public String dateTime() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time is => " + c);

        SimpleDateFormat df = new SimpleDateFormat("hh:mma", Locale.ENGLISH); //HH- 24hr, hh-12hr
        String formattedTime = df.format(c);
        //change to lowercase
        String formatClock = formattedTime.replace("PM", "pm").replace("AM", "am");

        System.out.println(formattedTime);

        return formatClock;
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
            avatarBitmap = task.execute("https://res.cloudinary.com/https-eazifunds-com/image/upload/v1602953980/unj4gdlawybvrs8pmziu.jpg").get();

            DownloadImageTask task2 = new DownloadImageTask();

        } catch (Exception e) {
            Log.i("getting Images", "Error \n" + e);
        }

        assert avatarBitmap != null;
        return avatarBitmap.get(0); //gets image in position 0 based on url }
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

}
