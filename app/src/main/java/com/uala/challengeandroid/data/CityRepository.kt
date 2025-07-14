package com.uala.challengeandroid.data

import com.uala.challengeandroid.model.City
import com.uala.challengeandroid.model.toEntity
import com.uala.challengeandroid.utils.toDomainList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

open class CityRepository @Inject constructor(
    val roomDataSource: CityDataLocal, private val remoteDataSource: CityDataRemote
) {
    val cities: Flow<List<City>>
        get() = roomDataSource.cities.onEach { list ->
            if (list.isEmpty()) {
                val fetch = remoteDataSource.getAllCities()
                roomDataSource.saveCities(fetch)
            }
        }

    suspend fun getFavorite(): Flow<List<City>> = roomDataSource.getFavorite().map { it.toDomainList() }
    suspend fun setFavorite(id: Long)= roomDataSource.toggleFavorite(id)
}