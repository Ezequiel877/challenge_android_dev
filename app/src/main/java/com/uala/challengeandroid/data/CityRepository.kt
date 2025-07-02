package com.uala.challengeandroid.data

import com.uala.challengeandroid.model.City
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.http.GET

interface CityRepository {
        suspend fun getCities(): List<City>
}