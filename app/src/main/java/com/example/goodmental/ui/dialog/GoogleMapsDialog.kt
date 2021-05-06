package com.example.goodmental.ui.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.goodmental.R
import com.example.goodmental.extensions.httpCallQuote
import com.example.goodmental.extensions.httpCallUrl
import com.example.goodmental.ui.summoner_info.SummonerListAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class GoogleMapsDialog : DialogFragment() {
    private lateinit var quote : TextView
    private lateinit var author : TextView
    private lateinit var loading : ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        val view = inflater.inflate(R.layout.dialog_fragment, container, false)
        quote = view.findViewById(R.id.textView_quote)
        author = view.findViewById(R.id.textView_author)
        loading = view.findViewById(R.id.progressBar_dialog)
        loading.visibility = View.VISIBLE
        return view
    }

    override fun onStart() {
        super.onStart()
        GlobalScope.launch {
            pickQuote()
        }
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private suspend fun pickQuote() {
        val url = "https://zenquotes.io/api/random/aacbb7f228f3116fcefd4e5751f160978088d3bb"
        val call = httpCallQuote(url)
        Log.e("call", call.toString())
        val quote1 = call?.get(0) as JSONObject
        val quote2 = quote1.getString("q")
        val author2 = quote1.getString("a")
        activity?.runOnUiThread() {
            run {
                loading.visibility = View.INVISIBLE
                quote.text = "\"" + quote2 + "\""
                author.text = author2
            }
        }

    }
}