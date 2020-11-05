package com.datenight_immersia_ltd.views.landing_screen


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.datenight_immersia_ltd.views.datehub_navigation.DateHubNavigation
import com.datenight_immersia_ltd.R


class BoardingDateScreen : AppCompatActivity() {
    lateinit var videoThai: VideoView
    lateinit var createDate: Button
    lateinit var joinDate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boarding_date_screen)

        videoThai = findViewById<VideoView>(R.id.thaiVideo)
        createDate = findViewById(R.id.createDate)
        joinDate = findViewById(R.id.joinDate)

        createDate()
    }

    private fun createDate(){
        createDate.setOnClickListener {
            var intent = Intent(this, DateHubNavigation::class.java)
            startActivity(intent)
        }
    }
    private fun joinDate(){
        joinDate.setOnClickListener {
            var intent = Intent(this, DateHubNavigation::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        //videoThai.setVideoURI()   // When using CDN eventually
        videoThai.setVideoPath("android.resource://" + packageName + "/" + R.raw.covverrthai)
        videoThai.setOnPreparedListener { mp ->
            mp.isLooping = true
            mp.start()
        }
    }

}