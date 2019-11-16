package com.kenansoylu.bauproject.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kenansoylu.bauproject.data.CardData

class GameBoardAdapter(
    val cards: List<CardData>,
    val onClick: (CardData) -> Unit
) : RecyclerView.Adapter<GameBoardAdapter.BoardViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BoardViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount() = cards.size

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(cardData: CardData, clickListener: (CardData) -> Unit) {
            itemView.setOnClickListener { clickListener(cardData) }
        }
    }
}