package com.kenansoylu.bauproject.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kenansoylu.bauproject.R

class EndActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end)

        // Set score
        findViewById<TextView>(R.id.finalScoreTxt).text = intent.getStringExtra("score")

        findViewById<Button>(R.id.retryBtn).setOnClickListener {
            startActivity(Intent(this@EndActivity, GameActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.quitBtn).setOnClickListener {
            startActivity(Intent(this@EndActivity, MainActivity::class.java))
            finish()
        }
    }
}