package com.kenansoylu.bauproject.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.kenansoylu.bauproject.R
import com.kenansoylu.bauproject.data.UserData
import com.kenansoylu.bauproject.misc.DisplayImage
import com.kenansoylu.bauproject.misc.SharedPreferenceManager
import com.kenansoylu.bauproject.services.UserService

class MainActivity : AppCompatActivity() {

    private val userService = UserService()

    private val auth = FirebaseAuth.getInstance()
    private val spManager = SharedPreferenceManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.leadersBtn).setOnClickListener {
            startActivity(Intent(this@MainActivity, LeadersActivity::class.java))
        }

        findViewById<Button>(R.id.playBtn).setOnClickListener {
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build()
            )
            // Create and launch sign-in intent
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(), 1
            )
        }
        findViewById<Button>(R.id.signOutBtn).setOnClickListener {
            signOut()
        }

        findViewById<ImageView>(R.id.profileAvatar).setOnClickListener {
            startActivity(Intent(this@MainActivity, LeadersActivity::class.java))
        }

//        Log.d("USER DATA:", this.spManager.getUser()?.serialize().toString())

        initUser(this.auth.currentUser)
    }

    private fun setUserFields(userData: UserData) {
        findViewById<TextView>(R.id.nickNameTxt).text = userData.name
        findViewById<TextView>(R.id.scoreTxt).text = "Score: " + userData.scores.firstOrNull()?.toString()
        findViewById<TextView>(R.id.highscoreTxt).text = "High score: " + userData.scores.max()?.toString()
        findViewById<Button>(R.id.signOutBtn).visibility = View.VISIBLE
        DisplayImage(findViewById(R.id.profileAvatar)).execute(userData.avatarURI)
    }

    private fun initUser(user: FirebaseUser?) {
        val userData = this.spManager.getUser()
        Log.d("USER DATA:", userData.toString())

        if (userData != null) {
            setUserFields(userData)
        } else {
            findViewById<Button>(R.id.signOutBtn).visibility = View.GONE

            if (user != null) {
                userService.getUser(user, ::setUserFields, ::onError)
            }
        }

    }

    private fun signOut() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener {
            findViewById<Button>(R.id.signOutBtn).visibility = View.GONE
            findViewById<TextView>(R.id.nickNameTxt).text = ""
            findViewById<TextView>(R.id.highscoreTxt).text = ""
            findViewById<TextView>(R.id.scoreTxt).text = ""
            findViewById<ImageView>(R.id.profileAvatar).setImageResource(R.mipmap.ic_first_avatar)
            Toast.makeText(applicationContext, "Successfully signed out.", Toast.LENGTH_SHORT)
                .show()
        }

        // Clear login data
        this.spManager.deleteUser()
    }

    private fun onNewUser(userRef: DocumentReference) {
        userRef.get().addOnSuccessListener {
            DisplayImage(findViewById(R.id.profileAvatar)).execute(it["avatarURI"] as String)
            findViewById<TextView>(R.id.highscoreTxt).text = "0"
            findViewById<TextView>(R.id.scoreTxt).text = "0"
        }
    }

    private fun onError(e: Exception) {
        Log.e("ERROR", e.localizedMessage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            val response = IdpResponse.fromResultIntent(data)


            if (resultCode == Activity.RESULT_OK) {
                // Might have to get a new instance
//                val user = FirebaseAuth.getInstance().currentUser
                val user = this.auth.currentUser
                val defaultPic =
                    "https://external-content.duckduckgo.com/iu/?u=http%3A%2F%2Fwww.commondreams.org%2Fsites%2Fdefault%2Ffiles%2Fimce-images%2Fmad_dog.jpg&f=1&nofb=1"
                val defaultName = "Guest"
                val userData = UserData(
                    user?.uid ?: "-1",
                    user?.displayName ?: defaultName,
                    defaultPic,
                    listOf(0)
                )

                // Will not run if response is null
                if (response?.isNewUser == true) {
                    userService.addUser(userData, ::onNewUser, ::onError)
                    Toast.makeText(
                        applicationContext,
                        "Successfully created user.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Successfully signed in.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                spManager.saveUser(userData)

                initUser(user)
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Toast.makeText(applicationContext, "Sign in cancelled!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
