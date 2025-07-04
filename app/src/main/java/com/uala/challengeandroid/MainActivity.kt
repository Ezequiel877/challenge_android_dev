package com.uala.challengeandroid

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.uala.challengeandroid.presentation.CityViewModel
import com.uala.challengeandroid.utils.CityItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CityListScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityListScreen(viewModel: CityViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        viewModel.loadCities()
    }
    val query by viewModel.query.collectAsState()
    val isSearching = query.isNotBlank()
    val pagedCities by if (isSearching) {
        viewModel.pagedSearchCities.collectAsState()
    } else {
        viewModel.pagedAllCities.collectAsState()
    }
    val pageall by viewModel.pagedAllCities.collectAsState()
    var active by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    if (pagedCities.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        Log.d("TAGLIST", "Aún no hay datos para mostrar")
        return
    }
    LaunchedEffect(listState, pagedCities) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val lastVisible = visibleItems.lastOrNull()?.index ?: 0
                val threshold = pagedCities.lastIndex - 3

                if (lastVisible >= threshold) {
                    if (query.isNotBlank()) {
                        viewModel.loadNextSearchPage()
                    } else {
                        viewModel.loadNextGeneralPage()
                    }
                }
            }
    }
    Column {
        SearchBar(
            query = query,
            onQueryChange = { viewModel.setQuery(it) },
            onSearch = {},
            active = active,
            onActiveChange = { active = it }
        ) {LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            Log.d("TAGLIST", "filteredCities: $pagedCities")
            Log.d("TAGLIST", "query: $query")
            items(pagedCities) { city ->
                Log.d("TAGLIST", "city: $city")

                CityItem(city)
            }
        }        }
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(pageall) { city ->
                Log.d("TAGLIST", "city: $city")

                CityItem(city)
            }
        }
    }
}
