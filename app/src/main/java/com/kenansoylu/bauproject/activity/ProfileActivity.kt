package com.kenansoylu.bauproject.activity

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.kenansoylu.bauproject.R
import com.kenansoylu.bauproject.data.UserData
import com.kenansoylu.bauproject.misc.DisplayImage
import com.kenansoylu.bauproject.services.UserService

class ProfileActivity : AppCompatActivity() {

    private var userService = UserService(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userService = UserService(applicationContext)

        setContentView(R.layout.activity_profile)

        val isUser = intent.getBooleanExtra("is_user", false)
        val playerID = intent.getStringExtra("player_id")

        findViewById<EditText>(R.id.nickNameTxt).isEnabled = isUser
        findViewById<Button>(R.id.updateBtn).visibility = if (isUser) View.VISIBLE else View.GONE

        populateScreen(playerID)
    }

    private fun onGetUser(userData: UserData) {
        val nameEditTxt = findViewById<EditText>(R.id.nickNameTxt)

        val highScoreStr = userData.scores.max().toString()
        findViewById<TextView>(R.id.highscoreTxt).text =
            "${getString(R.string.high_score)} : ${highScoreStr}"
        nameEditTxt.text = Editable.Factory.getInstance().newEditable(userData.name)
        DisplayImage(findViewById(R.id.profileAvatar)).execute(userData.avatarURI)

        findViewById<Button>(R.id.updateBtn).setOnClickListener {
            val newUser = UserData(
                userData.id,
                nameEditTxt.text.toString(),
                userData.avatarURI,
                userData.scores
            )
            userService.updateUser(userData, newUser, {
                Toast.makeText(applicationContext, "Successfully updated user.", Toast.LENGTH_SHORT)
                    .show()
            }, {
                Toast.makeText(applicationContext, "Failed to update user!", Toast.LENGTH_SHORT)
                    .show()
            })
        }
    }

    private fun onError(e: Exception) {
        Log.e("PROFILE", e.toString())
    }

    private fun populateScreen(playerID: String) {
        userService.getUserByID(playerID, ::onGetUser, ::onError)
    }

}