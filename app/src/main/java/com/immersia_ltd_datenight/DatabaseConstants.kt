package com.immersia_ltd_datenight

object DatabaseConstants {
    // Messages
    const val MESSAGES_NODE = "chatDb/chatRoomMessages"
    const val MOST_RECENT_MESSAGE_TIMESTAMP = "mostRecentMessage/timeStamp"

    //ChatRooms
    const val CHAT_ROOMS_NODE = "chatDb/chatRooms"

    //Users
    const val USER_DATA_NODE = "userData"
    const val USERNAME_CHILD = "username"
    const val FULL_NAME_CHILD = "name"
    const val USER_PHOTO_CHILD = "username"

    //Ratings
    const val USER_DATA_COLLECTION = "userData"
    const val AVG_DATE_STATS_DOC = "avgDateStats"
    const val USER_RATING_FIELD = "rating"

    //Dates
    const val DATES_COLLECTION = "dates"
    const val STATISTICS_NODE = "statistics"

    //Feedback
    const val FEEDBACK_NODE = "feedback"
    const val USERS_NODE = "users"
    const val LIKED_TEXT_FIELD = "likedText"
    const val DISLIKED_TEXT_FIELD = "dislikedText"
    const val RATING_FIELD = "rating"


    //Delimiters
    const val PARTICIPANTS_DELIMITER = ","

    //Auth Method
    const val LOCAL_AUTH = "EMAIL_PASSWORD"
}