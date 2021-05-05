package com.example.goodmental.ui.followed_summs

import android.util.Log
import androidx.lifecycle.*
import com.example.goodmental.extensions.FirebaseProfileService
import com.example.goodmental.ui.summoner_info.Summoner
import kotlinx.coroutines.launch

class FollowedViewModel : ViewModel() {

    private val _summonerProfile = MutableLiveData<Summoner>()
    val summonerProfile : LiveData<Summoner> = _summonerProfile
    private val _followedSumms = MutableLiveData<List<Summoner>>()
    val followedSumms: LiveData<List<Summoner>> = _followedSumms

    fun updateAll() = viewModelScope.launch {
        _summonerProfile.value = FirebaseProfileService.getProfileData()
        _followedSumms.value = FirebaseProfileService.getFollowedSumms()
    }
}