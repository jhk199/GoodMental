package com.example.goodmental.ui.followed_summs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.goodmental.R

class FollowedFragment : Fragment() {

    private lateinit var followedViewModel: FollowedViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        followedViewModel =
                ViewModelProvider(this).get(FollowedViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_followed, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        followedViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}