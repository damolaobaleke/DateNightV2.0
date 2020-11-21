package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.help

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.datenight_immersia_ltd.R
import com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.help.viewmodel.ReportViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class ReportIssue : AppCompatActivity() {
    private lateinit var reportButton: Button
    private lateinit var bugForm: EditText
    lateinit var db: FirebaseFirestore
    lateinit var bugRef: DocumentReference

    lateinit var reportViewModel: ReportViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_issue)

        reportButton = findViewById(R.id.report_bug)
        bugForm = findViewById(R.id.editTextTextMultiLine2)

        reportViewModel = ViewModelProvider(this).get(ReportViewModel::class.java)


        reportButton.setOnClickListener {
            reportViewModel.reportBug(bugForm.text.toString())
        }

    }

    fun withoutVm() {
        //initialize
//        db = FirebaseFirestore.getInstance()
//        bugRef = db.collection("help").document("bugReports")

//        val bugs: MutableMap<String, Any> = java.util.HashMap()
//            bugs["bugFound"] = bugForm.text.toString() // === bugs.put("bugFound", bugForm.text.toString())
//
//            bugRef.set(bugs).addOnSuccessListener { Toast.makeText(this, "Thanks for filing the bug", LENGTH_SHORT).show() }.addOnFailureListener { e ->
//                Toast.makeText(this, e.localizedMessage, LENGTH_SHORT).show()
//            }
    }

}