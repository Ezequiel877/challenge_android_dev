package com.uala.challengeandroid.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.uala.challengeandroid.data.CityDao
import com.uala.challengeandroid.model.CityEntity

@Database(entities = [CityEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
}
