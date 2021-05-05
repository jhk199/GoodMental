package com.example.goodmental.extensions

import android.util.Log
import com.example.goodmental.ui.match_info.Match
import com.example.goodmental.ui.match_info.Match.Companion.toMatch
import com.example.goodmental.ui.summoner_info.Summoner
import com.example.goodmental.ui.summoner_info.Summoner.Companion.toSummoner
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.lang.Exception

object FirebaseProfileService {
    private const val TAG = "FirebaseProfileService"

    suspend fun getProfileData() : Summoner? {
        val db = FirebaseFirestore.getInstance()
        return try {
            db.collection("summoner").document("App User").get().await().toSummoner()
        } catch (e: Exception) {
            // .collection("followedSumms").document(summonerId)
            Log.e(TAG, "Error getting user details", e)
            FirebaseCrashlytics.getInstance().log("Error getting user details")
            FirebaseCrashlytics.getInstance().setCustomKey("user id", "jhk199")
            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }
    suspend fun getFollowedSumms() : List<Summoner> {
        val db = FirebaseFirestore.getInstance()
        return try {
            db.collection("summoner").document("App User").collection("followedSumms").get().await().documents.mapNotNull {
                it.toSummoner()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user friends", e)
            FirebaseCrashlytics.getInstance().log("Error getting user friends")
            FirebaseCrashlytics.getInstance().setCustomKey("user id", "jhk199")
            FirebaseCrashlytics.getInstance().recordException(e)
            emptyList()
        }
    }

    suspend fun getMatches() : List<Match> {
        val db = FirebaseFirestore.getInstance()
        return try {
            db.collection("summoner").document("App User")
                    .collection("matches")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()
                    .documents.mapNotNull {
                it.toMatch()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user friends", e)
            FirebaseCrashlytics.getInstance().log("Error getting user friends")
            FirebaseCrashlytics.getInstance().setCustomKey("user id", "jhk199")
            FirebaseCrashlytics.getInstance().recordException(e)
            emptyList()
        }
    }

    suspend fun getFollowedMatches(id: String) : List<Match> {
        val db = FirebaseFirestore.getInstance()
        return try {
            val help = db.collection("summoner").document("App User").collection("followedSumms").document(id)
                    .collection("matches")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()
                    .documents.mapNotNull {
                        it.toMatch()
                    }
            Log.e("help", help.toString())
            return help
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user friends", e)
            FirebaseCrashlytics.getInstance().log("Error getting user friends")
            FirebaseCrashlytics.getInstance().setCustomKey("user id", "jhk199")
            FirebaseCrashlytics.getInstance().recordException(e)
            emptyList()
        }
    }

}