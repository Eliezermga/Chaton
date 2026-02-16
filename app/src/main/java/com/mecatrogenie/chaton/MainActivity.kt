package com.mecatrogenie.chaton

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Tasks
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mecatrogenie.chaton.user.User
import androidx.appcompat.widget.SearchView

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val db = Firebase.firestore
    private lateinit var chatsRecyclerView: RecyclerView
    private lateinit var noChatsLayout: LinearLayout
    private lateinit var newChatFab: FloatingActionButton
    private var allChats = listOf<Chat>()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that they will not receive notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level 33+ (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth

        if (auth.currentUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }

        askNotificationPermission()

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val userRef = db.collection("users").document(auth.currentUser!!.uid)
        userRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.exists()) {
                signOut()
            }
        }

        chatsRecyclerView = findViewById(R.id.chats_recycler_view)
        noChatsLayout = findViewById(R.id.no_chats_layout)
        newChatFab = findViewById(R.id.new_chat_fab)

        loadChats()

        newChatFab.setOnClickListener {
            startActivity(Intent(this, NewChatActivity::class.java))
        }
    }

    private fun loadChats() {
        val currentUserId = auth.currentUser!!.uid
        db.collection("chats")
            .whereArrayContains("participants", currentUserId)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshots == null || snapshots.isEmpty) {
                    updateUI(emptyList())
                    return@addSnapshotListener
                }

                val chatTasks = snapshots.documents.map { chatDoc ->
                    @Suppress("UNCHECKED_CAST")
                    val participants = chatDoc.get("participants") as? List<String> ?: emptyList()
                    val otherUserId = participants.firstOrNull { it != currentUserId }

                    val userTask = if (otherUserId != null) {
                        db.collection("users").document(otherUserId).get()
                    } else {
                        Tasks.forException(Exception("Other user ID is null for chat ${chatDoc.id}"))
                    }

                    val lastMessageTask = db.collection("chats").document(chatDoc.id)
                        .collection("messages")
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .limit(1)
                        .get()

                    Tasks.whenAllSuccess<Any>(userTask, lastMessageTask).continueWith { task ->
                        val results = task.result
                        val userDoc = results[0] as DocumentSnapshot
                        val messageQuerySnapshot = results[1] as com.google.firebase.firestore.QuerySnapshot

                        if (userDoc.exists()) {
                            val otherUser = userDoc.toObject(User::class.java)
                            val lastMessageDoc = messageQuerySnapshot.documents.firstOrNull()
                            val lastMessageText = lastMessageDoc?.getString("text")
                            val lastMessageTimestamp = lastMessageDoc?.getTimestamp("timestamp")

                            @Suppress("UNCHECKED_CAST")
                            val lastSeen = chatDoc.get("lastSeen") as? Map<String, com.google.firebase.Timestamp>
                            val lastSeenTimestamp = lastSeen?.get(currentUserId)

                            val isUnread = lastMessageTimestamp != null &&
                                    (lastSeenTimestamp == null || lastMessageTimestamp.toDate().after(lastSeenTimestamp.toDate()))

                            Chat(
                                id = chatDoc.id,
                                otherUserName = otherUser?.displayName,
                                otherUserPhotoUrl = otherUser?.photoUrl,
                                lastMessage = lastMessageText,
                                isUnread = isUnread
                            )
                        } else {
                            null
                        }
                    }
                }

                Tasks.whenAllComplete(chatTasks).addOnSuccessListener { completedTasks ->
                    val chats = completedTasks.mapNotNull { it.result as? Chat }
                    allChats = chats
                    updateUI(chats)
                }
            }
    }


    private fun updateUI(chats: List<Chat>) {
        if (chats.isEmpty()) {
            chatsRecyclerView.visibility = View.GONE
            noChatsLayout.visibility = View.VISIBLE
        } else {
            chatsRecyclerView.visibility = View.VISIBLE
            noChatsLayout.visibility = View.GONE
            val adapter = ChatAdapter(chats)
            chatsRecyclerView.adapter = adapter
            chatsRecyclerView.layoutManager = LinearLayoutManager(this)
            adapter.onItemClick = {
                val intent = ChatActivity.newIntent(this, it.id, it.otherUserName ?: "Chat")
                startActivity(intent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.setOnQueryTextListener(this)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sign_out -> {
                signOut()
                true
            }
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOut() {
        sendSignOutNotification()
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener(this) {
            Toast.makeText(this, "Vous avez été déconnecté.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

    private fun sendSignOutNotification() {
        val intent = Intent(this, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE)

        val channelId = "sign_out_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_send) // TODO: Replace with a proper notification icon
            .setContentTitle("Déconnexion")
            .setContentText("Vous avez été déconnecté avec succès.")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "Sign Out Notifications",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(1 /* ID of notification */, notificationBuilder.build())
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val filteredChats = if (newText.isNullOrBlank()) {
            allChats
        } else {
            allChats.filter { it.otherUserName?.contains(newText, ignoreCase = true) == true }
        }
        updateUI(filteredChats)
        return true
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
