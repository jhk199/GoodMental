package com.example.goodmental

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.goodmental.extensions.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class LoginActivity : AppCompatActivity() {

    private var BASE_URL = "https://p9hog00lo9.execute-api.us-west-1.amazonaws.com/gmapi/"
    private var DRAGON_URL = "https://ddragon.leagueoflegends.com"
    private lateinit var summonerName : EditText
    private lateinit var regionSpinner : Spinner
    private lateinit var buttonSubmit : Button
    private lateinit var errorText : TextView
    private lateinit var loading : ProgressBar
    var region = arrayOf("NA", "EUW" , "EUN" , "KR" , "JPN" , "OCE")
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
                    Log.d("1", "1")
                    val name = summJson?.getString("name")
                    val accountId = summJson?.getString("accountId")
                    val icon = summJson?.getString("profileIconId")
                    val match = httpCall(BASE_URL, matchUrl, region, accountId)?.let { it1 -> stringToJsonArray(it1) }
                    if (match != null) {
                        for (i in 0 until 10) {
                            val userMatch = hashMapOf(
                                "champion" to getChamp(match.getJSONObject(i).getString("champion")),
                                "gameID" to match.getJSONObject(i).getString("gameId"),
                                "lane" to match.getJSONObject(i).getString("lane"),
                                "timestamp" to getDateTime(match.getJSONObject(i).getString("timestamp"))
                            )
                            db.collection("summoner").document("App User").collection("matches").document()
                                .set(userMatch)
                        }
                    }

                    db.collection("summoner").document("App User")
                        .update("name", name, "region", region, "icon", icon)
                        .addOnSuccessListener { documentReference ->
                            Log.d("Success", "DocumentSnapshot added with ID: $documentReference")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Fail", "Error adding document", e)
                        }
                    sharedPreferences.edit().putString("icon", icon).apply()
                    sharedPreferences.getString("icon", "iconFailed")?.let { Log.e("Icon", it) }

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
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, region)
        selectSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        regionSpinner.adapter = selectSpinner
    }
}