package com.kenansoylu.bauproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kenansoylu.bauproject.R
import com.kenansoylu.bauproject.data.UserData
import com.kenansoylu.bauproject.misc.DisplayImage
import kotlinx.android.synthetic.main.player_row.view.*

class LeadersAdapter(
    private val leaders: List<UserData>,
    private val onClick: (UserData) -> Unit
) : RecyclerView.Adapter<LeadersAdapter.LeadersViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeadersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.player_row, parent, false)
        return LeadersViewHolder(view)
    }

    override fun getItemCount() = leaders.size

    override fun onBindViewHolder(holder: LeadersViewHolder, position: Int) {
        holder.bind(leaders[position], onClick)
    }

    class LeadersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(userData: UserData, clickListener: (UserData) -> Unit) {
            itemView.playerName.text = userData.name
            itemView.playerScore.text = userData.scores.max().toString()
            DisplayImage(itemView.playerAvatar).execute(userData.avatarURI)
            itemView.setOnClickListener { clickListener(userData) }
        }
    }
}