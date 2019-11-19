package com.kenansoylu.bauproject.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kenansoylu.bauproject.R
import com.kenansoylu.bauproject.adapter.LeadersAdapter
import com.kenansoylu.bauproject.data.UserData
import com.kenansoylu.bauproject.services.UserService
import kotlinx.android.synthetic.main.activity_leaders.*
import java.lang.Exception

class LeadersActivity : AppCompatActivity() {

    private lateinit var userService: UserService
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaders)

        swipeRefresh = findViewById(R.id.swipeRefresh)
        swipeRefresh.isRefreshing = true

        userService = UserService(applicationContext)

        swipeRefresh.setOnRefreshListener {
            this.userService.getAllUsers(::populateList, ::onError)
        }

        this.userService.getAllUsers(::populateList, ::onError)
    }

    private fun onLeaderClick(userData: UserData) {
        val profileIntent = Intent(this@LeadersActivity, ProfileActivity::class.java)
        profileIntent.putExtra("player_id", userData.id)
        startActivity(profileIntent)
    }

    private fun populateList(users: List<UserData>) {
        leadersList.layoutManager = LinearLayoutManager(this)
        leadersList.adapter = LeadersAdapter(users, ::onLeaderClick)
        swipeRefresh.isRefreshing = false
    }

    private fun onError(e: Exception) {
        Log.e("LEADERS", "Can't get players.", e)
    }
}