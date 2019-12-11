package com.kenansoylu.bauproject.activity

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import androidx.recyclerview.widget.GridLayoutManager
import com.kenansoylu.bauproject.R
import com.kenansoylu.bauproject.adapter.GameBoardAdapter
import com.kenansoylu.bauproject.data.CardData
import com.kenansoylu.bauproject.misc.Helpers
import com.kenansoylu.bauproject.misc.SharedPreferenceManager
import com.kenansoylu.bauproject.services.UserService
import kotlinx.android.synthetic.main.activity_game.*
import java.lang.Exception
import kotlin.math.ceil
import kotlin.math.log
import kotlin.math.roundToInt


class GameActivity : AppCompatActivity() {

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

    private val resolvedCards = mutableListOf<Pair<View, CardData>>()
    private val flipStack = mutableListOf<Pair<View, CardData>>()

    private var gameSize = 6
    private var gameRunning = false
    private var score = 0L

    private lateinit var timerTxt: TextView
    private lateinit var scoreTxt: TextView
    private lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        setSupportActionBar(findViewById(R.id.game_toolbar))

        initGame()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.game_toolbar, menu)

        // TODO: Remove title from toolbar
        actionBar?.run {
            setDisplayShowTitleEnabled(false)
        }

        menu?.let {
            val menuTimer = it.findItem(R.id.menu_timer)
            timerTxt = menuTimer.actionView as TextView
            timerTxt.textSize = 25f
            timerTxt.setPadding(0, 0, 100, 0)

            val menuScore = it.findItem(R.id.menu_score)
            scoreTxt = menuScore.actionView as TextView
            scoreTxt.textSize = 25f
            scoreTxt.setPadding(0, 0, 20, 0)
            scoreTxt.text = "0"
        }

        startTimer(30000)

        return super.onCreateOptionsMenu(menu)
    }

    private fun onError(e : Exception) {
        Log.d("GAME", e.toString())
    }

    private fun startTimer(duration: Long) {
        timer = object : CountDownTimer(duration, 1000) {
            override fun onFinish() {
                gameRunning = false

                val spManager = SharedPreferenceManager(applicationContext)
                val userService = UserService(applicationContext)
                val oldUser = spManager.getUser()
                val loading = ProgressDialog.show(
                    this@GameActivity, "",
                    "Saving Score...", true
                )

                spManager.getUser()?.let {
                    it.scores.add(score)
                    spManager.saveUser(it)
                    userService.updateUser(oldUser!!, it, {
                        val endIntent = Intent(this@GameActivity, EndActivity::class.java)
                        endIntent.putExtra("score", score.toString())
                        loading.dismiss()
                        Toast.makeText(applicationContext, "Saved score!", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(endIntent)
                        finish()
                    }, ::onError)
                }
            }

            override fun onTick(millisecondsLeft: Long) {
                val secondsLeft =
                    (millisecondsLeft / 1000.toDouble()).roundToInt()
                timerTxt.text = Helpers.secondsToString(secondsLeft)
            }
        }
        timer.start()
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

        val gridManager = GridLayoutManager(this, 3)
        val cards = getCards(gameSize)

        with(gameBoardRV) {
            layoutManager = gridManager
            adapter = GameBoardAdapter(cards, ::onCardClick)
        }

        // TODO: Find a better way to run initGame after recycler view mounts
        // Delay animation
        Handler().postDelayed({
            val cardsViews = getViewsByTag(gameBoardRV, "front_visible")

            // Flip the cards twice with a delay
            cardsViews.forEachIndexed { index, view ->
                val firstDelay = ((index * 50) + 100).toLong()
                val secondDelay = ((index * 50) + 1500).toLong()
                fullFlip(view, firstDelay, secondDelay)
            }
            gameRunning = true
        }, 200)
    }

    private fun onCardClick(cardView: View, cardData: CardData) {
        val newFlip = Pair(cardView, cardData)

        // If the card is clickable
        if (!resolvedCards.contains(newFlip) && gameRunning && !flipStack.contains(newFlip)) {
            // Animate and set tag
            flipCard(cardView)
            val prevFlip = flipStack.lastOrNull()

            prevFlip?.let {
                // If the card is clicked after another
                if(it.second.imageResource == cardData.imageResource){
                    // If cards match
                    // Add new card to the beginning of the list
                    resolvedCards.add(it)
                    resolvedCards.add(newFlip)
                    flipStack.remove(it)

                    // Set score
                    score += cardData.point
                    scoreTxt.text = score.toString()
                } else {
                    // If cards don't match

                    Handler().postDelayed({
                        flipCard(it.first)
                        flipCard(cardView)
                    }, 600)
                    flipStack.remove(it)
                }
            } ?: let {
                // If the card is the first one to be flipped
                flipStack.add(newFlip)
            }

            // If level is completed
            if(resolvedCards.size == gameSize){
                gameRunning = false
                // If all the cards are opened

                // flip back the open cards and init a new game
                val baseDelay = 400L
                val incrementDelay = 50L
                val totalDelay = baseDelay*2 + (gameSize*incrementDelay)
                getViewsByTag(gameBoardRV, "back_visible").forEachIndexed { index, view->
                    val delay = baseDelay + (index * incrementDelay)
                    Handler().postDelayed({
                        flipCard(view)
                    }, delay)
                }

                // init a new game
                Handler().postDelayed({
                    resolvedCards.removeAll { true }
                    flipStack.removeAll { true }
                    gameSize += 2
                    initGame()
                }, totalDelay)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        timer.cancel()
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