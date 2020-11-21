/*
 * Copyright 2020 Damola Obaleke. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datenight_immersia_ltd.views.datehub_navigation.redundant

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.datenight_immersia_ltd.R
import com.datenight_immersia_ltd.views.landing_screen.BoardingDateScreen

class GetBitmojiActivity : AppCompatActivity() {
    private lateinit var importBitmoji: Button
    private lateinit var downloadBitmoji: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_bitmoji)

        importBitmoji = findViewById<Button>(R.id.importBitmoji)
        downloadBitmoji = findViewById(R.id.downloadBitmoji)

        val bitmojiImage = findViewById<ImageView>(R.id.bitmoji)
        //CDN
        //val url = Uri.parse("https://www.kindpng.com/picc/m/109-1092718_bitmoji-face-hd-png-download.png")
        //bitmojiImage.setImageURI(url)
        bitmojiImage.setImageDrawable(getDrawable(R.drawable.bitmoji_face))
        bitmojiImage.translationY = -1000f
        bitmojiImage.animate().translationYBy(1000f).duration = 1000
        bitmojiImage.animate().start()

        downloadBitmoji()
        importBitmoji()
    }

    private fun downloadBitmoji(){
        downloadBitmoji.setOnClickListener { v: View? ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.bitstrips.imoji&hl=en"))
            startActivity(intent)
        }
    }

    private fun importBitmoji(){
        importBitmoji.setOnClickListener{ v: View? ->
            //Go to bitmoji App and import

            //Then Create/Join Date screen
            val intent  = Intent(this, BoardingDateScreen::class.java)
            startActivity(intent)
        }
    }
}