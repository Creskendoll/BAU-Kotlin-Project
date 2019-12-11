package com.kenansoylu.bauproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kenansoylu.bauproject.R

class ScoresAdapter(
    private val scores: List<Pair<Int, Long>>
) : RecyclerView.Adapter<ScoresAdapter.ScoreViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScoreViewHolder{
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(
            R.layout.score_row,
            parent,
            false
        )
        return ScoreViewHolder(view)
    }

    override fun getItemCount() = scores.size

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        holder.bind(scores[position])
    }

    class ScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(score: Pair<Int,Long>) {
            // Set index and score
            itemView.findViewById<TextView>(R.id.scoreText).text = score.second.toString()
            itemView.findViewById<TextView>(R.id.indexTxt).text = "Game: " + score.first .toString()
        }
    }
}