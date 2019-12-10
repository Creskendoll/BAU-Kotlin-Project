package com.kenansoylu.bauproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kenansoylu.bauproject.R

class ScoresAdapter(
    private val scores: List<Long>
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
        holder.bind(position, scores[position])
    }

    class ScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(index : Int, score: Long) {
            // Set index and score
            itemView.findViewById<TextView>(R.id.scoreText).text = score.toString()
            itemView.findViewById<TextView>(R.id.indexTxt).text = "Game: " + (1 + index).toString()
        }
    }
}