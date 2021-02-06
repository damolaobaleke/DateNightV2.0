package com.immersia_ltd_datenight.views.datehub_navigation.ui_fragments.help

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.immersia_ltd_datenight.R
import com.immersia_ltd_datenight.views.datehub_navigation.ui_fragments.help.viewmodel.ImprovementViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class SuggestImprovement : AppCompatActivity() {
    private lateinit var suggestImpButton: Button
    private lateinit var suggestForm: EditText
    lateinit var db: FirebaseFirestore
    lateinit var improvementRef: DocumentReference

    lateinit var improvementViewModel: ImprovementViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggest_improvement)

        suggestImpButton = findViewById(R.id.submit_suggestion)
        suggestForm = findViewById(R.id.features)

        //Get instance of the view model
        improvementViewModel = ViewModelProvider(this).get(ImprovementViewModel::class.java)

        suggestImpButton.setOnClickListener {
            improvementViewModel.suggestImprovement(suggestForm.text.toString())
        }
    }
}