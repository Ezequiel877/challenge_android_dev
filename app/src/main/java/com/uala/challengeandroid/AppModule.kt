package com.uala.challengeandroid

import android.content.Context
import androidx.room.Room
import com.uala.challengeandroid.data.CitiesRoomDataSource
import com.uala.challengeandroid.data.CityApiService
import com.uala.challengeandroid.data.CityDao
import com.uala.challengeandroid.data.CityDataLocal
import com.uala.challengeandroid.data.CityDataRemote
import com.uala.challengeandroid.data.CityRemoteDataSource
import com.uala.challengeandroid.data.local.AppDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideCityApiService(): CityApiService {
        return Retrofit.Builder()
            .baseUrl("https://gist.githubusercontent.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CityApiService::class.java)
    }


}
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext app: Context): AppDatabase =
        Room.databaseBuilder(app, AppDatabase::class.java, "app.db").build()

    @Provides
    fun provideCityDao(db: AppDatabase): CityDao = db.cityDao()
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class FrameworkMovieModule {

    @Binds
    abstract fun bindLocalDataSource(localDataSource: CitiesRoomDataSource): CityDataLocal

    @Binds
    abstract fun bindRemoteDataSource(remoteDataSource: CityRemoteDataSource): CityDataRemote

}