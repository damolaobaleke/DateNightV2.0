package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.casual;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.modelfirestore.Experience.ExperienceModel;
import com.datenight_immersia_ltd.views.date_schedule.DateScheduleActivity;
import com.datenight_immersia_ltd.views.unity.UnityEnvironmentLoad;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CasualFragment extends Fragment {

    private CasualViewModel galleryViewModel;
    CardView paris, cappaducia;
    Button createDate, joinDate;
    DocumentReference expRef;
    FirebaseFirestore db;
    TextView experienceName, experienceNameCappaduc; //Cappaduc actually used for meadow picnic change name convention
    ExperienceModel experienceModel, experienceModel1;
    static final String  TAG = "Casual Fragment";


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_casual, container, false);
        paris = view.findViewById(R.id.paris);
        cappaducia = view.findViewById(R.id.cappaducia);

        experienceName = view.findViewById(R.id.experience_name);
        experienceNameCappaduc = view.findViewById(R.id.experience_name_capp);


        db= FirebaseFirestore.getInstance();

        expRef = db.collection("experiences").document("aNightInParis");
        expRef.get().addOnSuccessListener(documentSnapshot1 -> {
            if(documentSnapshot1.exists()){
                experienceModel = documentSnapshot1.toObject(ExperienceModel.class);
                assert experienceModel != null;

                Log.i(TAG, experienceModel.getName());
                experienceName.setText(experienceModel.getName());
            }
        });

        expRef = db.collection("experiences").document("meadowPicnic");
        expRef.get().addOnSuccessListener(documentSnapshot1 -> {
            if(documentSnapshot1.exists()){
                experienceModel1 = documentSnapshot1.toObject(ExperienceModel.class);
                assert experienceModel1 != null;

                Log.i(TAG, experienceModel1.getName());
                experienceNameCappaduc.setText(experienceModel1.getName());
            }
        });

        paris.setOnClickListener(v->{
            //startParis();
            startScene();
        });

        cappaducia.setOnClickListener(v->startCappaducia());

        return view;
    }

    //FUTURE == Get experiences from db, for each experience doc object create a a cardview and populate the view
    //switch statement on card click
    //click listener for cards

    public void startParis(){
        Intent intent = new Intent(getContext(), DateScheduleActivity.class);
        //paris
        intent.putExtra("experienceName", experienceName.getText()); //OR experienceModel.getName()
        intent.putExtra("experienceDesc", experienceModel.getDescription());
        intent.putExtra("experienceCost", experienceModel.getPrice());
        startActivity(intent);
    }

    public void startCappaducia(){
        Intent intent = new Intent(getContext(), DateScheduleActivity.class);
        //capp
        intent.putExtra("experienceName", experienceNameCappaduc.getText()); //OR experienceModel.getName()
        intent.putExtra("experienceDesc", experienceModel1.getDescription());
        intent.putExtra("experienceCost", experienceModel1.getPrice());
        startActivity(intent);
    }

    public void startScene() {
        Intent intent = new Intent(requireContext(), UnityEnvironmentLoad.class);
        startActivity(intent);
    }

    public void showProgress(){

    }



}