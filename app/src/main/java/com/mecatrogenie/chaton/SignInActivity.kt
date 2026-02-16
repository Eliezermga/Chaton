package com.mecatrogenie.chaton

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.button.MaterialButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mecatrogenie.chaton.user.User

class SignInActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var signInButton: MaterialButton
    private lateinit var progressBar: ProgressBar
    private val db = Firebase.firestore

    private val signInResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w(TAG, "Google sign in failed", e)
            setInProgress(false)
            Toast.makeText(this, "La connexion a échoué.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        signInButton = findViewById(R.id.sign_in_button)
        progressBar = findViewById(R.id.progress_bar)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth

        signInButton.setOnClickListener {
            signIn()
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun signIn() {
        setInProgress(true)
        val signInIntent = googleSignInClient.signInIntent
        signInResultLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser!!
                    val userRef = db.collection("users").document(user.uid)
                    userRef.get().addOnSuccessListener { document ->
                        if (document.exists()) {
                            // User exists, go to MainActivity
                            goToMainActivity()
                        } else {
                            // User does not exist, create a new user document
                            val newUser = User(
                                uid = user.uid,
                                displayName = user.displayName,
                                photoUrl = user.photoUrl?.toString()
                            )
                            userRef.set(newUser).addOnSuccessListener {
                                // New user created, go to MainActivity
                                goToMainActivity()
                            }.addOnFailureListener { e ->
                                Log.w(TAG, "Error creating new user", e)
                                // Sign out user if creation fails
                                auth.signOut()
                                Toast.makeText(this, "La création du compte a échoué.", Toast.LENGTH_SHORT).show()
                                setInProgress(false)
                            }
                        }
                    }
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "La connexion a échoué.", Toast.LENGTH_SHORT).show()
                    setInProgress(false)
                }
            }
    }

    private fun goToMainActivity() {
        Toast.makeText(this, "Connexion réussie.", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }


    private fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            signInButton.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
        } else {
            signInButton.visibility = View.VISIBLE
            progressBar.visibility = View.INVISIBLE
        }
    }

    companion object {
        private const val TAG = "SignInActivity"
    }
}
