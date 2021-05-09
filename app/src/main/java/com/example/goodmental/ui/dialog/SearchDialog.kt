package com.example.goodmental.ui.dialog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.goodmental.R

class SearchDialog : DialogFragment() {
    private lateinit var enter : TextView
    private lateinit var query : EditText
    private lateinit var buttonGo: Button
    private lateinit var buttonClose : Button
    private val baseUrl = "https://www.youtube.com/results?search_query="


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        val view = inflater.inflate(R.layout.dialog_fragment_search, container, false)
        enter = view.findViewById(R.id.dialog_search)
        query = view.findViewById(R.id.editText_search)
        buttonGo = view.findViewById(R.id.button_searchGo)
        buttonClose = view.findViewById(R.id.button_searchClose)

        buttonGo.setOnClickListener {
            if(query.text.isNotEmpty()) {
                launchBrowser(query.text.toString())
            }
            else {
                Toast.makeText(context, "Please enter something to search for!", Toast.LENGTH_LONG).show()
            }
        }
        buttonClose.setOnClickListener {
            dialog?.dismiss()
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun launchBrowser(searchUnformatted : String) {
        val search = searchUnformatted.replace("\\s+".toRegex(), " ")
        search.replace("\\s".toRegex(), "+")
        val queryURL : Uri = Uri.parse("${baseUrl}${search}")
        val intent = Intent(Intent.ACTION_VIEW, queryURL)
        startActivity(intent)
    }



}