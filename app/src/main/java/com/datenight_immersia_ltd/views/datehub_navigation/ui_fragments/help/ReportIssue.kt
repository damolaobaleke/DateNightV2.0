package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.help

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import com.datenight_immersia_ltd.R
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class ReportIssue : AppCompatActivity() {
    private lateinit var reportButton: Button
    private lateinit var bugForm: EditText
    lateinit var db: FirebaseFirestore
    lateinit var bugRef: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_issue)

        reportButton = findViewById(R.id.report_bug)
        bugForm = findViewById(R.id.editTextTextMultiLine2)

        //initialize
        db = FirebaseFirestore.getInstance()

        bugRef = db.collection("help").document("bugReports")

        reportButton.setOnClickListener {

            val bugs: MutableMap<String, Any> = java.util.HashMap()
            bugs["bugFound"] = bugForm.text.toString() // === bugs.put("bugFound", bugForm.text.toString())

            bugRef.set(bugs).addOnSuccessListener { Toast.makeText(this, "Thanks for filing the bug", LENGTH_SHORT).show() }.addOnFailureListener { e ->
                Toast.makeText(this, e.localizedMessage, LENGTH_SHORT).show()
            }
        }

    }
}