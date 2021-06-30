package com.ltd_immersia_datenight.views.datehub_navigation.ui_fragments.casual

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.ltd_immersia_datenight.R
import com.ltd_immersia_datenight.modelfirestore.Experience.ExperienceModel
import com.ltd_immersia_datenight.views.date_schedule.DateScheduleActivity
import com.ltd_immersia_datenight.views.unity.UnityEnvironmentLoad
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.ltd_immersia_datenight.utils.DateNight
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CasualFragment : Fragment() {
    var imageUrl = arrayOf("")


    private val galleryViewModel: CasualViewModel? = null
    lateinit var paris: CardView
    lateinit var cappaducia: CardView
    var createDate: Button? = null
    var joinDate: Button? = null
    var expRef: DocumentReference? = null
    var db: FirebaseFirestore? = null
    lateinit var experienceName: TextView
    lateinit var experienceNameCappaduc: TextView //Cappaduc actually used for meadow picnic change name convention
    lateinit var parisNightDinner: ImageView
    lateinit var meadowPicnic: ImageView
    lateinit var progressBar: ProgressBar
    lateinit var progressBarMeadow: ProgressBar

    lateinit var imageBitmaps: ArrayList<Bitmap>
    lateinit var bitmap: Bitmap

    var experienceModel: ExperienceModel? = null
    var experienceModel1: ExperienceModel? = null
    var appState: DateNight? = null

    companion object {
        const val TAG = "Casual Fragment"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appState = requireActivity().application as DateNight
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_casual, container, false)
        paris = view.findViewById(R.id.paris)
        cappaducia = view.findViewById(R.id.cappaducia)
        parisNightDinner = view.findViewById(R.id.image_paris)
        //meadowPicnic = view.findViewById(R.id.image_picnic)
        experienceName = view.findViewById(R.id.experience_name)
        experienceNameCappaduc = view.findViewById(R.id.experience_name_capp)
        progressBar = view.findViewById(R.id.progressBar4)
        progressBarMeadow = view.findViewById(R.id.progressBarM)


        db = FirebaseFirestore.getInstance()

        expRef = db!!.collection("experiences").document("aNightInParis")
        expRef!!.get().addOnSuccessListener { documentSnapshot1: DocumentSnapshot ->
            if (documentSnapshot1.exists()) {
                experienceModel = documentSnapshot1.toObject(ExperienceModel::class.java)
                assert(experienceModel != null)
                Log.i(TAG, experienceModel!!.name)
                experienceName.text = experienceModel!!.name
            }
        }


        expRef = db!!.collection("experiences").document("meadowPicnic")
        expRef!!.get().addOnSuccessListener { documentSnapshot1: DocumentSnapshot ->
            if (documentSnapshot1.exists()) {
                experienceModel1 = documentSnapshot1.toObject(ExperienceModel::class.java)

                assert(experienceModel1 != null)
                Log.i(TAG, experienceModel1!!.name)
                experienceNameCappaduc.text = experienceModel1!!.name
            }
        }


        //TODO: Load images from array-strings in long run

        //paris
        /**Note: Bobo has helped you grab the appState within the onCreate method of this fragment
         * Grab URL like so:
         * appState.getAppData().getExperiencesData() -> see DateNightAPpData.java class
         * Above returns a HashMap of expId to ExperienceModel
         * Instead of hardcoding all of this, you can loop throught the hashmap returned and populate your views!
         * Alternatively, you can look up the id of the experience you want, index into the hash map and grab the experience model you want then grab url
         */
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/datenight-f491f.appspot.com/o/experiencePreviewImages%2Fparispreviewimg.png?alt=media&token=57e42db7-bc7f-4e9e-9a02-0b58cce33587").into(parisNightDinner, object : Callback { //cache image for offline .networkPolicy()
            override fun onSuccess() {
                progressBar.isVisible = false
            }
            override fun onError(e: Exception) {
                progressBar.isVisible = true
                parisNightDinner.setImageResource(R.color.white)
                //meadowPicnic.setImageResource(R.color.white)
                Toast.makeText(context, e.message, LENGTH_LONG).show()
            }
        })

        /**meadow picnic not available now*/
        /*
        Picasso.get().load("").into(meadowPicnic, object : Callback {
            override fun onSuccess() {
                progressBarMeadow.isVisible = false
            }

            override fun onError(e: Exception) {
                progressBarMeadow.isVisible = true
                parisNightDinner.setImageResource(R.color.white)
                meadowPicnic.setImageResource(R.color.white)
                Toast.makeText(context, e.message, LENGTH_LONG).show()
            }
        })
         */


        //Co-routine scope , Main dispatcher, as we have some UI
        GlobalScope.launch(Dispatchers.Main) {
            getImages()
            imageBitmaps = ArrayList()
            //parisNightDinner.setImageBitmap(imageBitmaps[0])
            //meadowPicnic.setImageBitmap(imageBitmaps[1])
        }

        paris.setOnClickListener { v: View? -> startParis() }
        cappaducia.setOnClickListener { v: View? ->
            Toast.makeText(requireContext(), "Coming soon", LENGTH_SHORT).show();
            //startCappaducia()
        }
        return view
    }

    //FUTURE == Get experiences from db, for each experience doc object create a a cardview and populate the view
    //switch statement on card click
    //click listener for cards

    fun startParis() {
        val intent = Intent(context, DateScheduleActivity::class.java)
        //paris
        intent.putExtra("experienceName", experienceName!!.text) //OR experienceModel.getName()
        intent.putExtra("experienceDesc", experienceModel!!.description)
        intent.putExtra("experienceCost", experienceModel!!.price)
        startActivity(intent)
    }

    fun startCappaducia() {
        val intent = Intent(context, DateScheduleActivity::class.java)
        //capp
        intent.putExtra("experienceName", experienceNameCappaduc!!.text) //OR experienceModel.getName()
        intent.putExtra("experienceDesc", experienceModel1!!.description)
        intent.putExtra("experienceCost", experienceModel1!!.price)
        startActivity(intent)
    }

    fun startScene() {
        val intent = Intent(requireContext(), UnityEnvironmentLoad::class.java)
        startActivity(intent)
    }

    fun showProgress() {}

    //runs within a co-routine == move image load into this co routine
    private suspend fun getImages() {
        withContext(Dispatchers.IO) {
            for (image in imageUrl) {
                //convert all strings to valid uniform resource locator , take image strings in array
                Log.i("Images", image)
                //bitmap = BitmapFactory.decodeStream(URL(image).openStream())

            }
            //imageBitmaps.add(bitmap)

            //Log.i(TAG,Thread.currentThread().name + imageBitmaps.size)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager: ConnectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //API 29
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork);
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> { //Mobile Data
                        return true;
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> { //Wifi
                        return true;
                    }
                }
            }

        } else {
            //works below API 29
            try {
                val activeNetworkInfo = connectivityManager.activeNetworkInfo;
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                    Log.i(TAG, "Network is available : TRUE");
                    return true;
                }
            } catch (e: Error) {
                Log.i(TAG, "" + e.localizedMessage);
            }

        }

        Log.i(TAG, "Network available : FALSE ");
        return false;
    }
}

