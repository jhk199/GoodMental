package com.example.goodmental.ui.followed_summs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FollowedViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is followed summoners Fragment"
    }
    val text: LiveData<String> = _text
}