package com.mecatrogenie.chaton

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mecatrogenie.chaton.user.User
import com.mecatrogenie.chaton.user.UserAdapter

class NewChatActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_new_chat)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val rootLayout: CoordinatorLayout = findViewById(R.id.root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        val usersRecyclerView: RecyclerView = findViewById(R.id.users_recycler_view)
        usersRecyclerView.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(emptyList())
        usersRecyclerView.adapter = userAdapter
        userAdapter.onItemClick = { user ->
            createOrOpenChat(user)
        }

        loadUsers()
    }

    private fun loadUsers() {
        val currentUserId = auth.currentUser?.uid
        db.collection("users").get()
            .addOnSuccessListener { result ->
                val newUsers = mutableListOf<User>()
                for (document in result) {
                    if (document.id != currentUserId) {
                        val user = document.toObject(User::class.java).copy(uid = document.id)
                        newUsers.add(user)
                    }
                }
                userAdapter.updateUsers(newUsers)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                Toast.makeText(this, "Failed to load users.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun createOrOpenChat(user: User) {
        val currentUser = auth.currentUser!!
        val sortedParticipants = listOf(currentUser.uid, user.uid).sorted()

        val chatsRef = db.collection("chats")
        chatsRef.whereEqualTo("participants", sortedParticipants)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // Chat does not exist, create a new one
                    val newChat = hashMapOf(
                        "participants" to sortedParticipants,
                        "createdAt" to System.currentTimeMillis()
                    )
                    chatsRef.add(newChat)
                        .addOnSuccessListener { documentReference ->
                            goToChatActivity(documentReference.id, user)
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                            Toast.makeText(this, "Failed to create chat.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Chat already exists
                    val chatId = querySnapshot.documents.first().id
                    goToChatActivity(chatId, user)
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error getting documents: ", e)
                Toast.makeText(this, "Failed to check for existing chats.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun goToChatActivity(chatId: String, user: User) {
        val intent = ChatActivity.newIntent(this, chatId, user.displayName ?: "Chat")
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_chat_menu, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                userAdapter.filter.filter(newText)
                return true
            }
        })

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        private const val TAG = "NewChatActivity"
    }
}
