package com.kenansoylu.bauproject.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kenansoylu.bauproject.R
import com.kenansoylu.bauproject.adapter.LeadersAdapter
import com.kenansoylu.bauproject.data.UserData
import com.kenansoylu.bauproject.misc.SharedPreferenceManager
import com.kenansoylu.bauproject.services.UserService
import kotlinx.android.synthetic.main.activity_leaders.*
import java.lang.Exception

class LeadersActivity : AppCompatActivity() {

    private lateinit var userService: UserService
    private lateinit var spManager: SharedPreferenceManager
    private lateinit var swipeRefresh: SwipeRefreshLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaders)

        swipeRefresh = findViewById(R.id.swipeRefresh)
        swipeRefresh.isRefreshing = true

        userService = UserService(applicationContext)
        spManager = SharedPreferenceManager(applicationContext)

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
        // Sort users by their high score
        val sortedUsers = users.sortedBy { userData ->
            userData.scores.max()
        }.reversed()
        spManager.getUser()?.let {
            leadersList.layoutManager = LinearLayoutManager(this)
            leadersList.adapter = LeadersAdapter(sortedUsers, ::onLeaderClick, it.id)
            swipeRefresh.isRefreshing = false
        }
    }

    private fun onError(e: Exception) {
        Toast.makeText(applicationContext, "Could't connect!", Toast.LENGTH_SHORT)
            .show()
        Log.e("LEADERS", "Can't get players.", e)
    }
}