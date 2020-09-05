package com.example.gittrend

import android.content.Context
import com.example.gittrend.datasources.LocalDataSource
import com.example.gittrend.datasources.RemoteDataSource
import java.util.*

/**
 * Created by Shivansh ON 03/09/20.
 */
class AppRepository(
    private val remoteSource: RemoteDataSource,
    private val localSource: LocalDataSource,
    private val appContext: Context
) {

    fun getRepoListLiveData() = localSource.getCartListLiveData()

    suspend fun refreshDatabaseFromApi() {
        val repositoryList = remoteSource.getRepositories()
        setCacheTime()
        localSource.clearDatabase()
        localSource.addReposToDatabase(repositoryList)
    }

    private fun setCacheTime() {
        val sharedPref = appContext.getSharedPreferences(APP_SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putLong(CACHE_SHARED_PREFERENCE, Calendar.getInstance().time.time)
            commit()
        }
    }

    fun getCacheTime() = appContext
            .getSharedPreferences(APP_SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE)
            .getLong(CACHE_SHARED_PREFERENCE, -1)
}