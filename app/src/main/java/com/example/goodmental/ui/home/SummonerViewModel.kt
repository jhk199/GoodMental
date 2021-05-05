package com.example.goodmental.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodmental.extensions.FirebaseProfileService
import com.example.goodmental.ui.match_info.Match
import com.example.goodmental.ui.summoner_info.Summoner
import kotlinx.coroutines.launch

class SummonerViewModel : ViewModel() {


    private val _matches = MutableLiveData<List<Match>>()
    val matches: LiveData<List<Match>> = _matches
    private val _followedMatches = MutableLiveData<List<Match>>()
    val followedMatches: LiveData<List<Match>> = _followedMatches

    fun updateAll(id : String) = viewModelScope.launch {
        if(id == "") {
            _matches.value = FirebaseProfileService.getMatches()
        }
        else {
            _followedMatches.value = FirebaseProfileService.getFollowedMatches(id)
        }
    }
}


