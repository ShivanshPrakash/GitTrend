package com.example.gittrend.datasources

import com.example.gittrend.database.RepoDao
import com.example.gittrend.database.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by Shivansh ON 03/09/20.
 */
class LocalDataSource(private val repoDao: RepoDao) {
    fun getCartListLiveData() = repoDao.getRepoLiveData()

    suspend fun addRepoToDatabase(repository: Repository) = withContext(Dispatchers.IO) {
        repoDao.insertRepository(repository)
    }

    suspend fun clearDatabase() = withContext(Dispatchers.IO) { repoDao.clearDatabase() }
}