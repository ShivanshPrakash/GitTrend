package com.example.gittrend

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gittrend.database.Repository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.util.*

/**
 * Created by Shivansh ON 03/09/20.
 */
class AppViewModel @ViewModelInject constructor(private val appRepository: AppRepository) :
    ViewModel() {

    val noInternetLiveData = MutableLiveData(false)
    val repoListLiveData = appRepository.getRepoListLiveData()

    private var pendingJob: Job? = null
    var checkForEmptyDatabase = true
    var expandedLayoutPosition: Int? = null

    fun refreshFromApi() {
        pendingJob?.cancel()
        pendingJob = viewModelScope.launch {
            try {
                appRepository.refreshDatabaseFromApi()
            } catch (exception: Exception) {
                if (exception is UnknownHostException)
                    noInternetLiveData.value = true
            }
        }
    }

    fun isCacheExpired(): Boolean {
        val lastUpdateTime = appRepository.getCacheTime()

        val minutesPassed =
            ((Calendar.getInstance().time.time - lastUpdateTime) / 1000) / 60
        return minutesPassed > CACHE_EXPIRY_DURATION
    }

    fun sortRepoListByStars(repoList: MutableList<Repository>) {
        repoList.sortBy { it.stars }
        repoList.reverse()
        expandedLayoutPosition = null
    }

    // lexicographic (or Alphabetical?)
    fun sortRepoListByName(repoList: MutableList<Repository>) {
        repoList.sortBy { it.name }
        expandedLayoutPosition = null
    }
}