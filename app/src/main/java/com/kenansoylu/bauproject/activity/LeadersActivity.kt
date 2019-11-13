package com.kenansoylu.bauproject.activity

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

        val db = FirebaseFirestore.getInstance()
        val mlayoutManager = LinearLayoutManager(this)

        with(leadersList) {
            layoutManager = mlayoutManager
            adapter = LeadersAdapter(getPlayers(db), this@LeadersActivity)
        }
    }

    private fun getPlayers(db: FirebaseFirestore): MutableList<PlayerData> {
        db.collection("players").get().addOnSuccessListener { result ->
            for (document in result) {
                Log.d("LEADERS", "${document.id} => ${document.data}")
            }
        }.addOnFailureListener {exception ->
            Log.w("LEADERS", "Can't get players.", exception)
        }
        return mutableListOf()
    }
}