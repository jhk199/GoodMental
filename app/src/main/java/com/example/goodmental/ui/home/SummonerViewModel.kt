package com.example.goodmental.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SummonerViewModel : ViewModel() {

    private val summonerInformation = MutableLiveData<SummonerInformation>()

    fun setInformation(info : SummonerInformation) {
        summonerInformation.postValue(info)
    }

    fun getInformation() : MutableLiveData<SummonerInformation> {
        return summonerInformation
    }


}