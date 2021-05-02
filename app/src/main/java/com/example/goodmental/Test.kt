package com.example.goodmental

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Test : AppCompatActivity() {

    private lateinit var button : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Firebase.firestore
        setContentView(R.layout.activity_start)
        button = findViewById(R.id.button)
//        val sharedPref : SharedPreferences = getSharedPreferences("test", MODE_PRIVATE)
        button.setOnClickListener {
//            val editor = sharedPref.edit()
//            editor.putBoolean("test", true)
//            editor.apply()
            val user = hashMapOf(
                "name" to "",
                "region" to ""
            )
            db.collection("summoner").document("App User")
                .set(user)
                .addOnSuccessListener { documentReference ->
                    Log.d("Success", "DocumentSnapshot added with ID: $documentReference")
                }
                .addOnFailureListener { e ->
                    Log.w("Fail", "Error adding document", e)
                }
            finish()
        }
    }
}