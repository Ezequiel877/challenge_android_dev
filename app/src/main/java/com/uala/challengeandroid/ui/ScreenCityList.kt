package com.uala.challengeandroid.ui

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uala.challengeandroid.model.City
import com.uala.challengeandroid.presentation.CityViewModel
import com.uala.challengeandroid.ui.theme.AccentPink
import com.uala.challengeandroid.ui.theme.ChallengeAndroidTheme
import com.uala.challengeandroid.ui.theme.PureWhite
import com.uala.challengeandroid.utils.CityItem
import com.uala.challengeandroid.utils.ErrorText
import com.uala.challengeandroid.utils.LoadingIndicator
import com.uala.challengeandroid.utils.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenCityList(
    viewModel: CityViewModel = hiltViewModel(), onCityClick: (City) -> Unit
) {
    val cities by viewModel.state.collectAsState()
    val query by viewModel.query.collectAsState()
    var active by remember { mutableStateOf(false) }
    val selectedCity = remember { mutableStateOf<City?>(null) }
    val onlyFavorites by viewModel.onlyFavorites.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    LaunchedEffect(Unit) {
        viewModel.onUiReady()
    }
    when (cities) {
        is Result.Loading -> {
            LoadingIndicator(modifier = Modifier.padding(20.dp))
        }
        is Result.Success -> {
            val data = ((cities as Result.Success<List<City>>).data)
            ChallengeAndroidTheme {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SearchBar(
                            query           = query,
                            onQueryChange   = viewModel::setQuery,
                            onSearch        = {  },
                            active          = active,
                            onActiveChange  = { active = it },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Buscar",
                                    tint = PureWhite     // contraste
                                )
                            },
                            colors = SearchBarDefaults.colors(
                                containerColor = AccentPink,
                                dividerColor   = Color.Transparent,
                                inputFieldColors = TextFieldDefaults.colors(
                                    focusedTextColor        = PureWhite,
                                    unfocusedTextColor      = PureWhite,
                                    cursorColor             = PureWhite,
                                    focusedContainerColor   = AccentPink,
                                    unfocusedContainerColor = AccentPink,
                                    focusedIndicatorColor   = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                )
                            ),
                            modifier = Modifier
                                .weight(0.7f)
                                .height(70.dp)
                                .clip(RoundedCornerShape(28.dp))
                                .testTag("SearchBar")
                        ) {  }

                        Spacer(Modifier.width(8.dp))
                        Switch(
                            modifier = Modifier.weight(0.2f),
                            checked = onlyFavorites,
                            onCheckedChange = { viewModel.setOnlyFavorites(!onlyFavorites) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor       = PureWhite,
                                checkedTrackColor       = AccentPink,
                                uncheckedThumbColor     = PureWhite,
                                uncheckedBorderColor    = AccentPink,
                                uncheckedTrackColor     = AccentPink.copy(alpha = .3f)
                            )
                        )
                    }
                    Box(
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        if (isLandscape) {
                            Row(modifier = Modifier.fillMaxSize()) {
                                LazyColumn(
                                    modifier = Modifier
                                        .weight(0.5f)
                                        .fillMaxHeight()
                                ) {
                                    items(data) { city ->
                                        Log.d("TAGLIST", "ScreenCityList:$city ")
                                        CityItem(city = city,
                                            isFavorite = city.isFavorite,
                                            onToggleFavorite = { viewModel.toggleFavorite(city) },
                                            onClick = { selectedCity.value = city })
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(0.5f)
                                        .fillMaxHeight()
                                ) {
                                    selectedCity.value?.let { city ->
                                        MapScreen(
                                            city.coord.lat, city.coord.lon, onCityClick = { lat ->
                                                Log.d("MapScreen", "Navegar a detalle de ciudad con lat: $lat,")
                                            })
                                    }
                                    if (selectedCity.value == null) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("Seleccioná una ciudad")
                                        }
                                    }
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxSize()
                            ) {
                                items(data) { city ->
                                    Log.d("TAGLIST", "ScreenCityList:$city ")
                                    CityItem(city = city,
                                        isFavorite = city.isFavorite,
                                        onToggleFavorite = { viewModel.toggleFavorite(city) },
                                        onClick = { onCityClick(city) })
                                }
                            }
                        }
                    }
                }
            }
        }

        is Result.Error -> {
            ErrorText(
                error = (cities as Result.Error).exception,
                modifier = Modifier
                    .padding(12.dp)
                    .testTag("Error")
            )
        }
    }
}