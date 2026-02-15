package com.mecatrogenie.chaton

data class Chat(
    val id: String = "",
    val otherUserName: String? = null,
    val otherUserPhotoUrl: String? = null,
    val lastMessage: String? = null,
    val isUnread: Boolean = false
)
