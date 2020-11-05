package com.datenight_immersia_ltd.views.datehub_navigation.redundant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.cardview.widget.CardView
import com.datenight_immersia_ltd.R

class CreateDate : AppCompatActivity() {
    private lateinit var parisCard: CardView
    private lateinit var cappaduciaCard: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_date)
        title = "Select a Scene"

        parisCard = findViewById(R.id.paris)
        cappaduciaCard = findViewById(R.id.cappaducia)

        parisAmbience()
        cappadociaAmbience()
    }

    fun parisAmbience(){
        parisCard.setOnClickListener { Toast.makeText(this, "Load Up Paris", LENGTH_SHORT).show() }
    }

    fun cappadociaAmbience(){
        cappaduciaCard.setOnClickListener {Toast.makeText(this, "Load Up Cappadocia", LENGTH_SHORT).show()  }
    }


}