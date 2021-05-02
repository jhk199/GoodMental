package com.example.goodmental.ui.followed_summs

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goodmental.R
import com.example.goodmental.extensions.*
import com.example.goodmental.ui.summoner_info.Summoner
import com.example.goodmental.ui.summoner_info.SummonerListAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.lang.IndexOutOfBoundsException
import kotlin.properties.Delegates

class FollowedFragment : Fragment() {

    private var BASE_URL = "https://p9hog00lo9.execute-api.us-west-1.amazonaws.com/gmapi/"
    private var DRAGON_URL = "https://ddragon.leagueoflegends.com"
    private lateinit var summonerName: EditText
    private lateinit var regionSpinner: Spinner
    private lateinit var buttonSubmit: Button
    private lateinit var buttonDelete: Button
    private lateinit var errorText: TextView
    private lateinit var loading: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var summonerList: ArrayList<Summoner>
    private var submitBoolean = true
    var region = arrayOf("NA", "EUW", "EUN", "KR", "JPN", "OCE")
    var regionArray = arrayOf("na1", "euw1", "eun1", "kr", "jp1", "oce1")

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_followed, container, false)
        summonerName = view.findViewById(R.id.editText_followedSummonerName)
        regionSpinner = view.findViewById(R.id.spinner_followedRegion)
        buttonSubmit = view.findViewById(R.id.button_followedSummoner)
        buttonDelete = view.findViewById(R.id.button_followedDelete)
        errorText = view.findViewById(R.id.textView_followedError)
        loading = view.findViewById(R.id.progressBar_followedLoad)
        // Picasso.get().isLoggingEnabled = true
        spinner()

        summonerList = ArrayList()
        recyclerView = view.findViewById(R.id.recyclerView_followed)
        val db = Firebase.firestore
        val followedSumms = db.collection("summoner").document("App User").collection("followedSumms")
        GlobalScope.launch {
            var patch = getPatch()
            followedSumms
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            if (document.id != "template") {
                                val name: String = document.get("name") as String
                                val icon = document.get("icon") as String
                                val url = "$DRAGON_URL/cdn/$patch/img/profileicon/$icon.png"
                                val summonerObject = Summoner(name, url, document.get("region") as String)
                                summonerList.add(summonerObject)
                            }
                        }
                        Log.e("MatchList", summonerList.toString())
                        recyclerView.adapter = SummonerListAdapter(summonerList)
                        recyclerView.layoutManager = LinearLayoutManager(context)
                        recyclerView.setHasFixedSize(true)
                    }
        }
        buttonDelete.setOnClickListener{
            summonerList.clear()
            followedSumms.get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            if(document.id != "template") {
                                followedSumms.document(document.id).delete()
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("id", "Error getting documents: ", exception)
                    }
        }
        buttonSubmit.setOnClickListener {
            view.hideKeyboard()
            if (TextUtils.isEmpty(summonerName.text)) {
                toastError("Please fill in all of the fields to continue")
            }
            else {
                loading.visibility = View.VISIBLE
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val region = regionArray[regionSpinner.selectedItemId.toInt()]
                        val matchUrl = "match"
                        Log.d("1", "1")
                        val summJson = httpCall(BASE_URL, "summoner", region, summonerName.text.toString())?.let {
                            stringToJsonObject(
                                    it
                            )
                        }
                        val name = summJson?.getString("name")
                        followedSumms.get().addOnSuccessListener { result ->
                            var exists by Delegates.notNull<Boolean>()
                            for (document in result) {
                                if (document.id == name) {
                                    exists = true
                                    break
                                }
                                exists = false
                            }
                        }
                        val accountId = summJson?.getString("accountId")
                        val icon = summJson?.getString("profileIconId")
                        val match = httpCall(BASE_URL, matchUrl, region, accountId)?.let { it1 -> stringToJsonArray(it1) }
                        if (name != null) {
                            val summoner = hashMapOf(
                                    "icon" to icon,
                                    "name" to name,
                                    "region" to region
                            )
                            followedSumms.document().set(summoner)
                        }
                        if (match != null) {
                            for (i in 0 until 10) {
                                val userMatch = hashMapOf(
                                        "champion" to getChamp(match.getJSONObject(i).getString("champion")),
                                        "gameID" to match.getJSONObject(i).getString("gameId"),
                                        "lane" to match.getJSONObject(i).getString("lane"),
                                        "timestamp" to getDateTime(match.getJSONObject(i).getString("timestamp"))
                                )
                                if (name != null) {
                                    followedSumms.document().collection("matches").add(userMatch)
                                }
                            }
                        }
                        submitBoolean = true
                        activity?.runOnUiThread() {
                            run {
                                recyclerView.adapter?.notifyDataSetChanged()
                                loading.visibility = View.INVISIBLE
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("EXCEPTION","")
                        withContext(Dispatchers.Main) {
                            context?.let { toastError("FUCK", it) }
                        }
                    }
                }
            }
        }


        return view
    }

    private fun spinner() {
        val selectSpinner =
                context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_dropdown_item, region) }
        selectSpinner?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        regionSpinner.adapter = selectSpinner
    }

    private fun toastError(text:String) {
        Toast.makeText(context,text, Toast.LENGTH_LONG).show()
    }


}



