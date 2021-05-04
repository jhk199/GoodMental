package com.example.goodmental.ui.match_info

import android.util.Log
import android.widget.ImageView
import com.example.goodmental.ui.summoner_info.Summoner
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import java.lang.Exception

data class Match (val matchID : String,
                  val name: String,
                  val pictureURL: String,
                  val date: String) {
    companion object {
        fun DocumentSnapshot.toMatch() : Match? {
            return try {
                val name = getString("champion")!!
                val pictureURL = getString("icon")!!
                val date = getString("timestamp")!!
                Match(id, name, pictureURL, date)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting user profile", e)
                FirebaseCrashlytics.getInstance().log("Error converting user profile")
                FirebaseCrashlytics.getInstance().setCustomKey("userId", id)
                FirebaseCrashlytics.getInstance().recordException(e)
                null
            }
        }
        private const val TAG = "Summoner"
    }
}