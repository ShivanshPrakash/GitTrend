package com.example.gittrend.api

import com.example.gittrend.database.Repository
import retrofit2.http.GET

/**
 * Created by Shivansh ON 02/09/20.
 */
interface TrendingApiService {
    @GET("repositories")
    suspend fun getRepositories(): List<Repository>
}