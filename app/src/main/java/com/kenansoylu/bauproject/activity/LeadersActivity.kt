package com.kenansoylu.bauproject.activity

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

    private val userService = UserService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaders)

        this.userService.getAllUsers(::populateList, ::onError)
    }

    private fun populateList(users: List<UserData>) {
        val mLayoutManager = LinearLayoutManager(this)
        with(leadersList) {
            layoutManager = mLayoutManager
            adapter = LeadersAdapter(users, this@LeadersActivity)
        }
    }

    private fun onError(e: Exception) {
        Log.e("LEADERS", "Can't get players.", e)
    }
}