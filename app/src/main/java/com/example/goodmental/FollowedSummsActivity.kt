package com.example.goodmental

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.goodmental.extensions.getPatch
import com.example.goodmental.extensions.refreshMatches
import com.example.goodmental.ui.home.SummonerViewModel
import com.example.goodmental.ui.match_info.MatchListAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class FollowedSummsActivity  : AppCompatActivity() {

    private lateinit var buttonDelete : Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var playerName: TextView
    private lateinit var playerImage : ImageView
    private lateinit var swipeRefreshLayout : SwipeRefreshLayout
    private lateinit var summonerViewModel: SummonerViewModel
    private lateinit var patch : String
    private lateinit var matchLink : String

    private var DRAGON_URL = "https://ddragon.leagueoflegends.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_summoner)
        summonerViewModel = ViewModelProvider(this).get(SummonerViewModel::class.java)
        recyclerView = findViewById(R.id.recyclerView_summoner)
        playerImage = findViewById(R.id.imageView_playerIcon)
        playerName = findViewById(R.id.textView_name)
        buttonDelete = findViewById(R.id.button_summonerDelete)
        swipeRefreshLayout = findViewById(R.id.swipeToRefresh_summoner)

        buttonDelete.text = "Delete"

        val intent = intent
        val array = intent.getStringArrayListExtra("info")
        val name = array?.get(0)
        val url = array?.get(1)
        val region = array?.get(2)
        matchLink = array?.get(3)!!
        val db = Firebase.firestore
        db.collection("summoner").document("App User").get().addOnSuccessListener {
            patch = it.data?.get("patch") as String
        }
        val nameFormatted = name?.filter { !it.isWhitespace() }?.toLowerCase(Locale.ROOT)!!
        val followedSumm = db
                .collection("summoner")
                .document("App User")
                .collection("followedSumms")
                .document(nameFormatted)
        val followedSummMatches = db
                .collection("summoner")
                .document("App User")
                .collection("followedSumms")
                .document(nameFormatted)
                .collection("matches")
        playerName.text = name
        Picasso.get().load(url).into(playerImage)
        recyclerView.apply {
            val adapter = MatchListAdapter()
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(context)
            summonerViewModel.updateAll(nameFormatted)
            summonerViewModel.followedMatches.observe(this@FollowedSummsActivity, Observer {
                adapter.submitList(it?.toMutableList())
            })
        }

        swipeRefreshLayout.setOnRefreshListener {
            GlobalScope.launch(Dispatchers.IO) {
                val login = db.collection("summoner")
                    .document("App User")
                    .collection("followedSumms")
                    .document(nameFormatted)
                refreshMatches(matchLink, playerName.text as String, region!!, patch, false, login)
                delay(1000)
                runOnUiThread {
                    recyclerView.apply {
                        val adapter = MatchListAdapter()
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(context)
                        summonerViewModel.updateAll(nameFormatted)
                        summonerViewModel.followedMatches.observe(this@FollowedSummsActivity, Observer {
                            adapter.submitList(it?.toMutableList())
                        })
                    }
                    swipeRefreshLayout.isRefreshing = false
                }

            }

        }

        buttonDelete.setOnClickListener {
            followedSummMatches.get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            followedSummMatches.document(document.id).delete()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("id", "Error getting documents: ", exception)
                    }
            followedSumm.delete()
            finish()
        }
    }
}