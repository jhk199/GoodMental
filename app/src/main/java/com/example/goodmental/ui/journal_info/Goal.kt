package com.example.goodmental.ui.journal_info

import android.os.Parcelable
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize
import java.lang.Exception

@Parcelize
data class Goal (val summonerID: String, /* Doc ID is actually summ id */
                     val name: String,
                     val pictureURL: String,
                     val date: String,
                 val content : String, val unixTime : Long) : Parcelable {


    companion object {
        fun DocumentSnapshot.toGoal() : Goal? {
            return try {
                val name = getString("name")!!
                val pictureURL = getString("goalType")!!
                val date = getString("date")!!
                val content = getString("content")!!
                val unixTime = getLong("unixTime")!!
                Goal(id, name, pictureURL, date, content, unixTime)
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