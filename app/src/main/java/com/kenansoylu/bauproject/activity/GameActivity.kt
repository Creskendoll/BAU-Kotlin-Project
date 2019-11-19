package com.kenansoylu.bauproject.activity

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import com.kenansoylu.bauproject.R

class GameActivity : AppCompatActivity() {

    private var isBackVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game)

        val cardBackLayout = findViewById<FrameLayout>(R.id.card_back)
        val cardFrontLayout = findViewById<FrameLayout>(R.id.card_front)

        val gameBoard = findViewById<GridLayout>(R.id.gameBoard)

        val distance = 3000
        val scale = resources.displayMetrics.density * distance
        cardFrontLayout.cameraDistance = scale
        cardBackLayout.cameraDistance = scale
    }

    fun flipCard(view: View) {
        val cardBackLayout = findViewById<FrameLayout>(R.id.card_back)
        val cardFrontLayout = findViewById<FrameLayout>(R.id.card_front)

        val mSetRightOut =
            AnimatorInflater.loadAnimator(this, R.animator.out_animation) as AnimatorSet
        val mSetLeftIn =
            AnimatorInflater.loadAnimator(this, R.animator.in_animation) as AnimatorSet


        isBackVisible = if (!isBackVisible) {
            mSetRightOut.setTarget(cardFrontLayout)
            mSetLeftIn.setTarget(cardBackLayout)
            mSetRightOut.start()
            mSetLeftIn.start()
            true
        } else {
            mSetRightOut.setTarget(cardBackLayout)
            mSetLeftIn.setTarget(cardFrontLayout)
            mSetRightOut.start()
            mSetLeftIn.start()
            false
        }
    }
}