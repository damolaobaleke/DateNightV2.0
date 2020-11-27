package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.dates.scheduled;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.modelfirestore.Date.DateModel;
import com.datenight_immersia_ltd.utils.DownloadImageTask;
import com.datenight_immersia_ltd.utils.RecyclerViewAdapterPending;
import com.datenight_immersia_ltd.utils.RecyclerViewAdapterScheduled;
import com.datenight_immersia_ltd.views.unity.UnityEnvironmentLoad;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ScheduledFragment extends Fragment {
    FirebaseFirestore db;
    CollectionReference datesCollRef;
    CollectionReference userCollRef;
    DocumentReference datesRef;
    DocumentReference userdocRef;
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
    String userId;//invitee

    DateModel dateModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_scheduled, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.date_list_scheduled_recyler_view);
        scheduledHint = view.findViewById(R.id.scheduledHint);

        mapCollectionsMapDocuments();


        userId = getActivity().getIntent().getStringExtra("userId"); //invitee
        userFullName = getActivity().getIntent().getStringExtra("userFullName"); //invitee
        Log.i(TAG, "The user id:" + userId + " " + userFullName);

        dateList = new ArrayList<>();

        if (dateList.size() >= 1) {
            scheduledHint = view.findViewById(R.id.pending_hint);
            scheduledHint.setVisibility(View.GONE);
        } else {
            scheduledHint.setVisibility(View.VISIBLE);
        }

        //getScheduledDates();

        return view;
    }

    public void getScheduledDates() {
        //Add Filter, using Query
        //condition 1= where (userId)invitee or participant status == accepted and datecreator status  == accepted
        datesCollRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                if (documentSnapshot.exists()) {
                    dateModel = documentSnapshot.toObject(DateModel.class); //recreate doc object from class

                    dateList.add(new DateModel(dateModel.getId(), null, mAuth.getCurrentUser().getUid(), dateModel.getDateInvitee(), participants, dateCreatedTime(), dateCreatedTime(), dateModel.getDateTime(), "", "", "https://datenight.co.uk/34f6784F234001", null));

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

                        }

                        @Override
                        public void onStartDate(int position) {
                            //startScene();
                        }
                    });

                }
            }
        });
    }


    public void startScene() {
        Intent intent = new Intent(requireContext(), UnityEnvironmentLoad.class);
        startActivity(intent);
    }

    public void mapCollectionsMapDocuments(){
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
        dateList.remove(pos);
        scheduledAdapter.notifyItemRemoved(pos);
    }

    public void alertDialogue(int pos) {
        View view = getLayoutInflater().inflate(R.layout.edit_invite_dialogue, null);
        view.setBackgroundResource(android.R.color.transparent);

        dateModel = dateList.get(pos);

        TextView dateDescTitle = view.findViewById(R.id.date_descr_title);
        dateDescTitle.setText("Datenight with" + dateModel.getDateInvitee());

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
