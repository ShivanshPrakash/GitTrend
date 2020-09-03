package com.example.gittrend.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Shivansh ON 02/09/20.
 */
@Entity(tableName = "repositories")
data class Repository(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "author") val author: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "language") val language: String?,
    @ColumnInfo(name = "language_color") val languageColor: String?,
    @ColumnInfo(name = "avatar") val avatar: String?,
    @ColumnInfo(name = "stars") val stars: Int,
    @ColumnInfo(name = "forks") val forks: Int
)
