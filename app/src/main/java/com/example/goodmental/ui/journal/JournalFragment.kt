package com.example.goodmental.ui.journal

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goodmental.AddGoalActivity
import com.example.goodmental.FollowedSummsActivity
import com.example.goodmental.R
import com.example.goodmental.ui.journal_info.GoalListAdapter
import com.example.goodmental.ui.summoner_info.SummonerListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class JournalFragment : Fragment() {

    private lateinit var journalViewModel: JournalViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var floatingActionButton: FloatingActionButton

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        journalViewModel =
                ViewModelProvider(this).get(JournalViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_journal, container, false)
        floatingActionButton = view.findViewById(R.id.floatingActionButton_addGoal)
        recyclerView = view.findViewById(R.id.recyclerView_journal)


        floatingActionButton.setOnClickListener {
            val intent = Intent(context, AddGoalActivity::class.java )
            startActivity(intent)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            val adapter = GoalListAdapter()
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(context)

            journalViewModel.updateAll()
            journalViewModel.goals.observe(viewLifecycleOwner, Observer {
                adapter.submitList(it?.toMutableList())
            })

        }
    }

    override fun onResume() {
        super.onResume()
        GlobalScope.launch {
            delay(1000)
            activity?.runOnUiThread{
                recyclerView.apply {
                    val adapter = GoalListAdapter()
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    journalViewModel.updateAll()
                    journalViewModel.goals.observe(viewLifecycleOwner, Observer {
                        adapter.submitList(it?.toMutableList())
                    })
                    recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                }
            }
        }
    }
}