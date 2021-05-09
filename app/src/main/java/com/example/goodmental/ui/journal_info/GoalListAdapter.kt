package com.example.goodmental.ui.journal_info

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.goodmental.R
import com.example.goodmental.ViewGoalActivity
import com.squareup.picasso.Picasso

class GoalListAdapter : ListAdapter<Goal, GoalListAdapter.GoalViewHolder>(GoalComparator()) {

    class GoalComparator: DiffUtil.ItemCallback<Goal>() {
        override fun areItemsTheSame(oldItem: Goal, newItem: Goal): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Goal, newItem: Goal): Boolean {
            return oldItem.name == newItem.name
        }

    }
    class GoalViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val textViewName : TextView = view.findViewById(R.id.textView_goalName)
        val textViewDate : TextView = view.findViewById(R.id.textView_goalDate)
        val imageViewGoal : ImageView = view.findViewById(R.id.imageView_goal)


        fun bindText(text : String?, textView: TextView) {
            textView.text = text
        }


        companion object {
            fun create (parent: ViewGroup) : GoalViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_goal, parent, false)
                return GoalViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        return GoalViewHolder.create(parent)
    }

    override fun submitList(list: List<Goal>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val currentGoal = getItem(position)
        holder.bindText(currentGoal.name, holder.textViewName)
        holder.bindText(currentGoal.date, holder.textViewDate)
        Picasso.get().load(currentGoal.pictureURL).into(holder.imageViewGoal)

        holder.itemView.setOnClickListener {
            val currentContext = holder.itemView.context
            val intent = Intent(currentContext, ViewGoalActivity::class.java )
            val arrayList = arrayListOf(currentGoal.name, currentGoal.pictureURL,
                currentGoal.date, currentGoal.content, currentGoal.unixTime.toString())
            Log.e("intent", arrayList.toString())
            intent.putExtra("info", arrayList)
            Log.e("intent", intent.toString())
            currentContext.startActivity(intent)
        }
    }
}