package com.example.goodmental

import android.os.Bundle
import android.os.PersistableBundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.goodmental.extensions.getDateTime
import com.example.goodmental.extensions.getDateTimeLong
import com.example.goodmental.extensions.getPatch
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.util.*

class AddGoalActivity : AppCompatActivity() {

    private lateinit var goalTitle : EditText
    private lateinit var radioButtonPersonal: RadioButton
    private lateinit var radioButtonMicro: RadioButton
    private lateinit var radioButtonMacro: RadioButton
    private lateinit var goalContent : EditText
    private lateinit var saveButton: Button
    private lateinit var errorText : TextView
    private lateinit var radioUrl : String
    private lateinit var patch : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addgoal)
        goalTitle = findViewById(R.id.editText_title)
        radioButtonPersonal = findViewById(R.id.radioButton_personal)
        radioButtonMicro = findViewById(R.id.radioButton_micro)
        radioButtonMacro = findViewById(R.id.radioButton_macro)
        goalContent = findViewById(R.id.editTextTextMultiLine3)
        errorText = findViewById(R.id.textView_errorAddGoal)
        saveButton = findViewById(R.id.button_goalSubmit)
        val db = Firebase.firestore
        val journal = db.collection("summoner")
            .document("App User")
            .collection("journal")
        saveButton.setOnClickListener {
            if(TextUtils.isEmpty(goalTitle.text) || TextUtils.isEmpty(goalContent.text)) {
                errorText.text = "Please fill in all fields to continue"
            }
            if(!radioButtonMacro.isChecked && !radioButtonMicro.isChecked && !radioButtonPersonal.isChecked) {
                errorText.text = "Please select a goal type"
            }
            else {
                    try {
                        val name = goalTitle.text.toString()
                        val content = goalContent.text.toString()
                        val timestamp = System.currentTimeMillis()
                        val date = getDateTimeLong(timestamp)
                        val userGoal = hashMapOf(
                            "name" to name,
                            "content" to content,
                            "goalType" to radioUrl,
                            "unixTime" to timestamp,
                            "date" to date)
                        Log.e("data", userGoal.toString())
                        db.collection("summoner").document("App User").collection("journal").document()
                            .set(userGoal)
                        finish()
                    } catch (e: IllegalArgumentException) {
                        Log.e("Error", "error")
                    }

            }
        }
    }

    fun onRadioButtonClicked(view : View) {
        val BASE_URL = "https://ddragon.leagueoflegends.com/cdn/11.9.1/img/item"
        if(view is RadioButton) {
            val checked = view.isChecked
            when(view.id) {
                R.id.radioButton_personal ->
                    if(checked) {
                        radioUrl = "$BASE_URL/1054.png"
                    }
                R.id.radioButton_micro ->
                    if(checked) {
                        radioUrl = "$BASE_URL/1055.png"
                    }
                R.id.radioButton_macro ->
                    if(checked) {
                        radioUrl = "$BASE_URL/1082.png"
                    }
            }
        }
    }


}