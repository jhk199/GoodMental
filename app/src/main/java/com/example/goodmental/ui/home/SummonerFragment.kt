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
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.goodmental.LoginActivity
import kotlinx.coroutines.*
import com.example.goodmental.R
import com.example.goodmental.extensions.getPatch
import com.example.goodmental.ui.match_info.Match
import com.example.goodmental.ui.match_info.MatchListAdapter
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import io.ktor.client.*
import io.ktor.client.request.*
import org.json.JSONArray


class SummonerFragment : Fragment() {


    private lateinit var patch : String
    private lateinit var matchList : ArrayList<Match>
    private lateinit var buttonDelete : Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var playerName: TextView
    private lateinit var playerImage : ImageView
    private var DRAGON_URL = "https://ddragon.leagueoflegends.com"

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_summoner, container, false)
        matchList = ArrayList()
        recyclerView = view.findViewById(R.id.recyclerView_summoner)
        playerImage = view.findViewById(R.id.imageView_playerIcon)
        playerName = view.findViewById(R.id.textView_name)
        buttonDelete = view.findViewById(R.id.button_delete)
        val sharedPreferences = this.activity?.getSharedPreferences("Pref", Context.MODE_PRIVATE)
        val patchTest = sharedPreferences?.getString("patch", "null")
        val iconTest = sharedPreferences?.getString("icon", "null")
        val db = Firebase.firestore
        val login = db.collection("summoner").document("App User")

        GlobalScope.launch {
            login.get().addOnSuccessListener { document ->
                // Log.e("Doc datat", document.data?.get("name") as String)
                playerName.text = document.data?.get("name") as String
            }
            patch = getPatch()
            login.collection("matches").orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val name : String = document.get("champion") as String
                        val url  = "$DRAGON_URL/cdn/$patch/img/champion/$name.png"
                        val date : String = document.get("timestamp") as String
                        val matchObject = Match(name, url, date)
                        matchList.add(matchObject)
                    }
                    // Log.e("MatchList", matchList.toString())
                    recyclerView.adapter = MatchListAdapter(matchList)
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    recyclerView.setHasFixedSize(true)
                }
                .addOnFailureListener { exception ->
                    Log.d("id", "Error getting documents: ", exception)
                }
        }
        Picasso.get().load("$DRAGON_URL/cdn/$patchTest/img/profileicon/$iconTest.png").into(playerImage)

        buttonDelete.setOnClickListener{
            matchList.clear()
            login.update("icon", "", "name", "", "region", "")
            login.collection("matches")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if(document.id != "template") {
                           login.collection("matches").document(document.id).delete()
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("id", "Error getting documents: ", exception)
                }
            login.collection("followedSumms")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if(document.id != "template") {
                            login.collection("followedSumms").document(document.id).delete()
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("id", "Error getting documents: ", exception)
                }
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }
        return view
    }
}



