package com.mecatrogenie.chaton

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

class ChatAdapter(private val chats: List<Chat>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    var onItemClick: ((Chat) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chats[position]
        holder.chatName.text = chat.otherUserName
        holder.lastMessage.text = chat.lastMessage
        holder.chatIcon.load(chat.otherUserPhotoUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_default_profile)
        }
        holder.unreadDot.visibility = if (chat.isUnread) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(chat)
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chatIcon: ImageView = itemView.findViewById(R.id.chat_icon)
        val chatName: TextView = itemView.findViewById(R.id.chat_name)
        val lastMessage: TextView = itemView.findViewById(R.id.last_message)
        val unreadDot: ImageView = itemView.findViewById(R.id.unread_dot)
    }
}
