package com.example.gittrend.database

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Created by Shivansh ON 02/09/20.
 */
@Database(entities = [Repository::class], version = 1)
abstract class RepoDatabase : RoomDatabase() {
    abstract fun getRepoDao(): RepoDao
}