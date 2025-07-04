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
    @ColumnInfo("id")
    val id: Long,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "country")
    val country: String,
    @ColumnInfo(name = "lat")
    val lat: Double,
    @ColumnInfo(name = "lon")
    val lon: Double,
    @ColumnInfo("isFavorite")
    val isFavorite: Boolean = false
)
fun CityEntity.toDomain(): City = City(
    country = this.country,
    name = this.name,
    id = this.id,
    coord = Coord(lon = this.lon, lat = this.lat)
)

fun City.toEntity(): CityEntity = CityEntity(
    id = this.id,
    name = this.name,
    country = this.country,
    lat = this.coord.lat,
    lon = this.coord.lon
)
