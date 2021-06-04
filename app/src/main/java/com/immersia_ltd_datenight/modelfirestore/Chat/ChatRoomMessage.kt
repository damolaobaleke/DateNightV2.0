package com.immersia_ltd_datenight.modelfirestore.Chat

import java.util.*

class ChatRoomMessage(val senderId: String,
                      val imageUrl: String,
                      val text: String,
                      val timeStamp:Long){
    var key: String? = null

    // Default Empty Constructor needed for firebase
    constructor(): this("","","",0)

    fun calculateTimeFromTimeStamp(seconds: Long): Date{
        return Date(seconds * 1000)
    }
}