package com.mecatrogenie.chaton

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mecatrogenie.chaton.message.Message
import com.mecatrogenie.chaton.message.MessageAdapter

class ChatActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_chat)

        val chatId = intent.getStringExtra("chatId")!!
        val chatName = intent.getStringExtra("chatName")

        val rootLayout: ConstraintLayout = findViewById(R.id.root_layout)
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        val messagesRecyclerView: RecyclerView = findViewById(R.id.messages_recycler_view)
        val messageEditText: EditText = findViewById(R.id.message_edit_text)
        val sendButton: MaterialButton = findViewById(R.id.send_button)

        setSupportActionBar(toolbar)
        supportActionBar?.title = chatName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        messagesRecyclerView.layoutManager = layoutManager

        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())

            // Apply the insets as padding to the root layout.
            // This will push the app bar down from the top and the input field up from the bottom.
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)

            // We've handled the insets, so we can return a consumed instance.
            WindowInsetsCompat.CONSUMED
        }

        loadMessages(chatId, messagesRecyclerView)
        markAsRead(chatId)

        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(chatId, messageText)
                messageEditText.text.clear()
            }
        }
    }

    private fun loadMessages(chatId: String, recyclerView: RecyclerView) {
        db.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Failed to load messages.", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val messages = snapshots!!.toObjects(Message::class.java)
                val adapter = MessageAdapter(messages)
                recyclerView.adapter = adapter
                recyclerView.post { recyclerView.scrollToPosition(messages.size - 1) }
            }
    }

    private fun sendMessage(chatId: String, text: String) {
        val senderId = auth.currentUser!!.uid
        val message = Message(text, senderId)

        db.collection("chats").document(chatId).collection("messages")
            .add(message)
            .addOnFailureListener { _ ->
                Toast.makeText(this, "Failed to send message.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun markAsRead(chatId: String) {
        val currentUserId = auth.currentUser!!.uid
        val lastSeenUpdate = mapOf("lastSeen.$currentUserId" to FieldValue.serverTimestamp())
        db.collection("chats").document(chatId).update(lastSeenUpdate)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        fun newIntent(context: Context, chatId: String, chatName: String): Intent {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("chatId", chatId)
            intent.putExtra("chatName", chatName)
            return intent
        }
    }
}
