package com.example.goodmental.extensions

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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

suspend fun refreshMatches(preferences: SharedPreferences, name: String, region : String) {
    val db = Firebase.firestore
    val login = db.collection("summoner").document("App User")
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
    try {
        val matchLink = preferences.getString("matchLink", "null")
        val matchCall = httpCallUrl(matchLink.toString())
        val match = matchCall?.let { stringToJsonArray(it) }
        val patch = preferences.getString("patch", "null")
        val BASE_URL = "https://p9hog00lo9.execute-api.us-west-1.amazonaws.com/gmapi/"
        if (match != null) {
            for (i in 0 until 10) {
                val champion = getChamp(match.getJSONObject(i).getString("champion"))
                val gameID =  match.getJSONObject(i).getString("gameId")
                val lane = match.getJSONObject(i).getString("lane")
                val timestamp = getDateTime(match.getJSONObject(i).getString("timestamp"))
                val icon = "$DRAGON_URL/cdn/$patch/img/champion/$champion.png"
                val matchInfo = match.getJSONObject(i)?.getString("gameId")
                val gameJson = httpCall(BASE_URL, "matchinfo", regionToUrl(region), matchInfo)
                val winLoss = getWinLoss(gameJson.toString(), name)
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

    } catch (e: Exception) {
        Log.e("ERROR", "ERROR")
    }
}

fun logout() {
    val db = Firebase.firestore
    val login = db.collection("summoner").document("App User")
    login.update("icon", "", "name", "", "region", "", "matchUrl", "")
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

}



fun getMatches(jsonArray: JSONArray) : ArrayList<String> {
    val matchList = ArrayList<String>()
    for (i in 0 until jsonArray.length()) {
        val match = jsonArray.getJSONObject(i).getString("gameId")
        matchList.add(match)
    }
    return matchList
}


fun getDateTime(s: String): String? {
    return try {
        val sdf = SimpleDateFormat("MM/dd/yyyy : hh:mm aa")
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

