package com.kenansoylu.bauproject.activity

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.kenansoylu.bauproject.R
import com.kenansoylu.bauproject.adapter.GameBoardAdapter
import com.kenansoylu.bauproject.data.CardData
import kotlinx.android.synthetic.main.activity_game.*
import kotlin.math.ceil

class GameActivity : AppCompatActivity() {
    private val LEVEL = 1

    private val cardImages = listOf(
        R.mipmap.go,
        R.mipmap.cs,
        R.mipmap.cpp,
        R.mipmap.java,
        R.mipmap.js,
        R.mipmap.kotlin,
        R.mipmap.lua,
        R.mipmap.php,
        R.mipmap.py,
        R.mipmap.ruby,
        R.mipmap.rust,
        R.mipmap.ts
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

//        Log.d("RES", resources.displayMetrics.density.toString())

        val gameSize = 2 * LEVEL + 4

        val gridManager = GridLayoutManager(this, 3)
        val cards = getCards(gameSize)

        with(gameBoardRV) {
            layoutManager = gridManager
            adapter = GameBoardAdapter(cards, ::onCardClick)
        }

        // TODO: Find a better way to run initGame after recycler view mounts
        // Delay animation
        Handler().postDelayed({
            initGame()
        }, 100)
    }

    private fun flipCard(cardView: View) {
        // Animators have to be loaded separately for each view
        val rightOut =
            AnimatorInflater.loadAnimator(this, R.animator.out_animation) as AnimatorSet
        val leftIn =
            AnimatorInflater.loadAnimator(this, R.animator.in_animation) as AnimatorSet

        // Front and back card layouts are defined inside the game card
        val cardBackLayout = cardView.findViewById<FrameLayout>(R.id.card_back)
        val cardFrontLayout = cardView.findViewById<FrameLayout>(R.id.card_front)

        // Animate card
        val isFrontVisible = cardView.tag == "front_visible"
        if (!isFrontVisible) {
            // If the back side is visible
            rightOut.setTarget(cardFrontLayout)
            leftIn.setTarget(cardBackLayout)
            rightOut.start()
            leftIn.start()
            cardView.tag = "front_visible"
        } else {
            // If the front side is visible
            rightOut.setTarget(cardBackLayout)
            leftIn.setTarget(cardFrontLayout)
            rightOut.start()
            leftIn.start()
            cardView.tag = "back_visible"
        }
    }

    private fun fullFlip(view: View, firstDelay: Long, secondDelay: Long) {
        // Initial flip with the initial delay
        Handler().postDelayed({
            flipCard(view)
        }, firstDelay)

        // Second flip
        Handler().postDelayed({
            flipCard(view)
        }, secondDelay)
    }

    private fun initGame() {
        val cardsViews = getViewsByTag(gameBoardRV, "front_visible")

        // Flip the cards twice with a delay
        cardsViews.forEachIndexed { index, view ->
            val firstDelay = ((index * 50) + 100).toLong()
            val secondDelay = ((index * 50) + 1300).toLong()
            fullFlip(view, firstDelay, secondDelay)
        }
    }

    private fun onCardClick(cardView: View, cardData: CardData) {
        flipCard(cardView)
    }

    private fun getCards(count: Int): List<CardData> {
        val shuffledImages = cardImages.shuffled()
        // Get N random card images
        // We need half the number of images for the cards
        val selectedImages = (0..count / 2).map { shuffledImages[it] }

        // Randomly set pairs
        return (0 until count).map {
            if (it % 2 == 0)
                CardData(selectedImages[it / 2], (count * 10).toLong())
            else {
                val index = ceil(it / 2.0) - 1
                CardData(selectedImages[index.toInt()], (count * 10).toLong())
            }
        }.shuffled()
    }

    // https://stackoverflow.com/questions/5062264/find-all-views-with-tag
    private fun getViewsByTag(root: ViewGroup, tag: String?): ArrayList<View> {
        val views = ArrayList<View>()
        val childCount = root.childCount
        for (i in 0 until childCount) {
            val child = root.getChildAt(i)
            if (child is ViewGroup) {
                views.addAll(getViewsByTag(child, tag))
            }
            val tagObj = child.tag
            if (tagObj != null && tagObj == tag) {
                views.add(child)
            }
        }
        return views
    }
}