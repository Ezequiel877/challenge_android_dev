package com.uala.challengeandroid

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
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
fun CityListScreen(viewModel: CityViewModel= hiltViewModel()) {
    val filteredCities by viewModel.filteredCities.collectAsState()
    val query by viewModel.query.collectAsState()
    var active by remember {  mutableStateOf(false)}
    Log.d("TAGLIST", "CityListScreen: ${viewModel.loadCities()}")
    Column {
        SearchBar(
            query = query,
            onQueryChange = {viewModel.setQuery(it)},
            onSearch = {  },
            active = active,
            onActiveChange = {active = it }){}
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredCities) { city ->
                CityItem(city)
            }
            item {
                LaunchedEffect(filteredCities.size) {
                    //viewModel.loadNextPage()
                }
            }
        }
    }
}