package com.kenansoylu.bauproject.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kenansoylu.bauproject.R
import com.kenansoylu.bauproject.adapter.LeadersAdapter
import com.kenansoylu.bauproject.data.PlayerData
import com.kenansoylu.bauproject.services.UserService
import kotlinx.android.synthetic.main.activity_leaders.*
import java.lang.Exception

class LeadersActivity : AppCompatActivity() {

    private val userService = UserService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaders)

        this.userService.getAllUsers(::populateList, ::onError)
    }

    private fun populateList(players: List<PlayerData>) {
        val mLayoutManager = LinearLayoutManager(this)
        with(leadersList) {
            layoutManager = mLayoutManager
            adapter = LeadersAdapter(players, this@LeadersActivity)
        }
    }

    private fun onError(e: Exception) {
        Log.e("LEADERS", "Can't get players.", e)
    }
}