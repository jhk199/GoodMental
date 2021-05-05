package com.example.goodmental.ui.match_info

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.goodmental.R
import com.example.goodmental.ui.summoner_info.Summoner
import com.example.goodmental.ui.summoner_info.SummonerListAdapter
import com.squareup.picasso.Picasso

class MatchListAdapter : ListAdapter<Match, MatchListAdapter.MatchViewHolder>(MatchComparator()) {

    class MatchComparator: DiffUtil.ItemCallback<Match>() {
        override fun areItemsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem.name == newItem.name
        }

    }
    class MatchViewHolder(private val view : View) : RecyclerView.ViewHolder(view) {
        val textViewName : TextView = view.findViewById(R.id.textView_champName)
        val textViewDate : TextView = view.findViewById(R.id.textView_time)
        val textViewWinLoss : TextView = view.findViewById(R.id.textView_winLoss)
        val imageViewChamp : ImageView = view.findViewById(R.id.imageView_champ)

        fun bindText(text : String?, textView: TextView) {
            textView.text = text
        }




        companion object {
            fun create (parent: ViewGroup) : MatchListAdapter.MatchViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_match, parent, false)
                return MatchListAdapter.MatchViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.item_match, parent, false)
        return MatchViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val currentMatch = getItem(position)
        if(currentMatch.winLoss == "W") {
            holder.textViewWinLoss.setTextColor(Color.GREEN)
        }
        else {
            holder.textViewWinLoss.setTextColor(Color.RED)
        }
        holder.bindText(currentMatch.name, holder.textViewName)
        holder.bindText(currentMatch.date, holder.textViewDate)
        holder.bindText(currentMatch.winLoss, holder.textViewWinLoss)
        Picasso.get().load(currentMatch.pictureURL).into(holder.imageViewChamp)
    }

    override fun submitList(list: List<Match>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
}