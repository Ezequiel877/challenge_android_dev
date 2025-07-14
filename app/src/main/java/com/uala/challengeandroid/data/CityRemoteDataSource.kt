package com.uala.challengeandroid.data

import com.uala.challengeandroid.model.City
import com.uala.challengeandroid.model.CityEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CityRemoteDataSource @Inject constructor(private val fetch:CityApiService) : CityDataRemote{

    override suspend fun getAllCities(): List<City> = fetch.getCities()

}