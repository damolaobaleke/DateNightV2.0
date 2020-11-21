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

package com.datenight_immersia_ltd.views.date_schedule

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.datenight_immersia_ltd.R
import com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.datehub.DatehubFragment
import com.datenight_immersia_ltd.views.unity.UnityEnvironmentLoad

class DateCreated : AppCompatActivity() {
    lateinit var backToDateHub: Button
    lateinit var inviteByUsername: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date_created)

        backToDateHub = findViewById(R.id.back_date_hub)
        inviteByUsername = findViewById(R.id.button_invite_by_username)

        backToDateHub.setOnClickListener {
            val intent = Intent(this, DatehubFragment::class.java)
            startActivity(intent)
        }

        inviteByUsername.setOnClickListener {
            startScene()
        }


    }

    private fun startScene() {
        val intent = Intent(this, UnityEnvironmentLoad::class.java)
        startActivity(intent)
    }
}