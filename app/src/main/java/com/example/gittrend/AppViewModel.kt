package com.example.gittrend

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.net.UnknownHostException

/**
 * Created by Shivansh ON 03/09/20.
 */
class AppViewModel @ViewModelInject constructor(private val appRepository: AppRepository) :
    ViewModel() {

    val noInternetLiveData = MutableLiveData(false)
    val repoListLiveData = appRepository.getRepoListLiveData()

    var checkForEmptyDatabase = true

    fun refreshFromApi() = viewModelScope.launch {
        try {
            appRepository.refreshDatabaseFromApi()
        } catch (exception: Exception) {
            if (exception is UnknownHostException)
                noInternetLiveData.value = true
        }
    }
}