package com.example.goodmental.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.goodmental.R

class LossDialog : DialogFragment() {
    private lateinit var loss : TextView
    private lateinit var message : TextView
    private lateinit var buttonYes: Button
    private lateinit var buttonNo : Button



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        val view = inflater.inflate(R.layout.dialog_fragment_loss, container, false)
        loss = view.findViewById(R.id.dialog_search)
        message = view.findViewById(R.id.textView_loss)
        buttonYes = view.findViewById(R.id.button_searchGo)
        buttonNo = view.findViewById(R.id.button_searchClose)

        buttonYes.setOnClickListener {
            launchNextDialog()
        }
        buttonNo.setOnClickListener {
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

    private fun launchNextDialog() {
        SearchDialog().show(childFragmentManager, "Search Fragment")
    }

}