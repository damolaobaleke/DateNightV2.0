package com.immersia_ltd_datenight.views.datehub_navigation.ui_fragments.help.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.immersia_ltd_datenight.modelfirestore.Help.Help
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class ReportViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var bugRef: DocumentReference
    lateinit var db : FirebaseFirestore


    fun reportBug(bugFormInput: String) {
        //initialize an instance of the database
        db = FirebaseFirestore.getInstance()

        bugRef = db.collection("help").document()

        val bugs: MutableMap<String, Any> = java.util.HashMap()
        bugs["bugFound"] =bugFormInput // === bugs.put("bugFound", bugFormInput)

        val help = Help("ISSUE", bugFormInput)

        bugRef.set(help).addOnSuccessListener {
            Toast.makeText(getApplication(), "Thanks for filing the bug", Toast.LENGTH_SHORT).show() }.addOnFailureListener { e ->
            Toast.makeText(getApplication(), e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

}