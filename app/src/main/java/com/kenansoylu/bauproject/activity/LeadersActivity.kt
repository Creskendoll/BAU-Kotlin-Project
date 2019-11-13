package com.kenansoylu.bauproject.activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.kenansoylu.bauproject.R
import com.kenansoylu.bauproject.adapter.LeadersAdapter
import com.kenansoylu.bauproject.data.PlayerData
import kotlinx.android.synthetic.main.activity_leaders.*

class LeadersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaders)

        getPlayers()
    }

    private fun getPlayers(): List<PlayerData> {

        val db = FirebaseFirestore.getInstance()
        val mLayoutManager = LinearLayoutManager(this)

        db.collection("players").get().addOnSuccessListener { result ->
            val players = result.map { PlayerData(it.id, it.data["name"] as String, it.data["avatar"] as String, it.data["scores"] as List<Int>) }

            with(leadersList) {
                layoutManager = mLayoutManager
                adapter = LeadersAdapter(players, this@LeadersActivity)
            }

//            for (document in result) {
//                Log.d("LEADERS", "${document.id} => ${document.data}")
//            }
        }.addOnFailureListener {exception ->
            Log.w("LEADERS", "Can't get players.", exception)
        }
        return mutableListOf()
    }
}