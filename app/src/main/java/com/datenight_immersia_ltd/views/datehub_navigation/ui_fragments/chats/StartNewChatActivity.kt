package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.chats

import android.os.Bundle
import android.support.wearable.activity.WearableActivity

class StartNewChatActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_new_chat)

        // Enables Always-on
        setAmbientEnabled()
    }
}