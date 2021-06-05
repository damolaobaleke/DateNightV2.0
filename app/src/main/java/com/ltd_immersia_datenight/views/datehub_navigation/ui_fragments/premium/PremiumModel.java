package com.ltd_immersia_datenight.views.datehub_navigation.ui_fragments.premium;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ltd_immersia_datenight.modelfirestore.Experience.ExperienceModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PremiumModel extends ViewModel {

    private final MutableLiveData<String> experienceName;
    private final MutableLiveData<Integer> price;
    public final MutableLiveData<String> environmentImage;
    public final MutableLiveData<String> environmentVideoUrl;
    public final MutableLiveData<String> experienceDescription;
    DocumentReference expRef;
    FirebaseFirestore db;

    public PremiumModel() {
        //Instance of db
        db = FirebaseFirestore.getInstance();
        //Ref to Doc
        expRef = db.collection("experiences").document("capBalloonRide");
        //initialize LiveData variables
        experienceName = new MutableLiveData<>();
        price = new MutableLiveData<>();
        environmentImage = new MutableLiveData<>();
        environmentVideoUrl = new MutableLiveData<>();
        experienceDescription = new MutableLiveData<>();
        //
        environmentImage.setValue("https://firebasestorage.googleapis.com/v0/b/datenight-f491f.appspot.com/o/experiencePreviewImages%2Fcappadociapreviewimg.png?alt=media&token=a9797ce0-3df8-4688-aac5-61d7a7319c64");

    }

    public LiveData<String> getExpName() {
        expRef.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                ExperienceModel experience = documentSnapshot.toObject(ExperienceModel.class);
                assert experience != null;
                experienceName.setValue(experience.getName());
            }
        });
        return experienceName;
    }

    public MutableLiveData<Integer> getPrice() {
        expRef.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                ExperienceModel experience = documentSnapshot.toObject(ExperienceModel.class);
                assert experience != null;
                price.setValue(experience.getPrice());
            }
        });
        return price;
    }

    public MutableLiveData<String> getEnvironmentImage() {

        return environmentImage;
    }

    public MutableLiveData<String> getEnvironmentVideoUrl() {
        expRef.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                ExperienceModel experience = documentSnapshot.toObject(ExperienceModel.class);
                assert experience != null;
                environmentVideoUrl.setValue(experience.getEnvironmentPreviewUrl());
            }
        });
        return environmentVideoUrl;
    }

    public MutableLiveData<String> getExperienceDescription() {
        expRef.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                ExperienceModel experience = documentSnapshot.toObject(ExperienceModel.class);
                assert experience != null;
                experienceDescription.setValue(experience.getDescription());
            }
        });
        return experienceDescription;
    }
}