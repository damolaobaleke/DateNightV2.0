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

package com.immersia_ltd_datenight.utils.constants

object UnityConstants {
    // Startup Info
    const val NETWORK_MANAGER_GAME_OBJECT = "NetworkManager"
    const val STARTUP_FUNCTION_ARG = "JsonInfo"

    // Startup arguments
    const val EXPERIENCE_ID = "experienceID"
    const val DATE_ID = "dateID"
    const val CURRENT_USER_ID = "userId"
    const val CURRENT_USERNAME = "userName"
    const val AVATAR_URL = "avatarUrl"
    const val SERVER_TO_CONNECT = "serverToConnect"

    // Send Received Chat to Unity
    const val MESSAGE_CONTROLLER_GAME_OBJECT = "MessageController"
    const val SEND_MESSAGE_FUNCTION = "ReceiveMessageFromNative"
}