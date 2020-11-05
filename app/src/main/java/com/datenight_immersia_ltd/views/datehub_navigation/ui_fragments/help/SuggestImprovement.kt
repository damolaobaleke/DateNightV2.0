package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.help

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.datenight_immersia_ltd.R
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class SuggestImprovement : AppCompatActivity() {
    private lateinit var suggestImpButton: Button
    private lateinit var suggestForm: EditText
    lateinit var db: FirebaseFirestore
    lateinit var improvementRef: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggest_improvement)

        suggestImpButton = findViewById(R.id.submit_suggestion)
        suggestForm = findViewById(R.id.features)

        //initialize
        db = FirebaseFirestore.getInstance()
        //Reference
        improvementRef = db.collection("help").document("improvementReports")


        suggestImpButton.setOnClickListener {
            val suggestions: MutableMap<String, Any> = java.util.HashMap()
            suggestions["suggestion"] = suggestForm.text.toString() // === bugs.put("suggestion", suggestForm.text.toString())

            improvementRef.set(suggestions).addOnSuccessListener { Toast.makeText(this, "Thanks for the feedback", Toast.LENGTH_SHORT).show() }.addOnFailureListener { e ->
                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}