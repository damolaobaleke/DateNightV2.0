package com.ltd_immersia_datenight.modelfirestore.Chat

class ChatHead(val participants: Map<String?, String?>, val usernames: Map<String?, String?>, val mostRecentMessage: ChatRoomMessage){
    var key: String? = null
    var participantId: String? = null
    var participantUsername: String? = null
    var participantFullName: String? = null
    var participantPhotoUrl: String? = null

    fun setChatParticipantDetails(currentUserId: String){
        // participants is of form Map<String, String> where the key are userIds
        val temp = participants.keys
        participantId = if(temp.elementAt(0) != currentUserId) temp.elementAt(0) else temp.elementAt(1)
        participantUsername = usernames[participantId]
        participantFullName = participants[participantId]
        participantPhotoUrl = "none" // TODO: Fix
    }

    // Default Constructor needed for firebase
    constructor(): this(emptyMap(), emptyMap(), ChatRoomMessage())
}