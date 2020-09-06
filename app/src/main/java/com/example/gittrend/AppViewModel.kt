package com.example.gittrend

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gittrend.database.Repository
import com.example.gittrend.utils.CACHE_EXPIRY_DURATION
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

    var expandedLayoutPosition: Int? = null
    var  isForApiCall = false
    val repoList = mutableListOf<Repository>()

    private var pendingJob: Job? = null

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

    fun sortRepoListByStars() {
        var expandedRepo: Repository? = null
        expandedLayoutPosition?.let { expandedRepo = repoList[it] }
        repoList.sortBy { it.stars }
        repoList.reverse()
        expandedRepo.let { expandedLayoutPosition = repoList.indexOf(expandedRepo) }
    }

    // lexicographic (or Alphabetical?)
    fun sortRepoListByName() {
        var expandedRepo: Repository? = null
        expandedLayoutPosition?.let { expandedRepo = repoList[it] }
        repoList.sortBy { it.name }
        expandedLayoutPosition = null
        expandedRepo.let { expandedLayoutPosition = repoList.indexOf(expandedRepo) }
    }
}