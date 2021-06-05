package com.ltd_immersia_datenight.views.datehub_navigation.ui_fragments.help.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.ltd_immersia_datenight.modelfirestore.Help.Help
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class ImprovementViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var db: FirebaseFirestore
    lateinit var improvementRef: DocumentReference
    val TAG = "ImprovementViewModel"

    fun suggestImprovement(suggestForm: String) {

        //initialize
        db = FirebaseFirestore.getInstance()
        //Reference
        improvementRef = db.collection("help").document()

        val suggestions: MutableMap<String, Any> = java.util.HashMap()
        suggestions["suggestion"] = suggestForm // === suggestion.put("suggestion", suggestForm.text.toString())

        val help  = Help("IMPROVEMENT", suggestForm)

        improvementRef.set(help).addOnSuccessListener {
            Toast.makeText(getApplication(), "Thanks for the feedback", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Log.e(TAG, e.localizedMessage)
        }
    }

}