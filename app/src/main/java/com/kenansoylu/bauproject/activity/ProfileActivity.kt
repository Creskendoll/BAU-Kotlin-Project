package com.kenansoylu.bauproject.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.kenansoylu.bauproject.R
import com.kenansoylu.bauproject.data.UserData
import com.kenansoylu.bauproject.misc.DisplayImage
import com.kenansoylu.bauproject.misc.SharedPreferenceManager
import com.kenansoylu.bauproject.services.UserService

class ProfileActivity : AppCompatActivity() {

    private lateinit var userService: UserService
    private lateinit var spManager: SharedPreferenceManager
    private lateinit var playerID: String
    private lateinit var userID: String
    private val AVATAR_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userService = UserService(applicationContext)
        spManager = SharedPreferenceManager(applicationContext)

        setContentView(R.layout.activity_profile)

        userID = FirebaseAuth.getInstance().currentUser!!.uid
        playerID = intent.getStringExtra("player_id")

        // Initially hide button
        findViewById<Button>(R.id.updateBtn).visibility = View.GONE

        findViewById<ImageButton>(R.id.profileAvatar).setOnClickListener {
            if (userID == playerID) {
                val avatarsIntent = Intent(this@ProfileActivity, ChangeAvatarActivity::class.java)
                startActivityForResult(avatarsIntent, AVATAR_REQUEST)
            }
        }

        populateScreen(playerID)
    }

    private fun updateUser(oldUser : UserData, newUser : UserData) {
        userService.updateUser(oldUser, newUser, {
            Toast.makeText(applicationContext, "Successfully updated user.", Toast.LENGTH_SHORT)
                .show()

            val nameEditTxt = findViewById<EditText>(R.id.nickNameTxt)
            // Hide keyboard
            val imm =
                applicationContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(nameEditTxt.windowToken, 0)
        }, {
            Toast.makeText(applicationContext, "Failed to update user!", Toast.LENGTH_SHORT)
                .show()
        })
    }

    private fun onGetUser(userData: UserData) {
        val isUser = playerID == userID

        findViewById<EditText>(R.id.nickNameTxt).isEnabled = isUser
        findViewById<Button>(R.id.updateBtn).visibility = if (isUser) View.VISIBLE else View.GONE

        val nameEditTxt = findViewById<EditText>(R.id.nickNameTxt)

        val highScoreStr = userData.scores.max().toString()
        findViewById<TextView>(R.id.highscoreTxt).text =
            "${getString(R.string.high_score)} : ${highScoreStr}"
        nameEditTxt.text = Editable.Factory.getInstance().newEditable(userData.name)
        DisplayImage(findViewById(R.id.profileAvatar)).execute(userData.avatarURI)

        // Update user on button click
        findViewById<Button>(R.id.updateBtn).setOnClickListener {
            val newUser = UserData(
                userData.id,
                nameEditTxt.text.toString(),
                userData.avatarURI,
                userData.scores
            )
            updateUser(userData, newUser)
        }
    }

    private fun onError(e: Exception) {
        Log.e("PROFILE", e.toString())
    }

    private fun populateScreen(playerID: String) {
        userService.getUserByID(playerID, ::onGetUser, ::onError)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AVATAR_REQUEST && resultCode == RESULT_OK) {
            val newAvatar = data?.getStringExtra("avatar_url")
            val curUser = spManager.getUser()
            newAvatar?.let {
                curUser?.let {
                    val newUser = UserData(
                        it.id,
                        it.name,
                        newAvatar,
                        it.scores
                    )
                    updateUser(curUser, newUser)

                    DisplayImage(findViewById(R.id.profileAvatar)).execute(newAvatar)
                }
            }
        }

    }

}