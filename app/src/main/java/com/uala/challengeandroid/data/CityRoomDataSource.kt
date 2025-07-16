package com.uala.challengeandroid.data

import com.uala.challengeandroid.model.City
import com.uala.challengeandroid.model.CityEntity
import com.uala.challengeandroid.utils.toDomainList
import com.uala.challengeandroid.utils.toEntityList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
internal class CitiesRoomDataSource @Inject constructor(private val citiesRoom: CityDao) : CityDataLocal{

    override val cities: Flow<List<City>> = citiesRoom.getAll().map { it.toDomainList() }
    override suspend fun saveCities(cities: List<City>) = citiesRoom.insertAll(cities.toEntityList())
    override suspend fun toggleFavorite(id: Long) {
        val cityList = citiesRoom.getAll().firstOrNull() ?: return
        val city = cityList.find { it.id == id } ?: return
        citiesRoom.toggleFavorite(id, !city.isFavorite)
    }

    override fun getFavorite(): Flow<List<CityEntity>> = citiesRoom.getFavorites()
}
