package com.kenansoylu.bauproject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kenansoylu.bauproject.R
import com.kenansoylu.bauproject.data.UserData
import com.kenansoylu.bauproject.misc.DisplayImage
import kotlinx.android.synthetic.main.player_row.view.*


class LeadersViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val avatarIV = view.playerAvatar
    val nameTV = view.playerName
    val scoreTV = view.playerScore
}

class LeadersAdapter(
    val leaders: List<UserData>,
    val context: Context
) : RecyclerView.Adapter<LeadersViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeadersViewHolder {
        return LeadersViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.player_row,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = leaders.size

    override fun onBindViewHolder(holder: LeadersViewHolder, position: Int) {
        with(leaders[position]) {
            holder.nameTV.text = name
            holder.scoreTV.text = scores.max().toString()
            DisplayImage(holder.avatarIV).execute(avatarURI)
        }
    }

}