package com.example.gittrend.datasources

import com.example.gittrend.api.TrendingApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by Shivansh ON 03/09/20.
 * Data source responsible for fetching data from network.
 */
class RemoteDataSource(private val apiService: TrendingApiService) {

    suspend fun getRepositories() = withContext(Dispatchers.IO) {
        return@withContext apiService.getRepositories()
    }

}