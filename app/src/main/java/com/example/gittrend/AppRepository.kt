package com.example.gittrend

import android.content.Context
import com.example.gittrend.datasources.LocalDataSource
import com.example.gittrend.datasources.RemoteDataSource

/**
 * Created by Shivansh ON 03/09/20.
 */
class AppRepository(
    private val remoteSource: RemoteDataSource,
    private val localSource: LocalDataSource,
    private val appContext: Context
) {
    suspend fun getRepoListFromApi() = remoteSource.getRepositories()
}