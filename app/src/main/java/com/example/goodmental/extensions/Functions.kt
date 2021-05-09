package com.example.goodmental.extensions

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log

/**
 * import com.example.goodmental.extensions.httpCall
 * import com.example.goodmental.extensions.getMatches
 * import com.example.goodmental.extensions.getDateTime
 * import com.example.goodmental.extensions.getChamp
 * import com.example.goodmental.extensions.stringToJsonObject
 * import com.example.goodmental.extensions.getPatch
 * import com.example.goodmental.extensions.stringToJsonArray
 * import com.example.goodmental.extensions.hideKeyboard
 */
const val DRAGON_URL = "https://ddragon.leagueoflegends.com"
suspend fun httpCall(BASE_URL : String?, url : String ?, region : String?, endOfCall : String?): String? {
    try {
        val request = "$BASE_URL$url/$region/$endOfCall"
        val client = HttpClient()
        val response : String = client.get(request)
        val json  = JSONObject(response)
        client.close()
        return if (url == "match") {
            val jsonArray = json.getJSONArray("matches")
            jsonArray.toString()
        }
        else  {
            json.toString()
        }
    } catch (e: java.lang.Exception) {
        return null
    }
}

suspend fun httpCallUrl(url : String): String? {
    return try {
        val client = HttpClient()
        val response : String = client.get(url)
        val json  = JSONObject(response)
        client.close()
        val jsonArray = json.getJSONArray("matches")
        jsonArray.toString()
    } catch (e: java.lang.Exception) {
        null
    }
}

suspend fun httpCallQuote(url : String) : JSONArray? {
    return try {
        val client = HttpClient()
        val response : String = client.get(url)
        val json  = JSONArray(response)
        client.close()
        json
    } catch (e: java.lang.Exception) {
        null
    }
}

fun getWinLoss(json : String, summonerName : String) : String? {
    val jsonNew = stringToJsonObject(json)
    val jsonArrayIdentities = jsonNew.getJSONArray("participantIdentities")
    val jsonArrayParticipants = jsonNew.getJSONArray("participants")
    var participantId  = -1
    for ( i in 0 until jsonArrayIdentities.length()) {
        val jsonObject = jsonArrayIdentities[i] as JSONObject
        if(jsonObject.getJSONObject("player").getString("summonerName").equals(summonerName)) {
            participantId = i
            break
        }
    }
    return if(participantId == -1) null
    else {
        val jsonObject = jsonArrayParticipants[participantId] as JSONObject
        if(!jsonObject.getJSONObject("stats").getString("win").toBoolean()) {
            "L"
        } else "W"
    }
}

suspend fun refreshMatches(matchLink : String, name: String, region : String, patch : String, user : Boolean, login : DocumentReference) {
    val db = Firebase.firestore
    try {
        val matchCall = httpCallUrl(matchLink)
        val match = matchCall?.let { stringToJsonArray(it) }
        val BASE_URL = "https://p9hog00lo9.execute-api.us-west-1.amazonaws.com/gmapi/"
        login.get().addOnSuccessListener {
            val time = it.data?.get("latestGame") as Long
            Log.e("time", time.toString())
            if (match != null) {
                GlobalScope.launch {
                    for (i in 0 until 10) {
                        val champion = getChamp(match.getJSONObject(i).getString("champion"))
                        val gameID = match.getJSONObject(i).getString("gameId")
                        val lane = match.getJSONObject(i).getString("lane")
                        val timeUnix = match.getJSONObject(i).getString("timestamp")
                        val timestamp = getDateTime(timeUnix)
                        val timeUnixLong = timeUnix.toLong()
                        val icon = "$DRAGON_URL/cdn/$patch/img/champion/$champion.png"
                        val matchInfo = match.getJSONObject(i)?.getString("gameId")
                        val gameJson =
                            httpCall(BASE_URL, "matchinfo", regionToUrl(region), matchInfo)
                        val winLoss = getWinLoss(gameJson.toString(), name)
                        val userMatch = hashMapOf(
                            "champion" to champion,
                            "gameID" to gameID,
                            "lane" to lane,
                            "timestamp" to timestamp,
                            "icon" to icon,
                            "timeUnix" to timeUnixLong,
                            "winLoss" to winLoss
                        )
                        if (time < timeUnixLong) {
                            if (i == 0) {
                                login.update("latestGame", timeUnixLong)
                            }
                            login.collection("matches").add(userMatch)
                        } else {
                            break
                        }
                    }
                    db.collection("summoner")
                        .document("App User")
                        .get()
                        .addOnSuccessListener { document ->
                            val count = document.data?.get("count").toString().toInt()
                            Log.e("Count", count.toString())
                            if(count > 10) {
                                val countDif = count - 10
                                Log.e("countdif", countDif.toString())
                                db.collection("summoner").document("App User")
                                    .collection("matches")
                                    .orderBy("gameID", Query.Direction.ASCENDING)
                                    .limit((countDif).toLong())
                                    .get().addOnSuccessListener { result ->
                                        val count2 = countDif
                                        for (doc in result) {
                                            if (doc.id != "template") {
                                                Log.e("data", doc.data.toString())
                                                login.collection("matches").document(doc.id)
                                                    .delete()
                                                val countNew = count - 1
                                            }
                                        }
                                        login.update("count", 10)
                                    }
                            }
                    }
                }
            }
        }
    } catch (e: Exception) {
        Log.e("ERROR", "ERROR")
    }
}

fun logout() {
    val db = Firebase.firestore
    val login = db.collection("summoner").document("App User")
    login.update("icon", "", "name", "", "region", "", "matchUrl", "", "latestGame", 0, "count", 0)
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
                login.collection("followedSumms").document(document.id).collection("matches").get().addOnSuccessListener { result2 ->
                    for(doc2 in result2) {
                        login.collection("followedSumms").document(document.id).collection("matches").document(doc2.id).delete()
                    }
                }
                if(document.id != "template") {
                    login.collection("followedSumms").document(document.id).delete()
                }
            }
        }
        .addOnFailureListener { exception ->
            Log.d("id", "Error getting documents: ", exception)
        }
    login.collection("journal")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
                if(document.id != "template") {
                    login.collection("journal").document(document.id).delete()
                }
            }
        }
        .addOnFailureListener { exception ->
            Log.d("id", "Error getting documents: ", exception)
        }

}

fun completeJournalEntry(unixTime : Long) {
    val db = Firebase.firestore
    val login = db.collection("summoner").document("App User").collection("journal")
    login.get().addOnSuccessListener { result ->
        for (document in result) {
            if(document.data["unixTime"] == unixTime) {
                login.document(document.id).delete()
                break
            }
        }
    }
}


fun getDateTime(s: String): String? {
    return try {
        val sdf = SimpleDateFormat("E, MM/dd/yyyy : hh:mm aa", Locale.US)
        val netDate = Date(s.toLong() )
        sdf.format(netDate)
    } catch (e: Exception) {
        e.toString()
    }
}

fun getDateTimeLong(s : Long): String? {
    return try {
        val sdf = SimpleDateFormat("E, MM/dd/yyyy : hh:mm aa", Locale.US)
        val netDate = Date(s.toLong() )
        sdf.format(netDate)
    } catch (e: Exception) {
        e.toString()
    }
}

suspend fun getChamp(champion : String) : String {
    val client = HttpClient()
    var champName : String = ""
    val patch = getPatch()
    val champResponse : String = client.get("$DRAGON_URL/cdn/$patch/data/en_US/champion.json")
    val jsonChamp = JSONObject(champResponse).getJSONObject("data")
    val jsonArray = jsonChamp.toJSONArray(jsonChamp.names())
    for (i in 0 until jsonArray.length()) {
        if (jsonArray.getJSONObject(i).getString("key") == champion) {
            champName = jsonArray.getJSONObject(i).getString("id")
        }
    }
    return champName
}

fun stringToJsonObject(string : String) : JSONObject {
    return JSONObject(string)
}

fun stringToJsonArray(string : String) : JSONArray {
    return JSONArray(string)
}

fun toastError(text:String, context: Context) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}

suspend fun getPatch() : String {
    val client = HttpClient()
    val patchResponse: ByteArray = client.get("$DRAGON_URL/api/versions.json")
    val jsonPatch = JSONArray(String(patchResponse))
    return jsonPatch.get(0).toString()
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun regionToUrl(region: String) : String {
    return when(region) {
        "NA" -> "na1"
        "EUW" -> "euw1"
        "EUN" -> "eun1"
        "KR" -> "kr"
        "JPN" -> "jp1"
        "OCE" -> "oce1"
        else -> "error"
    }
}

