package com.kenansoylu.bauproject.adapter

import android.graphics.Color
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
    private val onClick: (UserData) -> Unit,
    private val currentUserId : String
) : RecyclerView.Adapter<LeadersAdapter.LeadersViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeadersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.player_row, parent, false)
        return LeadersViewHolder(view)
    }

    override fun getItemCount() = leaders.size

    override fun onBindViewHolder(holder: LeadersViewHolder, position: Int) {
        holder.bind(leaders[position], onClick, currentUserId)
    }

    class LeadersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(userData: UserData, clickListener: (UserData) -> Unit, currentUserId: String) {
            if(currentUserId == userData.id)
                itemView.setBackgroundColor(Color.parseColor("#7403D8F4"))

            itemView.playerName.text = userData.name
            itemView.playerScore.text = userData.scores.sum().toString()
            DisplayImage(itemView.playerAvatar).execute(userData.avatarURI)
            itemView.setOnClickListener { clickListener(userData) }
        }
    }
}