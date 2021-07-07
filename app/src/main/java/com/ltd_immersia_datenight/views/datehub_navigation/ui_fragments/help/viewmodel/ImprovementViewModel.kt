package com.ltd_immersia_datenight.views.datehub_navigation.ui_fragments.help.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.ltd_immersia_datenight.modelfirestore.Help.Help
import com.ltd_immersia_datenight.utils.constants.IntentConstants
import com.ltd_immersia_datenight.views.datehub_navigation.DateHubNavigation
import java.io.IOException


class ImprovementViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var db: FirebaseFirestore
    lateinit var improvementRef: DocumentReference
    lateinit var userDocRef: DocumentReference
    lateinit var userAuth: FirebaseAuth
    val TAG = "ImprovementViewModel"

    fun suggestImprovement(suggestInput: String, context: Context) {

        //initialize
        db = FirebaseFirestore.getInstance()
        //Reference
        improvementRef = db.collection("help").document()


        val suggestions: MutableMap<String, Any> = java.util.HashMap()
        suggestions["suggestion"] = suggestInput // === suggestion.put("suggestion", suggestInput.text.toString())

        val help  = Help("IMPROVEMENT", suggestInput)

        improvementRef.set(help).addOnSuccessListener {
            Toast.makeText(getApplication(), "Thanks for the feedback", Toast.LENGTH_SHORT).show()
            goToHelpFrag(context)

        }.addOnFailureListener { e ->
            Log.e(TAG, e.localizedMessage)
            Toast.makeText(getApplication(), e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    //client side sending of email

    fun sendEmail(){
//        val from = Email(userAuth.currentUser!!.email)
//        val subject = "Improvement"
//        val to = Email("info@immersia.co.uk")
//        val content = Content("text/plain", suggestInput)
//        val mail = Mail(from, subject, to, content)
//
//        //TODO: Hide api key
//        val sg = SendGrid(System.getenv("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"))
//        val request = Request()
//
//        try {
//            request.method = Method.POST
//            request.endpoint = "mail/send"
//            request.body = mail.build()
//
//            val response: Response = sg.api(request)
//            println(response.statusCode)
//            println(response.body)
//            println(response.headers)
//
//        } catch (ex: IOException) {
//            throw ex
//        }
    }


    fun goToHelpFrag(context: Context){
        val intent = Intent(context, DateHubNavigation::class.java)
        intent.putExtra(IntentConstants.FRAGMENT_TO_LOAD, IntentConstants.HELP_FRAGMENT)
        startActivity(context, intent, null)
    }

    companion object{
        lateinit  var suggestInput: String
    }

}