package com.example.goodmental.ui.journal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodmental.extensions.FirebaseProfileService
import com.example.goodmental.ui.journal_info.Goal
import com.example.goodmental.ui.summoner_info.Summoner
import kotlinx.coroutines.launch

class JournalViewModel : ViewModel() {

    private val _goals = MutableLiveData<List<Goal>>()
    val goals : LiveData<List<Goal>> = _goals

    fun updateAll() = viewModelScope.launch {
        _goals.value = FirebaseProfileService.getGoals()
    }
}