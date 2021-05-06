package com.example.goodmental

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.goodmental.extensions.*
import com.example.goodmental.ui.dialog.GoogleMapsDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonArray
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.util.*


class LoginActivity : AppCompatActivity() {

    private var BASE_URL = "https://p9hog00lo9.execute-api.us-west-1.amazonaws.com/gmapi/"
    private var DRAGON_URL = "https://ddragon.leagueoflegends.com"
    private lateinit var summonerName : EditText
    private lateinit var regionSpinner : Spinner
    private lateinit var buttonSubmit : Button
    private lateinit var errorText : TextView
    private lateinit var loading : ProgressBar
    var regionDisplay = arrayOf("NA", "EUW" , "EUN" , "KR" , "JPN" , "OCE")
    var regionArray = arrayOf("na1", "euw1", "eun1", "kr",  "jp1",  "oce1")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val sharedPreferences = getSharedPreferences("Pref", Context.MODE_PRIVATE)
        val db = Firebase.firestore
        summonerName = findViewById(R.id.editText_followedSummonerName)
        regionSpinner = findViewById(R.id.spinner_followedRegion)
        errorText = findViewById(R.id.textView_followedError)
        buttonSubmit = findViewById(R.id.button_followedSummoner)
        loading = findViewById(R.id.progressBar_followedLoad)
        spinner()



        buttonSubmit.setOnClickListener { view ->
            errorText.visibility = View.INVISIBLE
            summonerName.visibility = View.INVISIBLE
            buttonSubmit.visibility = View.INVISIBLE
            regionSpinner.visibility = View.INVISIBLE
            loading.visibility = View.VISIBLE

            view.hideKeyboard()
            runBlocking {
                launch(Dispatchers.Default) {

                }
            }
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val patch = getPatch()
                    val region = regionSpinner.selectedItemId.toInt()
                    val matchUrl = "match"
                    val matchInfoUrl = "matchinfo"
                    val summJson = httpCall(BASE_URL, "summoner", regionArray[region], summonerName.text.toString())?.let {
                        stringToJsonObject(
                            it
                        )
                    }
                    val name = summJson?.getString("name")
                    val accountId = summJson?.getString("accountId")
                    val icon = summJson?.getString("profileIconId")
                    val regionCall = regionArray[region]
                    val matchLink = "$BASE_URL/$matchUrl/$regionCall/$accountId"
                    val matchCall = httpCall(BASE_URL, matchUrl, regionArray[region], accountId)
                    val match = matchCall?.let { stringToJsonArray(it) }
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
                            db.collection("summoner").document("App User").collection("matches").document()
                                .set(userMatch)
                        }
                    }

                    db.collection("summoner").document("App User")
                        .update("name", name, "region", regionDisplay[region], "icon", icon)
                        .addOnSuccessListener { documentReference ->
                            Log.d("Success", "DocumentSnapshot added with ID: $documentReference")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Fail", "Error adding document", e)
                        }
                    sharedPreferences.edit().putString("patch", patch).apply()
                    sharedPreferences.edit().putString("matchLink", matchLink).apply()
                    sharedPreferences.edit().putString("icon", icon).apply()

                    val intent = Intent(this@LoginActivity, SecondActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        toastError("FUCK", this@LoginActivity)
                    }

                    runOnUiThread {
                        run() {
                            errorText.visibility = View.VISIBLE
                            errorText.text = "PLEASE ENTER A VALID SUMMONER OR REGION"
                            summonerName.text.clear()

                            summonerName.visibility = View.VISIBLE
                            buttonSubmit.visibility = View.VISIBLE
                            regionSpinner.visibility = View.VISIBLE
                            loading.visibility = View.INVISIBLE
                        }
                    }

                    Log.e("ERROR", "ERROR")
                }
            }
        }
    }



    private fun spinner() {
        val selectSpinner =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, regionDisplay)
        selectSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        regionSpinner.adapter = selectSpinner
    }
}