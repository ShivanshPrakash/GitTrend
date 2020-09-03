package com.example.gittrend.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Created by Shivansh ON 02/09/20.
 */
@Dao
interface RepoDao {
    @Query("SELECT * FROM repositories")
    fun getRepoLiveData(): LiveData<List<Repository>>

    @Query("DELETE FROM repositories")
    suspend fun clearDatabase()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepository(repository: Repository)
}