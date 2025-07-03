package com.uala.challengeandroid.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.uala.challengeandroid.model.City
import com.uala.challengeandroid.model.CityEntity

interface CityRepository {
        suspend fun getCities(): List<CityEntity>
}

@Dao
interface CityDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertAll(cities: List<CityEntity>)

        @Update
        suspend fun update(city: CityEntity)

        @Delete
        suspend fun delete(city: CityEntity)

        @Query("SELECT * FROM cities ORDER BY name")
        fun getAll(): List<CityEntity>

        @Query("SELECT * FROM cities WHERE name LIKE :query OR country LIKE :query ORDER BY name")
        fun search(query: String): List<CityEntity>

        @Query("SELECT * FROM cities WHERE id = :id")
        suspend fun getById(id: Long): CityEntity?
}
