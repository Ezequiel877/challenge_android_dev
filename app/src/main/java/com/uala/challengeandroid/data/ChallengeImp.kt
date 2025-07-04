package com.uala.challengeandroid.data

import android.util.Log
import androidx.paging.PagingSource
import com.uala.challengeandroid.model.City
import com.uala.challengeandroid.model.CityEntity
import com.uala.challengeandroid.model.toEntity
import com.uala.challengeandroid.utils.toDomainList
import com.uala.challengeandroid.utils.toEntityList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class CityRepositoryImpl @Inject constructor(
    private val api: CityApiService, private val room: CityDao
) : CityRepository {
    override suspend fun saveCities() {
        val list = api.getCities()
        Log.d("TAGLIST", "saveCities: $list ")
        room.insertAll(list.toEntityList())
    }

    override suspend fun getAllCities(): List<CityEntity> = room.getAll()
    override suspend fun toggleFavorite(id: Long) {
        val current = room.getAll().firstOrNull { it.id == id } ?: return
        room.toggleFavorite(id, !current.isFavorite)
    }

    override suspend fun getFavorites(): List<CityEntity> = room.getFavorites()

}
