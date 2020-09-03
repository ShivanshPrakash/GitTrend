package com.example.gittrend

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * Created by Shivansh ON 03/09/20.
 */
class AppViewModel @ViewModelInject constructor(private val appRepository: AppRepository) :
    ViewModel() {

    val repoListLiveData = appRepository.getRepoListLiveData()

    fun refreshFromApi() = viewModelScope.launch { appRepository.refreshDatabaseFromApi() }
}