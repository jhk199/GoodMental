package com.example.goodmental.ui.summoner_info

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.goodmental.R
import com.example.goodmental.FollowedSummsActivity
import com.squareup.picasso.Picasso

class SummonerListAdapter : ListAdapter<Summoner, SummonerListAdapter.SummonerViewHolder>(SummonerComparator()) {

    class SummonerComparator: DiffUtil.ItemCallback<Summoner>() {
        override fun areItemsTheSame(oldItem: Summoner, newItem: Summoner): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Summoner, newItem: Summoner): Boolean {
            return oldItem.name == newItem.name
        }

    }
    class SummonerViewHolder(private val view : View) : RecyclerView.ViewHolder(view) {
        val textViewName : TextView = view.findViewById(R.id.textView_followedSummName)
        val textViewRegion : TextView = view.findViewById(R.id.textView_followedSummRegion)
        val imageViewSumm : ImageView = view.findViewById(R.id.imageView_followedSumm)

        fun bindText(text : String?, textView: TextView) {
            textView.text = text
        }


        companion object {
            fun create (parent: ViewGroup) : SummonerViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_summoner, parent, false)
                return SummonerViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummonerViewHolder {
        return SummonerViewHolder.create(parent)
    }

    override fun submitList(list: List<Summoner>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    override fun onBindViewHolder(holder: SummonerViewHolder, position: Int) {
        val currentSummoner = getItem(position)
        holder.bindText(currentSummoner.name, holder.textViewName)
        holder.bindText(currentSummoner.region, holder.textViewRegion)
        Picasso.get().load(currentSummoner.pictureURL).into(holder.imageViewSumm)

        holder.itemView.setOnClickListener {
            val currentContext = holder.itemView.context
            val intent = Intent(currentContext, FollowedSummsActivity::class.java )
            val arrayList = arrayListOf(currentSummoner.name, currentSummoner.pictureURL, currentSummoner.region, currentSummoner.matchLink)
            intent.putExtra("info", arrayList)
            currentContext.startActivity(intent)
        }
    }



}