package com.uala.challengeandroid.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
}
@Composable
fun CityItem2(
    city: City,
    isFavorite: Boolean,
    onToggleFavorite: (City) -> Unit,
    onClick: (City) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(city) }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(text = city.name, style = MaterialTheme.typography.titleMedium)
            Text(text = city.country, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Lat: ${city.coord.lat}, Lon: ${city.coord.lon}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        IconButton(onClick = { onToggleFavorite(city) }) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = if (isFavorite) Color.Red else Color.Gray
            )
        }
    }
}


sealed class Screen(val route: String) {
    object CityList : Screen("city_list")
    object Map : Screen("map/{lat}/{lon}") {
        fun createRoute(lat: Double, lon: Double) = "map/$lat/$lon"
    }
}


sealed class LoadState<out T> {
    object Loading : LoadState<Nothing>()
    data class Success<T>(val data: List<T>) : LoadState<T>()
    data class Error(val exception: Throwable) : LoadState<Nothing>()
}
fun List<City>.toEntityList(): List<CityEntity> = map { it.toEntity() }
fun List<CityEntity>.toDomainList(): List<City> = map { it.toDomain() }