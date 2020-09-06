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

    val repoListLiveData = appRepository.getRepoListLiveData()
    val noInternetLiveData = MutableLiveData(false)

    // Variables to preserve activity state info
    val repoList = mutableListOf<Repository>()
    var expandedLayoutPosition: Int? = null

    /**
     * Flag to check whether the observer triggered in [MainActivity]
     * observing [repoListLiveData] is caused due to screen rotation.
     */
    var onScreenRotation = false

    // Reference to last job that was started to fetch data from api
    private var pendingJob: Job? = null


    /**
     * Cancels [pendingJob] and creates a new one responsible for
     * fetching data from api and resetting the database with it.
     */
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

    /**
     * Checks whether the data stored in database is older than [CACHE_EXPIRY_DURATION]
     */
    fun isCacheExpired(): Boolean {
        val lastUpdateTime = appRepository.getCacheTime()
        val minutesPassed =
            ((Calendar.getInstance().time.time - lastUpdateTime) / 1000) / 60
        return minutesPassed > CACHE_EXPIRY_DURATION
    }


    /**
     * Sort current list of repositories by number of stars in descending order and
     * updates [expandedLayoutPosition]
     */
    fun sortRepoListByStars() {
        repoList.sortBy { it.stars }
        repoList.reverse()
        for ((index, repo) in repoList.withIndex()) {
            if (repo.isExpanded) expandedLayoutPosition = index
        }
    }

    /**
     * Sort current list of repositories by name and updates [expandedLayoutPosition]
     */
    fun sortRepoListByName() {
        repoList.sortBy { it.name } // lexicographic (or Alphabetical?)
        for ((index, repo) in repoList.withIndex()) {
            if (repo.isExpanded) expandedLayoutPosition = index
        }
    }
}