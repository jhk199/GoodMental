package com.example.goodmental.ui.followed_summs

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.goodmental.R
import com.example.goodmental.extensions.*

import com.example.goodmental.ui.summoner_info.SummonerListAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*


class FollowedFragment : Fragment() {

    private var BASE_URL = "https://p9hog00lo9.execute-api.us-west-1.amazonaws.com/gmapi/"
    private var DRAGON_URL = "https://ddragon.leagueoflegends.com"
    private lateinit var summonerName: EditText
    private lateinit var regionSpinner: Spinner
    private lateinit var buttonSubmit: Button
    private lateinit var errorText: TextView
    private lateinit var loading: ProgressBar
    private lateinit var recyclerView: RecyclerView

    private lateinit var followedViewModel: FollowedViewModel
    private var submitBoolean = true
    var regionDisplay = arrayOf("NA", "EUW", "EUN", "KR", "JPN", "OCE")
    var regionArray = arrayOf("na1", "euw1", "eun1", "kr", "jp1", "oce1")

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        followedViewModel =
                ViewModelProvider(this).get(FollowedViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_followed, container, false)
        summonerName = view.findViewById(R.id.editText_followedSummonerName)
        regionSpinner = view.findViewById(R.id.spinner_followedRegion)
        buttonSubmit = view.findViewById(R.id.button_followedSummoner)
        errorText = view.findViewById(R.id.textView_followedError)
        loading = view.findViewById(R.id.progressBar_followedLoad)
        recyclerView = view.findViewById(R.id.recyclerView_followed)

        spinner()
        val db = Firebase.firestore
        val followedSumms = db.collection("summoner").document("App User").collection("followedSumms")





        buttonSubmit.setOnClickListener {
            view.hideKeyboard()
            if (TextUtils.isEmpty(summonerName.text)) {
                toastError("Please fill in all of the fields to continue")
            }
            else {
                loading.visibility = View.VISIBLE
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val patch = getPatch()
                        val region = regionSpinner.selectedItemId.toInt()
                        val matchUrl = "match"
                        val matchInfoUrl = "matchinfo"
                        Log.d("1", "1")
                        val summJson = httpCall(BASE_URL, "summoner", regionArray[region], summonerName.text.toString())?.let {
                            stringToJsonObject(
                                    it
                            )
                        }
                        val name = summJson?.getString("name")
                        val accountId = summJson?.getString("accountId")
                        val icon = summJson?.getString("profileIconId")
                        val match = httpCall(BASE_URL, matchUrl, regionArray[region], accountId)?.let { it1 -> stringToJsonArray(it1) }
                        if (name != null) {
                            val summoner = hashMapOf(
                                    "icon" to "$DRAGON_URL/cdn/$patch/img/profileicon/$icon.png",
                                    "name" to name,
                                    "region" to regionDisplay[regionSpinner.selectedItemId.toInt()]
                            )
                            followedSumms.document(name).set(summoner)
                        }
                        if (match != null) {
                            for (i in 0 until 10) {
                                val champion = getChamp(match.getJSONObject(i).getString("champion"))
                                val gameID =  match.getJSONObject(i).getString("gameId")
                                val lane = match.getJSONObject(i).getString("lane")
                                val timestamp = getDateTime(match.getJSONObject(i).getString("timestamp"))
                                val icon = "$DRAGON_URL/cdn/$patch/img/champion/$champion.png"
                                val matchInfo = match.getJSONObject(i)?.getString("gameId")
                                val gameJson = httpCall(BASE_URL, matchInfoUrl, regionArray[region], matchInfo)
                                val winLoss = getWinLoss(gameJson.toString(), name!!)
                                val userMatch = hashMapOf(
                                        "champion" to champion,
                                        "gameID" to gameID,
                                        "lane" to lane,
                                        "timestamp" to timestamp,
                                        "icon" to icon,
                                        "winLoss" to winLoss
                                )
                                val summId = followedSumms.whereEqualTo("name", name).get()
                                followedSumms.document(name).collection("matches").add(userMatch)
                            }
                        }
                        submitBoolean = true
                        activity?.runOnUiThread() {
                            run {
                                val adapter = SummonerListAdapter()
                                followedViewModel.updateAll()
                                followedViewModel.followedSumms.observe(viewLifecycleOwner, Observer {
                                    adapter.submitList(it?.toMutableList())
                                })
                                loading.visibility = View.INVISIBLE
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("EXCEPTION","")
                        withContext(Dispatchers.Main) {
                            context?.let { toastError("Something went Wrong!", it) }
                        }
                    }
                }
            }
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        recyclerView.apply {
            val adapter = SummonerListAdapter()
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(context)
            followedViewModel.updateAll()
            followedViewModel.followedSumms.observe(viewLifecycleOwner, Observer {
                adapter.submitList(it?.toMutableList())

            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            val adapter = SummonerListAdapter()
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(context)
            followedViewModel.updateAll()
            followedViewModel.followedSumms.observe(viewLifecycleOwner, Observer {
                adapter.submitList(it?.toMutableList())
            })
        }
    }

    private fun spinner() {
        val selectSpinner =
                context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_dropdown_item, regionDisplay) }
        selectSpinner?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        regionSpinner.adapter = selectSpinner
    }

    private fun toastError(text:String) {
        Toast.makeText(context,text, Toast.LENGTH_LONG).show()
    }
}



