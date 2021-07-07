package com.ltd_immersia_datenight.views.datehub_navigation.ui_fragments.help

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.ltd_immersia_datenight.R
import com.ltd_immersia_datenight.views.datehub_navigation.ui_fragments.help.viewmodel.ReportViewModel

class ReportIssue : AppCompatActivity() {
    private lateinit var reportButton: Button
    private lateinit var bugForm: EditText
    lateinit var db: FirebaseFirestore
    lateinit var bugRef: DocumentReference

    lateinit var reportViewModel: ReportViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_issue)
        title = "Report issue"

        val toolbar = findViewById<Toolbar>(R.id.report_issue_toolbar)
        toolbar.setTitle(title)
        setSupportActionBar(toolbar)

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