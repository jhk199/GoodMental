package com.example.goodmental.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import kotlinx.coroutines.*
import com.example.goodmental.R
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.serialization.json.buildJsonArray
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class SummonerFragment : Fragment() {

    private lateinit var summonerInfo : SummonerInformation
    private val summonerViewModel: SummonerViewModel by activityViewModels()
    var region = arrayOf("NA", "EUW" , "EUN" , "KR" , "JPN" , "OCE")
    var regionArray = arrayOf("na1", "euw1", "eun1", "kr",  "jp1",  "oce1")
    private var BASE_URL = "https://p9hog00lo9.execute-api.us-west-1.amazonaws.com/gmapi/"
    private var DRAGON_URL = "https://ddragon.leagueoflegends.com"
    private lateinit var summonerName : EditText
    private lateinit var regionSpinner : Spinner
    private lateinit var buttonSubmit : Button

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
//        summonerViewModel =
//                ViewModelProvider(this).get(SummonerViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_summoner, container, false)
        val textView: TextView = view.findViewById(R.id.text_home)
        buttonSubmit = view.findViewById(R.id.button_summoner)
        regionSpinner = view.findViewById(R.id.spinner_region)
        summonerName = view.findViewById(R.id.editText_SummonerName)
        summonerInfo = SummonerInformation("n/a")
        val selectSpinner =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, region)
        selectSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        regionSpinner.adapter = selectSpinner


        buttonSubmit.setOnClickListener {
            GlobalScope.launch {
                try {
                    //Log.d("patch", getPatch())
                    val summUrl = "summoner"
                    val matchUrl = "match"
                    val summ = httpCall(summUrl, summonerName.text.toString())
                    //Log.d("Summoner", summ.toString())
                    val match = httpCall(matchUrl, summ)
                    //Log.d("match", summ.toString())
                    summonerInfo = SummonerInformation(match.toString())
                    summonerViewModel.setInformation(summonerInfo)
                } catch (e: Exception) {
                    summonerInfo = SummonerInformation("Summoner doesn't exist")
                    summonerViewModel.setInformation(summonerInfo)
                }
            }
        }

        summonerViewModel.getInformation().observe(viewLifecycleOwner, Observer {
            summonerInfo -> summonerInfo.let {
                textView.text = it.http
        }
        })
        return view
    }

    private suspend fun getPatch() : String/*HashMap<String, Int>*/ {
        val client = HttpClient()
        val patchResponse : ByteArray = client.get("$DRAGON_URL/api/versions.json")
        val jsonPatch  = JSONArray(String(patchResponse))
        val patch = jsonPatch.get(0).toString()
        val champResponse : String = client.get("$DRAGON_URL/cdn/$patch/data/en_US/champion.json")
        val jsonChamp = JSONObject(champResponse).getJSONObject("data")
//        for (i in 0 until jsonChamp.length()) {
//            jsonChamp
//        }
        return jsonChamp.toString()
    }

    private suspend fun httpCall(url : String ?, endOfCall : String?) : String? {
        val request = "$BASE_URL$url/na1/$endOfCall"
        val client = HttpClient()
        val response : String = client.get(request)
        val json  = JSONObject(response)
        client.close()
        Log.d("response", json.toString())
        if(url == "summoner") {
            return json.getString("accountId")
        }
        else if (url == "match") {
            val jsonArray = json.getJSONArray("matches")
            val match1 = jsonArray.getJSONObject(0)
            Log.d("Matches 0", match1.toString())
            return match1.toString()
            //return getMatches(jsonArray).toString()
        }
        return null
    }

    private fun getMatches(jsonArray: JSONArray) : ArrayList<String> {
        val matchList = ArrayList<String>()
        for (i in 0 until jsonArray.length()) {
            val match = jsonArray.getJSONObject(i).getString("gameId")
            matchList.add(match)
        }
        return matchList
    }

}



