package com.uala.challengeandroid.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.uala.challengeandroid.model.City
import com.uala.challengeandroid.model.CityEntity
import com.uala.challengeandroid.model.toDomain
import com.uala.challengeandroid.model.toEntity

class extensions {}

@Composable
fun CityItem(city: City, onClick: (City) -> Unit = {}) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick(city) }
        .padding(12.dp)) {
        Text(
            text = city.name, style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = city.country, style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Lon: ${city.coord.lat}, Lat: ${city.coord.lon}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
    Divider()
}

sealed class LoadState<out T> {
    object Loading : LoadState<Nothing>()
    data class Success<T>(val data: List<T>, val endReached: Boolean) : LoadState<T>()
    data class Error(val exception: Throwable) : LoadState<Nothing>()
}
fun List<City>.toEntityList(): List<CityEntity> = map { it.toEntity() }
fun List<CityEntity>.toDomainList(): List<City> = map { it.toDomain() }