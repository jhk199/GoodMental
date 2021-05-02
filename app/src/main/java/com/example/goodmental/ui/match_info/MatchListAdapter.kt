package com.example.goodmental.ui.match_info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.goodmental.R
import com.squareup.picasso.Picasso

class MatchListAdapter (private val dataSet:List<Match>) : RecyclerView.Adapter<MatchListAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view : View) : RecyclerView.ViewHolder(view) {
        val textViewName : TextView = view.findViewById(R.id.textView_champName)
        val textViewDate : TextView = view.findViewById(R.id.textView_time)
        val imageViewChamp : ImageView = view.findViewById(R.id.imageView_champ)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.item_match, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val match : Match = dataSet[position]
        holder.textViewName.text = match.name
        holder.textViewDate.text = match.date
        Picasso.get().load(match.pictureURL).into(holder.imageViewChamp)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}