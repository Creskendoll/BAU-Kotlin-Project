package com.kenansoylu.bauproject.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.kenansoylu.bauproject.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.leadersBtn).setOnClickListener{
            startActivity(Intent(this@MainActivity, LeadersActivity::class.java))
        }
    }
}
