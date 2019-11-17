package com.kenansoylu.bauproject.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kenansoylu.bauproject.R
import com.kenansoylu.bauproject.adapter.LeadersAdapter
import com.kenansoylu.bauproject.data.UserData
import com.kenansoylu.bauproject.services.UserService
import kotlinx.android.synthetic.main.activity_leaders.*
import java.lang.Exception

class LeadersActivity : AppCompatActivity() {

    private var userService = UserService(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaders)

        userService = UserService(applicationContext)

        this.userService.getAllUsers(::populateList, ::onError)
    }

    private fun onLeaderClick(userData: UserData) {
        val profileIntent = Intent(this@LeadersActivity, ProfileActivity::class.java)
        profileIntent.putExtra("is_user", false)
        profileIntent.putExtra("player_id", userData.id)
        startActivity(profileIntent)
    }

    private fun populateList(users: List<UserData>) {
        leadersList.layoutManager = LinearLayoutManager(this)
        leadersList.adapter = LeadersAdapter(users, ::onLeaderClick)
    }

    private fun onError(e: Exception) {
        Log.e("LEADERS", "Can't get players.", e)
    }
}