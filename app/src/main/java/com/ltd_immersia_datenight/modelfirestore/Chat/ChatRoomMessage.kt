package com.ltd_immersia_datenight.modelfirestore.Chat

import java.io.Serializable
import java.util.*

class ChatRoomMessage(val senderId: String,
                      val imageUrl: String,
                      val text: String,
                      val timeStamp:Long) : Serializable{
    var key: String? = null

    // Default Empty Constructor needed for firebase
    constructor(): this("","","",0)

    fun calculateTimeFromTimeStamp(seconds: Long): Date{
        return Date(seconds * 1000)
    }
}