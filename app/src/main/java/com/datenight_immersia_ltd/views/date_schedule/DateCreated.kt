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
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.datenight_immersia_ltd.R
import com.datenight_immersia_ltd.utils.RecyclerViewAdapterPending
import com.datenight_immersia_ltd.views.datehub_navigation.DateHubNavigation
import com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.dates.pending.PendingFragment
import com.datenight_immersia_ltd.views.unity.UnityEnvironmentLoad

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DateCreated : AppCompatActivity() {
    lateinit var backToDateHub: Button
    lateinit var inviteByUsername: Button

    companion object {
        lateinit var userId: String
    }

    lateinit var userFullName: String
    lateinit var dateId: String
    lateinit var adapterPending: RecyclerViewAdapterPending

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date_created)

        backToDateHub = findViewById(R.id.back_date_hub)
        inviteByUsername = findViewById(R.id.button_invite_by_username)

        userId = intent.getStringExtra("userId")  //invitee
        userFullName = intent.getStringExtra("userFullName")
        dateId = intent.getStringExtra("dateID")

        Log.i("CongratsActivity", "The user invitee info: $userId $userFullName $dateId")

        backToDateHub.setOnClickListener {
            val intent = Intent(this, DateHubNavigation::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("userFullName", userFullName)//invitee
            intent.putExtra("dateID", dateId)//invitee
            startActivity(intent)

        }

        inviteByUsername.setOnClickListener {
            //startScene()
        }


    }

    fun toPendingFragment() {
        val bundle = Bundle()
        bundle.putString("userId", userId)
        bundle.putString("userFullName", userFullName)

        val fragmentManager = supportFragmentManager
        val pendingFragment = PendingFragment()
        pendingFragment.arguments = bundle
        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, pendingFragment).commit()
    }

    private fun startScene() {
        val intent = Intent(this, UnityEnvironmentLoad::class.java)
        startActivity(intent)
    }

//    companion object {
//        lateinit var userId: String
//    }
}