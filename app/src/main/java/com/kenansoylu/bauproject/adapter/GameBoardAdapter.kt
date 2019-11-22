package com.kenansoylu.bauproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.kenansoylu.bauproject.R
import com.kenansoylu.bauproject.data.CardData

class GameBoardAdapter(
    private val cards: List<CardData>,
    private val onClick: (View, CardData) -> Unit
) : RecyclerView.Adapter<GameBoardAdapter.BoardViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BoardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(
            R.layout.game_card,
            parent,
            false
        )
        return BoardViewHolder(view)
    }

    override fun getItemCount() = cards.size

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        holder.bind(cards[position], onClick)
    }

    class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Camera settings
        private val distance = 8000
        private val scale = 3.0f * distance

        fun bind(cardData: CardData, clickListener: (View, CardData) -> Unit) {

            itemView.findViewById<FrameLayout>(R.id.card_front).cameraDistance = scale
            itemView.findViewById<FrameLayout>(R.id.card_back).cameraDistance = scale

            itemView.findViewById<ImageView>(R.id.front_image).setImageResource(cardData.imageResource)
            itemView.setOnClickListener { clickListener(itemView, cardData) }
        }
    }
}