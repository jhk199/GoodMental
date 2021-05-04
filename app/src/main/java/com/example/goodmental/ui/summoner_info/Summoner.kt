package com.example.goodmental.ui.summoner_info

import android.content.ContentValues.TAG
import android.os.Parcelable
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.parcelize.Parcelize
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.auth.User
import java.lang.Exception
@Parcelize
data class Summoner (val summonerID: String, /* Doc ID is actually summ id */
                     val name: String,
                     val pictureURL: String,
                     val region: String) : Parcelable {

    companion object {
        fun DocumentSnapshot.toSummoner() : Summoner? {
            return try {
                val name = getString("name")!!
                val pictureURL = getString("icon")!!
                val region = getString("region")!!
                Summoner(id, name, pictureURL, region)
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