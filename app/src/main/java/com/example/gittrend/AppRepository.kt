package com.example.gittrend

import android.content.Context
import com.example.gittrend.datasources.LocalDataSource
import com.example.gittrend.datasources.RemoteDataSource
import com.example.gittrend.utils.APP_SHARED_PREFERENCE_FILE
import com.example.gittrend.utils.CACHE_SHARED_PREFERENCE
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

    /**
     * Sets the value of the shared preference
     * [CACHE_SHARED_PREFERENCE] to current time.
     */
    private fun setCacheTime() {
        val sharedPref = appContext.getSharedPreferences(APP_SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putLong(CACHE_SHARED_PREFERENCE, Calendar.getInstance().time.time)
            commit()
        }
    }

    /**
     * @return value stored in shared preference [CACHE_SHARED_PREFERENCE]
     */
    fun getCacheTime() = appContext
            .getSharedPreferences(APP_SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE)
            .getLong(CACHE_SHARED_PREFERENCE, -1)
}