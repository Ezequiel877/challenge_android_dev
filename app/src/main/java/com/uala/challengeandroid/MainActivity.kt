package com.uala.challengeandroid

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.uala.challengeandroid.model.City
import com.uala.challengeandroid.presentation.CityViewModel
import com.uala.challengeandroid.utils.CityItem2
import com.uala.challengeandroid.utils.Screen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavHost()
        }
    }
}

@Composable
fun CityMapScreen2(lat: Double, lon: Double) {
    val cameraPositionState = rememberCameraPositionState()
    val location = LatLng(lat, lon)
    var mapLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(mapLoaded, lat, lon) {
        if (mapLoaded) {
            cameraPositionState.move(
                CameraUpdateFactory.newLatLngZoom(location, 10f)
            )
        }
    }
    GoogleMap(modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapLoaded = {
            mapLoaded = true
        }) {
        Marker(
            state = MarkerState(position = location),
            title = "Ciudad",
            snippet = "Lat: $lat, Lon: $lon"
        )
    }
}

@Composable
fun AppNavHost(viewModel: CityViewModel = hiltViewModel()) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.CityList.route) {
        composable(Screen.CityList.route) {
            CityListScreen(viewModel = viewModel, onCityClick = { city ->
                navController.navigate(Screen.Map.createRoute(city.coord.lat, city.coord.lon))
            })
        }

        composable(
            route = Screen.Map.route,
            arguments = listOf(navArgument("lat") { type = NavType.FloatType },
                navArgument("lon") { type = NavType.FloatType })
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0
            val lon = backStackEntry.arguments?.getFloat("lon")?.toDouble() ?: 0.0
            CityMapScreen2(lat, lon)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityListScreen(
    viewModel: CityViewModel = hiltViewModel(), onCityClick: (City) -> Unit
) {
    LaunchedEffect(Unit) { viewModel.loadCities() }

    val query by viewModel.query.collectAsState()
    val isSearching = query.isNotBlank()
    val pagedCities by if (isSearching) {
        viewModel.pagedSearchCities.collectAsState()
    } else {
        viewModel.pagedAllCities.collectAsState()
    }
    val apgerAll by viewModel.pagedAllCities.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    var active by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val selectedCity = rememberSaveable { mutableStateOf<City?>(null) }
    val pageall by viewModel.pagedAllCities.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState()
    var onlyFavorites by remember { mutableStateOf(false) }
    LaunchedEffect(selectedCity.value) {
        selectedCity.value?.let { city ->
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(
                    LatLng(city.coord.lat, city.coord.lon), 10f
                ), durationMs = 1000
            )
        }
    }
    LaunchedEffect(listState, pagedCities) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }.collect { visibleItems ->
            val lastVisible = visibleItems.lastOrNull()?.index ?: 0
            val threshold = pagedCities.lastIndex - 3
            if (lastVisible >= threshold) {
                if (query.isNotBlank()) viewModel.loadNextSearchPage()
                else viewModel.loadNextGeneralPage()
            }
        }
    }
    if (pagedCities.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    if (isLandscape) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchBar(
                    query = query,
                    onQueryChange = { viewModel.setQuery(it) },
                    onSearch = {
                       query
                        active = false
                    },
                    active = active,
                    onActiveChange = { active = it },
                    modifier = Modifier.weight(1f)
                ) {
                    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                        items(pagedCities) { city ->
                            CityItem2(
                                city = city,
                                isFavorite = favorites.contains(city.id),
                                onToggleFavorite = { viewModel.toggleFavorite(city) },
                                onClick = { selectedCity.value = city }
                            )
                        }
                    }
                }
                IconButton(
                    onClick = {  showDialog = true },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = "Abrir diálogo")
                }
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Opciones de filtrado") },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Filtrar por favoritos", modifier = Modifier.weight(1f))
                                Switch(
                                    checked = onlyFavorites,
                                    onCheckedChange = {
                                        onlyFavorites = it
                                    }
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Cerrar")
                            }
                        }
                    )
                }
            }
            Row(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState, modifier = Modifier
                        .weight(0.5f)
                        .fillMaxHeight()
                ) {
                    items(apgerAll) { city ->
                        CityItem2(city = city,
                            isFavorite = favorites.contains(city.id),
                            onToggleFavorite = { viewModel.toggleFavorite(city) },
                            onClick = { selectedCity.value = city })
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxHeight()
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState
                    ) {
                        selectedCity.value?.let { city ->
                            Marker(
                                state = MarkerState(
                                    position = LatLng(city.coord.lat, city.coord.lon)
                                ), title = city.name
                            )
                        }
                    }

                    if (selectedCity.value == null) {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            Text("Seleccioná una ciudad")
                        }
                    }
                }
            }
        }
    } else {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchBar(
                    query = query,
                    onQueryChange = { viewModel.setQuery(it) },
                    onSearch = {
                       query
                        active = false
                    },
                    active = active,
                    onActiveChange = { active = it },
                    modifier = Modifier.weight(1f)
                ) {
                    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                        items(pagedCities) { city ->
                            CityItem2(
                                city = city,
                                isFavorite = favorites.contains(city.id),
                                onToggleFavorite = { viewModel.toggleFavorite(city) },
                                onClick = { selectedCity.value = city }
                            )
                        }
                    }
                }
                IconButton(
                    onClick = {  showDialog = true },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = "Abrir diálogo")
                }
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Opciones de filtrado") },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Filtrar por favoritos", modifier = Modifier.weight(1f))
                                Switch(
                                    checked = onlyFavorites,
                                    onCheckedChange = {
                                        onlyFavorites = it
                                    }
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Cerrar")
                            }
                        }
                    )
                }
            }
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                items(pageall) { city ->
                    CityItem2(city = city,
                        isFavorite = favorites.contains(city.id),
                        onToggleFavorite = { viewModel.toggleFavorite(city) },
                        onClick = { onCityClick(city) })
                }
            }
        }
    }
}
