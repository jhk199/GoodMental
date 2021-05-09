package com.example.goodmental

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.makeText
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.goodmental.ui.dialog.LossDialog
import com.example.goodmental.ui.dialog.QuoteDialog
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val db = Firebase.firestore
        val login = db.collection("summoner").document("App User")
        var lossCount = 0
        login.collection("matches").orderBy("timeUnix", Query.Direction.DESCENDING).limit(3).get().addOnSuccessListener { result ->
            for (document in result) {
                if(document.data["winLoss"] == "L") {
                    lossCount++
                }
            }
        }
        GlobalScope.launch {
            delay(1000)
            if(lossCount >= 3) {
                LossDialog().show(supportFragmentManager, "Loss Dialog")
            } else {
                QuoteDialog().show(supportFragmentManager, "Quote Dialog")
            }

        }

    }




}