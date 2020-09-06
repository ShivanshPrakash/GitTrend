package com.example.gittrend.datasources

import com.example.gittrend.database.RepoDao
import com.example.gittrend.database.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by Shivansh ON 03/09/20.
 * Data source responsible for maintaining local database.
 */
class LocalDataSource(private val repoDao: RepoDao) {
    fun getCartListLiveData() = repoDao.getRepoLiveData()

    suspend fun addReposToDatabase(repositoryList: List<Repository>) = withContext(Dispatchers.IO) {
        repoDao.insertRepositories(repositoryList)
    }

    suspend fun clearDatabase() = withContext(Dispatchers.IO) { repoDao.clearDatabase() }
}