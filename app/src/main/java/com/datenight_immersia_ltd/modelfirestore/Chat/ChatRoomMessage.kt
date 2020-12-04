package com.datenight_immersia_ltd.modelfirestore.Chat

class ChatRoomMessage(val senderId: String,
                      val imageUrl: String,
                      val text: String,
                      val timeStamp:Long){
    var key: String? = null

    // Default Empty Constructor needed for firebase
    constructor(): this("","","",0)

    fun calculateTimeFromTimeStamp(timeStamp: Long): String{
        return ""
    }
}