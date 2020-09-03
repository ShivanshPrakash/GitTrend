package com.example.gittrend

import android.content.Context
import androidx.room.Room
import com.example.gittrend.database.RepoDatabase
import com.example.gittrend.datasources.LocalDataSource
import com.example.gittrend.datasources.RemoteDataSource
import com.example.gittrend.utils.RetrofitGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

/**
 * Created by Shivansh ON 03/09/20.
 */
@Module
@InstallIn(ApplicationComponent::class)
object HiltModule {

    @Singleton
    @Provides
    fun provideRemoteDataSource(): RemoteDataSource {
        return RemoteDataSource(RetrofitGenerator.create(TrendingApiService::class.java) as TrendingApiService)
    }

    @Singleton
    @Provides
    fun provideLocalDataSource(database: RepoDatabase): LocalDataSource {
        return LocalDataSource(database.getRepoDao())
    }

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): RepoDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            RepoDatabase::class.java,
            "Repo.db"
        ).build()
    }
}

@Module
@InstallIn(ApplicationComponent::class)
object AppRepositoryModule {

    @Singleton
    @Provides
    fun provideAppRepository(
        remoteDataSource: RemoteDataSource,
        localDataSource: LocalDataSource,
        @ApplicationContext context: Context
    ): AppRepository {
        return AppRepository(remoteDataSource, localDataSource, context)
    }
}