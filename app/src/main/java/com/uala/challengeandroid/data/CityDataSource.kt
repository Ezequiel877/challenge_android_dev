package com.uala.challengeandroid.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.uala.challengeandroid.model.City
import com.uala.challengeandroid.model.CityEntity
import kotlinx.coroutines.flow.Flow

interface CityDataRemote {
        suspend fun getAllCities():List<City>

}
interface CityDataLocal {
        val cities:Flow<List<City>>
        suspend fun saveCities(cities: List<City>)
        suspend fun toggleFavorite(id: Long)
        fun getFavorite():Flow<List<CityEntity>>
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
        fun getAll(): Flow<List<CityEntity>>

        @Query("SELECT * FROM cities WHERE name LIKE :query OR country LIKE :query ORDER BY name")
        fun search(query: String): Flow<List<CityEntity>>

        @Query("SELECT * FROM cities WHERE id = :id")
        suspend fun getById(id: Long): CityEntity?
        @Query("UPDATE cities SET isFavorite = :isFav WHERE id = :id")
        suspend fun toggleFavorite(id: Long, isFav: Boolean)
        @Query("SELECT * FROM cities WHERE isFavorite = 1")
        fun getFavorites(): Flow<List<CityEntity>>
}


