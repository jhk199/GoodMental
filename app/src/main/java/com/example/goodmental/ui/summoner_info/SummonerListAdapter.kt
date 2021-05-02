package com.example.goodmental.ui.summoner_info

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.goodmental.R
import com.squareup.picasso.Picasso

class SummonerListAdapter (private val dataSet:List<Summoner>) : RecyclerView.Adapter<SummonerListAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view : View) : RecyclerView.ViewHolder(view) {
        val textViewName : TextView = view.findViewById(R.id.textView_followedSummName)
        val textViewRegion : TextView = view.findViewById(R.id.textView_followedSummRegion)
        val imageViewSumm : ImageView = view.findViewById(R.id.imageView_followedSumm)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.item_summoner, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var summoner : Summoner = dataSet[position]
        holder.textViewName.text = summoner.name
        holder.textViewRegion.text = summoner.region
        Picasso.get().load(summoner.pictureURL).into(holder.imageViewSumm)
        //Log.d("Pic", summoner.pictureURL)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}