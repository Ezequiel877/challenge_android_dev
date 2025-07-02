package com.uala.challengeandroid.model

import java.io.Serializable
import javax.inject.Qualifier

data class City(
    val country: String,
    val name: String,
    val _id: Long,
    val coord: Coord
):Serializable

data class Coord(
    val lon: Double,
    val lat: Double
):Serializable
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class ApiKey