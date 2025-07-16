package com.uala.challengeandroid.data

import com.uala.challengeandroid.model.City
import com.uala.challengeandroid.model.CityEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

open class SetFavoriteUseCase @Inject constructor(private val moviesRepository: CityRepository) {
    suspend fun setNewFavorite(id:Long) = moviesRepository.setFavorite(id)
    suspend fun getAllFavorite(): Flow<List<City>> = moviesRepository.getFavorite()
}