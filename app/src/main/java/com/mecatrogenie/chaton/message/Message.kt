package com.mecatrogenie.chaton.message

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Message(
    val text: String = "",
    val senderId: String = "",
    @ServerTimestamp
    val timestamp: Date? = null
)
