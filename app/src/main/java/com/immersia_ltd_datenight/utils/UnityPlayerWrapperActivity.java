/*
 * Copyright 2021 Damola Obaleke. All rights reserved.
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

package com.immersia_ltd_datenight.utils;

import android.os.Bundle;
import com.unity3d.player.UnityPlayerActivity;

public abstract class UnityPlayerWrapperActivity extends UnityPlayerActivity {
    public static UnityPlayerWrapperActivity instance = null;

    /**
     * Method is called whenever current user sends a chat within unity
     * This method should forward the message to the chat room
     * @param message - message current user is attempting to send
     */
    abstract protected void sendChatFromUnity(String message);

    /**
     * Method is called whenever current user sends a kiss to participant within unity
     * This method should increment the kiss count of the participant
     */
    abstract protected void sendKissFromUnity(); // Method is called whenever current user sends a kiss to participant within unity

    /**
     * Method is called whenever date creator/host successfully connects to a server
     * @param server - serverId where the date is hosted
     */
    abstract protected void sendServerFromUnity(String server); // Method is called whenever date creator/host sends server info from unity


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        instance = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }
}

