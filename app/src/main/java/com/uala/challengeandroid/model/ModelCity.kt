package com.uala.challengeandroid.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class City(
    val country: String,
    val name: String,
    val id: Long,
    val coord: Coord
)

data class Coord(
    val lon: Double,
    val lat: Double
)
@Entity(tableName = "cities")
data class CityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "country")
    val country: String,
    @ColumnInfo(name = "lat")
    val lat: Double,
    @ColumnInfo(name = "lon")
    val lon: Double
)
fun CityEntity.toDomain(): City = City(
    country = country,
    name = name,
    id = id,
    coord = Coord(lon = lon, lat = lat)
)

fun City.toEntity(): CityEntity = CityEntity(
    id = id,
    name = name,
    country = country,
    lat = coord.lat,
    lon = coord.lon
)
