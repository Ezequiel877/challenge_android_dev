package com.uala.challengeandroid

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uala.challengeandroid.presentation.CityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChallengeScreen()
        }
    }
}

@Composable
fun ChallengeScreen(viewModel: CityViewModel = hiltViewModel()) {
    val query by viewModel.query.collectAsState()
    val filteredCities by viewModel.filteredCities.collectAsState()
    Log.d("TAGLIST", "ChallengeScreen: $filteredCities")
    LaunchedEffect(Unit) {
        viewModel.loadCities()
    }
    Column {
        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.setQuery(it) },
            label = { Text("Buscar ciudad...") }
        )
        LazyColumn {
            items(filteredCities) { city ->
                Row(Modifier.padding(8.dp)) {
                    Column {
                        Text("${city.name}, ${city.country}", style = MaterialTheme.typography.titleMedium)
                        Text("Lon: ${city.coord.lon}, Lat: ${city.coord.lat}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
