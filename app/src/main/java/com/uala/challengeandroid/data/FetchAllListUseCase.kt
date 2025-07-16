package com.uala.challengeandroid.data

import com.uala.challengeandroid.model.City
import com.uala.challengeandroid.model.CityEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

open class FetchCityUseCase @Inject constructor(private val cityRepository: CityRepository) {
    operator fun invoke(): Flow<List<City>> = cityRepository.cities
}