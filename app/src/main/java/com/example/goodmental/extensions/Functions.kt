package com.example.goodmental.extensions

import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import io.ktor.client.*
import io.ktor.client.request.*
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
suspend fun httpCall(BASE_URL : String?, url : String ?, region : String?, endOfCall : String?) : String? {
    try {
        val request = "$BASE_URL$url/$region/$endOfCall"
        //Log.d("Http Request", request)
        val client = HttpClient()
        val response : String = client.get(request)
        val json  = JSONObject(response)
        client.close()
        //Log.d("response", json.toString())
        return if (url == "match") {
            val jsonArray = json.getJSONArray("matches")
            jsonArray.toString()
        }
        else  {
            Log.d("2", "2")
            return json.toString()
        }
    } catch (e: java.lang.Exception) {
        return null
    }
}

private fun getMatches(jsonArray: JSONArray) : ArrayList<String> {
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


