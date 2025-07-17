package com.uala.challengeandroid.utils

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.uala.challengeandroid.R
import com.uala.challengeandroid.model.City
import com.uala.challengeandroid.model.CityEntity
import com.uala.challengeandroid.model.toDomain
import com.uala.challengeandroid.model.toEntity
import com.uala.challengeandroid.ui.theme.AccentPink
import com.uala.challengeandroid.ui.theme.PureWhite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Composable
fun CityItem(
    city: City,
    isFavorite: Boolean,
    onToggleFavorite: (City) -> Unit,
    onClick: (City) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(city) }
            .padding(12.dp).testTag("CityItem"),
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
    object CityDetail : Screen("city_detail/{lat}") {
        fun createRoute(lat: Double) = "city_detail/$lat"
    }
}
sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: Throwable) : Result<Nothing>
    data object Loading : Result<Nothing>
}
fun <T> Result<T>.ifSuccess(block: (T) -> Unit) {
    if (this is Result.Success) block(data)
}

fun <T> Flow<T>.stateAsResultIn(scope: CoroutineScope): StateFlow<Result<T>> =
    map<T, Result<T>> { Result.Success(it) }
        .catch { emit(Result.Error(it)) }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Result.Loading
        )

fun List<City>.toEntityList(): List<CityEntity> = map { it.toEntity() }
fun List<CityEntity>.toDomainList(): List<City> = map { it.toDomain() }

@Composable
fun ErrorText(error: Throwable, modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.error) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier.size(72.dp)
            )
            Text(
                text = error.localizedMessage ?: "An error occurred",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
            )
        }
    }
}

const val LOADING_INDICATOR_TAG = "loadingIndicator"

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag(LOADING_INDICATOR_TAG),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
@Composable
fun PermissionRequestEffect(permission: String, onResult: (Boolean) -> Unit) {
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            onResult(it)
        }
    LaunchedEffect(Unit) {
        permissionLauncher.launch(permission)
    }
}

fun Modifier.accentPink(
    shape: Shape = RoundedCornerShape(8.dp),
    padding: Dp = 8.dp
): Modifier = composed {
    val ripple = rememberRipple(color = PureWhite.copy(alpha = .16f))
    val interactions = remember { MutableInteractionSource() }

    this
        .clip(shape)
        .background(AccentPink)
        .indication(interactions, ripple)
        .padding(padding)
}