package com.kenansoylu.bauproject.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kenansoylu.bauproject.R
import com.kenansoylu.bauproject.data.UserData
import com.kenansoylu.bauproject.misc.DisplayImage
import com.kenansoylu.bauproject.misc.SharedPreferenceManager
import com.kenansoylu.bauproject.services.UserService

class MainActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    private val LOGIN_CODE = 1

    private lateinit var spManager: SharedPreferenceManager
    private lateinit var userService: UserService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Init services
        spManager = SharedPreferenceManager(applicationContext)
        userService = UserService(applicationContext)

        findViewById<Button>(R.id.leadersBtn).setOnClickListener {
            if (this.auth.currentUser == null) // If user is not signed in
                signIn()
            else // If the user is signed in start the leaders activity
                startActivity(Intent(this@MainActivity, LeadersActivity::class.java))
        }
        findViewById<Button>(R.id.playBtn).setOnClickListener {
            if (this.auth.currentUser == null) // If the user is not signed in
                signIn()
            else // Start game if the user is signed in
                startActivity(Intent(this@MainActivity, GameActivity::class.java))
        }
        findViewById<Button>(R.id.signOutBtn).setOnClickListener {
            signOut()
        }
        findViewById<ImageButton>(R.id.profileAvatar).setOnClickListener {
            // Navigate to profile edit activity if the user is signed in
            if (this.auth.currentUser != null) {
                val profileIntent = Intent(this@MainActivity, ProfileActivity::class.java)
                profileIntent.putExtra("player_id", this.auth.currentUser!!.uid)
                startActivity(profileIntent)
            } else
                signIn()
        }
        initUser(this.auth.currentUser)
    }

    private fun setUserFields(userData: UserData) {
        // Set UI fields
        findViewById<TextView>(R.id.nickNameTxt).text = userData.name
        findViewById<TextView>(R.id.scoreTxt).text =
            "Latest Score : ${userData.scores.lastOrNull()}"
        findViewById<TextView>(R.id.highscoreTxt).text =
            "${getString(R.string.high_score)} : ${userData.scores.sum()}"
        findViewById<Button>(R.id.signOutBtn).visibility = View.VISIBLE
        DisplayImage(findViewById(R.id.profileAvatar)).execute(userData.avatarURI)
    }

    private fun initUser(user: FirebaseUser?) {
        //Initialize the user using a firebase user
        val userData = this.spManager.getUser()

        if (userData != null) {
            setUserFields(userData)
        } else {
            // Hide sign out button
            findViewById<Button>(R.id.signOutBtn).visibility = View.GONE

            if (user != null) {
                userService.getUser(user, ::setUserFields, ::onError)
            }
        }

    }

    private fun signIn() {
        // Sign in with email
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )
        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(), LOGIN_CODE
        )
    }

    private fun signOut() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener {
            // Reset UI fields
            findViewById<Button>(R.id.signOutBtn).visibility = View.GONE
            findViewById<TextView>(R.id.nickNameTxt).text = ""
            findViewById<TextView>(R.id.highscoreTxt).text = ""
            findViewById<TextView>(R.id.scoreTxt).text = ""
            findViewById<ImageButton>(R.id.profileAvatar).setImageResource(R.mipmap.ic_first_avatar)
            Toast.makeText(applicationContext, "Successfully signed out.", Toast.LENGTH_SHORT)
                .show()
        }

        // Clear login data
        this.spManager.deleteUser()
    }

    private fun onNewUser(userData: UserData) {
        DisplayImage(findViewById(R.id.profileAvatar)).execute(userData.avatarURI)
        findViewById<TextView>(R.id.highscoreTxt).text = "0"
        findViewById<TextView>(R.id.scoreTxt).text = "0"
    }

    private fun onError(e: Exception) {
        Log.e("ERROR", e.localizedMessage)
    }

    override fun onRestart() {
        super.onRestart()
        // We don't have to pass a firebase user since we expect to have the user data
        // in the shared preferences
        initUser(null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LOGIN_CODE) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                FirebaseAuth.getInstance().currentUser?.let {
                    // Will not run if response is null
                    if (response?.isNewUser == true) {
                        // NEW USER
                        val defaultPic =
                            "https://pbs.twimg.com/profile_images/1002933519629389824/rMED8za4_400x400.jpg"
                        val defaultName = "Guest"
                        // Create default user with default values
                        val userData = UserData(
                            it.uid,
                            it.displayName ?: defaultName,
                            defaultPic,
                            mutableListOf(0) // Scores are 0
                        )

                        val that = it
                        userService.addUser(userData, { run {
                            onNewUser(userData)
                            initUser(that)
                         } }, ::onError)
                        Toast.makeText(
                            applicationContext,
                            "Successfully created user.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // EXISTING USER
                        val that = it
                        // Save user to shared preferences and initialize user
                        userService.getUser(it, { userData -> run {
                            spManager.saveUser(userData)
                            initUser(that)
                        } }, ::onError)

                        Toast.makeText(
                            applicationContext,
                            "Successfully signed in.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                // Sign in failed
                Toast.makeText(applicationContext, "Sign in cancelled!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
