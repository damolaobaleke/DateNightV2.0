package com.immersia_ltd_datenight.utils.constants

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

    //Experiences
    const val EXPERIENCE_NODE = "experiences"

    //Ratings
    const val AVG_DATE_STATS_DOC = "avgDateStats"
    const val USER_RATING_FIELD = "rating"
    const val NUM_RATED_DATES = "ratedDates"

    //Dates
    const val DATES_COLLECTION = "dates"
    const val STATISTICS_NODE = "statistics"
    const val DATE_TIME_FIELD = "dateTime"
    const val DATE_CREATED_TIME_FIELD = "timeCreated"
    const val DATE_ACCEPTED = "ACCEPTED"
    const val DATE_REJECTED = "REJECTED"
    const val DATE_PENDING = "PENDING"
    const val PARTICIPANT_STATUS_FIELD = "participantStatus"
    const val SERVER_TO_CONNECT_STRING = "serverToConnect"
    const val DATE_COMPLETED_TIME_FIELD = "timeCompleted"
    const val LINKED_EXPERIENCE_ID = "linkedExperienceId"
    const val KISS_COUNT = "kissCount"
    const val DATE_COUNT = "dateCount"

    //Feedback
    const val FEEDBACK_NODE = "feedback"
    const val USERS_NODE = "users"
    const val LIKED_TEXT_FIELD = "likedText"
    const val DISLIKED_TEXT_FIELD = "dislikedText"
    const val RATING_FIELD = "rating"

    // Avatar
    const val AVATAR_NODE = "avatar"
    const val AVATAR_HEADSHOT_URL_FIELD = "avatarHeadShotUrl"
    const val AVATAR_URL_FIELD = "avatarUrl"


    //Auth Method
    const val LOCAL_AUTH = "EMAIL_PASSWORD"
}