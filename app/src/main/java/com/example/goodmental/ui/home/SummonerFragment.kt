package com.example.goodmental.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.goodmental.LoginActivity
import kotlinx.coroutines.*
import com.example.goodmental.R
import com.example.goodmental.extensions.logout
import com.example.goodmental.extensions.refreshMatches
import com.example.goodmental.ui.match_info.MatchListAdapter
import com.example.goodmental.ui.summoner_info.SummonerListAdapter
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso



class SummonerFragment : Fragment() {

    private lateinit var buttonDelete : Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var playerName: TextView
    private lateinit var playerRegion: TextView
    private lateinit var playerImage : ImageView
    private lateinit var summonerViewModel: SummonerViewModel
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var DRAGON_URL = "https://ddragon.leagueoflegends.com"
    private lateinit var matchLink : String
    private lateinit var patch : String

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        summonerViewModel = ViewModelProvider(this).get(SummonerViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_summoner, container, false)
        recyclerView = view.findViewById(R.id.recyclerView_summoner)
        playerImage = view.findViewById(R.id.imageView_playerIcon)
        playerName = view.findViewById(R.id.textView_name)
        playerRegion = view.findViewById(R.id.textView_summonerRegion)
        buttonDelete = view.findViewById(R.id.button_summonerDelete)
        swipeRefreshLayout = view.findViewById(R.id.swipeToRefresh_summoner)
        val sharedPreferences = this.activity?.getSharedPreferences("Pref", Context.MODE_PRIVATE)
        val iconTest = sharedPreferences?.getString("icon", "null")
        val db = Firebase.firestore
        val login = db.collection("summoner").document("App User")

        login.get().addOnSuccessListener { document ->
            // Log.e("Doc datat", document.data?.get("name") as String)
            playerName.text = document.data?.get("name") as String
            playerRegion.text = document.data?.get("region") as String
            matchLink = document.data?.get("matchLink") as String
            patch = document.data?.get("patch") as String
            Picasso.get().load("$DRAGON_URL/cdn/$patch/img/profileicon/$iconTest.png").into(playerImage)
        }


        swipeRefreshLayout.setOnRefreshListener {
            GlobalScope.launch(Dispatchers.IO) {
                refreshMatches(matchLink, playerName.text as String, playerRegion.text as String, patch, true, login)
                swipeRefreshLayout.isRefreshing = false
                delay(1000)
                activity?.runOnUiThread {
                    recyclerView.apply {
                        val adapter = MatchListAdapter()
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(context)
                        summonerViewModel.updateAll("")
                        summonerViewModel.matches.observe(viewLifecycleOwner, Observer {
                            adapter.submitList(it?.toMutableList())
                        })
                    }
                }

            }

        }


        buttonDelete.setOnClickListener{
            logout()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            val adapter = MatchListAdapter()
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(context)
            summonerViewModel.updateAll("")
            summonerViewModel.matches.observe(viewLifecycleOwner, Observer {
                adapter.submitList(it?.toMutableList())
            })
        }
    }

    override fun onResume() {
        super.onResume()
        recyclerView.apply {
            val adapter = MatchListAdapter()
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(context)
            summonerViewModel.updateAll("")
            summonerViewModel.matches.observe(viewLifecycleOwner, Observer {
                adapter.submitList(it?.toMutableList())
            })
            recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
    }


}

//patch = getPatch()
//login.collection("matches").orderBy("timestamp", Query.Direction.DESCENDING)
//.get()
//.addOnSuccessListener { result ->
//    for (document in result) {
//        val name : String = document.get("champion") as String
//        val url  = "$DRAGON_URL/cdn/$patch/img/champion/$name.png"
//        val date : String = document.get("timestamp") as String
//        val matchObject = Match(name, url, date)
//        matchList.add(matchObject)
//    }
//    // Log.e("MatchList", matchList.toString())
//    recyclerView.adapter = MatchListAdapter(matchList)
//    recyclerView.layoutManager = LinearLayoutManager(context)
//    recyclerView.setHasFixedSize(true)
//}
//.addOnFailureListener { exception ->
//    Log.d("id", "Error getting documents: ", exception)
//}



