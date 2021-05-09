package com.example.goodmental

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.goodmental.extensions.completeJournalEntry
import com.squareup.picasso.Picasso
import kotlinx.coroutines.delay
import kotlin.properties.Delegates

class ViewGoalActivity  : AppCompatActivity() {

    private lateinit var goalTitle : TextView
    private lateinit var goalType : ImageView
    private lateinit var goalDate : TextView
    private lateinit var scrollView: LinearLayout
    private lateinit var contentText : TextView
    private lateinit var checkBox: CheckBox
    private var unixTimeLong by Delegates.notNull<Long>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewgoal)
        goalTitle = findViewById(R.id.textView_viewGoalName)
        goalDate = findViewById(R.id.textView_viewGoalDate)
        goalType = findViewById(R.id.imageView_goalview)
        scrollView = findViewById(R.id.scrollView_viewGoal)
        contentText = findViewById(R.id.textView_contentText)
        checkBox = findViewById(R.id.checkBox_viewGoal)

        val intent = intent
        val array = intent.getStringArrayListExtra("info")
        val name = array?.get(0)
        val goalUrl = array?.get(1)
        val date = array?.get(2)
        val content = array?.get(3)
        val unixTime = array?.get(4)
        unixTimeLong = unixTime.toString().toLong()
        goalTitle.text = name
        Picasso.get().load(goalUrl).into(goalType)
        goalDate.text = date
        contentText.text = content


    }

    fun onCheckboxClicked(view : View) {
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked
            when (view.id) {
                R.id.checkBox_viewGoal -> {
                    if (checked) {
                        completeJournalEntry(unixTimeLong)
                        Toast.makeText(this, "Goal Reached, congrats!", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
            }
        }
    }
}