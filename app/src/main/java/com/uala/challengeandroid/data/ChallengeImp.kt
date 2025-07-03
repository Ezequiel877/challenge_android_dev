package com.uala.challengeandroid.data

import android.util.Log
import androidx.paging.PagingSource
import com.uala.challengeandroid.model.City
import com.uala.challengeandroid.model.CityEntity
import com.uala.challengeandroid.utils.toEntityList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class CityRepositoryImpl @Inject constructor(
    private val api: CityApiService, private val room:CityDao) : CityRepository {
    override suspend fun getCities(): List<CityEntity> = withContext(Dispatchers.IO) {
        val list = api.getCities()
        Log.d("TAGLIST", "getCities: ${list.size}")
        room.insertAll(list.toEntityList())
        room.getAll()
    }

}
