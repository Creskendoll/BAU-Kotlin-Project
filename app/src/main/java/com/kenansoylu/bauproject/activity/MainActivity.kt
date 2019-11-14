package com.kenansoylu.bauproject.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kenansoylu.bauproject.R
import com.kenansoylu.bauproject.misc.SharedPreferenceManager

class MainActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val spManager = SharedPreferenceManager(this)

    private val LOGIN_KEY = "loggedIn"

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

        initUser(this.auth.currentUser)
    }

    private fun initUser(user: FirebaseUser?) {
        val userData = this.spManager.getData(LOGIN_KEY)

        // TODO: set a boolean for login add to and check shared preferences
        if (user != null) {
            findViewById<TextView>(R.id.nickNameTxt).text = user.displayName
            findViewById<Button>(R.id.signOutBtn).visibility = View.VISIBLE
        } else {
            findViewById<Button>(R.id.signOutBtn).visibility = View.GONE
        }
    }

    private fun signOut() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener {
            findViewById<Button>(R.id.signOutBtn).visibility = View.GONE
            findViewById<TextView>(R.id.nickNameTxt).text = ""
            findViewById<TextView>(R.id.highscoreTxt).text = ""
            findViewById<TextView>(R.id.scoreTxt).text = ""
            Toast.makeText(applicationContext, "Successfully signed out.", Toast.LENGTH_SHORT).show()
        }

        // Clear login data
        this.spManager.saveData(LOGIN_KEY, "")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in

                // Might have to get a new instance
//                val user = FirebaseAuth.getInstance().currentUser
                val user = this.auth.currentUser

                initUser(user)

                val userData = user!!.uid
                Log.d("USER DATA:", userData)

                this.spManager.saveData(LOGIN_KEY, userData)
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
