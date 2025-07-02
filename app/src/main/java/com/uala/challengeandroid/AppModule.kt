package com.uala.challengeandroid

import com.uala.challengeandroid.data.CityApiService
import com.uala.challengeandroid.data.CityRepository
import com.uala.challengeandroid.data.CityRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
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

    @Singleton
    @Provides
    fun provideCityRepository(api: CityApiService): CityRepository = CityRepositoryImpl(api)
}

