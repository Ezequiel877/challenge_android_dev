package com.uala.challengeandroid.data

import com.uala.challengeandroid.model.City
import javax.inject.Inject


class CityRepositoryImpl @Inject constructor(
    private val api: CityApiService) : CityRepository {
    override suspend fun getCities(): List<City> = api.getCities()
}
