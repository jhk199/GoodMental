package com.example.goodmental

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    // private lateinit var button :Button
    private var DRAGON_URL = "https://ddragon.leagueoflegends.com"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Firebase.firestore
        val login = db.collection("summoner").document("App User")

        // setContentView(R.layout.activity_start)
        GlobalScope.launch {
            val sharedPreferences = getSharedPreferences("Pref", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val patch = getPatch()
            editor.putString("patch", patch)
            editor.apply()
            login.get().addOnSuccessListener { document ->
                if (document != null) {
                    if(document.getString("patch") != patch) {
                        login.update("patch", patch)
                    }
                    if(document.getString("name") == "") {
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else {
                        val intent = Intent(this@MainActivity, SecondActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }


    }

    private suspend fun getPatch() : String/*HashMap<String, Int>*/ {
        val client = HttpClient()
        val patchResponse: ByteArray = client.get("$DRAGON_URL/api/versions.json")
        val jsonPatch = JSONArray(String(patchResponse))
        return jsonPatch.get(0).toString()
    }
}