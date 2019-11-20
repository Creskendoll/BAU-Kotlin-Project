package com.kenansoylu.bauproject.activity

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.FrameLayout
import android.widget.TableLayout
import androidx.appcompat.app.AppCompatActivity
import com.kenansoylu.bauproject.R
import android.view.ViewGroup


class GameActivity : AppCompatActivity() {

    val LEVEL = 1

//    lateinit var cards : List<View>

    private lateinit var gameBoard: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game)

        gameBoard = findViewById(R.id.gameBoard)

        // Camera settings
        val distance = 8000
        val scale = resources.displayMetrics.density * distance
        findViewById<FrameLayout>(R.id.card_front).cameraDistance = scale
        findViewById<FrameLayout>(R.id.card_back).cameraDistance = scale


//        val vi =
//            applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//
//        cards = listOf<View>(
//            vi.inflate(R.layout.game_card, null),
//            vi.inflate(R.layout.game_card, null),
//            vi.inflate(R.layout.game_card, null),
//            vi.inflate(R.layout.game_card, null),
//            vi.inflate(R.layout.game_card, null),
//            vi.inflate(R.layout.game_card, null)
//        )
//
//        cards.forEach {
//            it.findViewById<FrameLayout>(R.id.card_front).cameraDistance = scale
//            it.findViewById<FrameLayout>(R.id.card_back).cameraDistance = scale
//        }
//
//        val noRows = LEVEL * 2
//        val cardPerRow = 3
//
//        for(i in 0..noRows){
//            val tr = vi.inflate(R.layout.table_row, null)
//            val trView = tr.findViewById<TableRow>(R.id.table_row)
//
//            for(j in i*cardPerRow..i+cardPerRow ) {
//                val gameCard =  cards[j].rootView
//                if(trView.parent != null)
//                    (trView.parent as ViewGroup).removeView(trView)
//                trView.addView(gameCard)
//            }
//
//            val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//            gameBoard.addView(trView, 0, layoutParams)
//        }

        // Start animation
        start()
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

    private fun start() {
        val cards = getViewsByTag(gameBoard, "front_visible")

        cards.forEachIndexed { index, view ->
            val firstDelay = ((index * 20) + 100).toLong()
            val secondDelay = ((index * 50) + 1300).toLong()
            fullFlip(view, firstDelay, secondDelay)
        }
    }

    fun flipCard(view: View) {

        // Animators have to be loaded separately for each view
        val mSetRightOut =
            AnimatorInflater.loadAnimator(this, R.animator.out_animation) as AnimatorSet
        val mSetLeftIn =
            AnimatorInflater.loadAnimator(this, R.animator.in_animation) as AnimatorSet

        // Front and back card layouts are defined inside the game card
        val cardBackLayout = view.findViewById<FrameLayout>(R.id.card_back)
        val cardFrontLayout = view.findViewById<FrameLayout>(R.id.card_front)

        val isFrontVisible = view.tag == "front_visible"
        if (!isFrontVisible) {
            // If the back side is visible
            mSetRightOut.setTarget(cardFrontLayout)
            mSetLeftIn.setTarget(cardBackLayout)
            mSetRightOut.start()
            mSetLeftIn.start()
            view.tag = "front_visible"
        } else {
            // If the front side is visible
            mSetRightOut.setTarget(cardBackLayout)
            mSetLeftIn.setTarget(cardFrontLayout)
            mSetRightOut.start()
            mSetLeftIn.start()
            view.tag = "back_visible"
        }
    }
}