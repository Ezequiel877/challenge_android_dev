package com.uala.challengeandroid.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.uala.challengeandroid.utils.PermissionRequestEffect
import kotlinx.coroutines.tasks.await


@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    lat: Double,
    lon: Double,
    onCityClick: (lat: Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    val location = LatLng(lat, lon)
    val cameraPositionState = rememberCameraPositionState()
    var mapLoaded by remember { mutableStateOf(false) }
    var showNotice by remember { mutableStateOf(false) }
    var distanceMeters by remember { mutableStateOf<Float?>(null) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    PermissionRequestEffect(
        permission = Manifest.permission.ACCESS_COARSE_LOCATION
    ) { granted ->
        hasLocationPermission = granted
    }
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    // Wait for the map to load, then move the camera
    LaunchedEffect(mapLoaded, lat, lon) {
        if (mapLoaded) {
            try {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(location, 10f),
                    durationMs = 1000
                )
                showNotice = true
                if (hasLocationPermission &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    try {
                        val lastLoc = fusedLocationClient.lastLocation.await()
                        val results = FloatArray(1)
                        Location.distanceBetween(
                            lat, lon,
                            lastLoc.latitude, lastLoc.longitude,
                            results
                        )
                        distanceMeters = results[0]
                    } catch (_: Exception) {
                        distanceMeters = null
                    }
                }
            } catch (e: Exception) {
                Log.e("MapScreen", "Error al mover la cámara: ${e.message}")
            }
        }
    }
    Box(modifier = modifier.fillMaxSize()) {
        if (hasLocationPermission) {
            GoogleMap(
                modifier = Modifier.matchParentSize(),
                cameraPositionState = cameraPositionState,
                onMapLoaded = { mapLoaded = true }
            ) {
                Marker(
                    state = MarkerState(position = location),
                    title = "Ciudad",
                    snippet = "Lat: $lat, Lon: $lon"
                )
            }
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Permiso de ubicación denegado.\nSin él no se puede mostrar el mapa.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        if (mapLoaded && showNotice && hasLocationPermission) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Detalle de la Ciudad",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = {
                                distanceMeters?.toDouble()?.let { onCityClick(it) }
                            }
                        ) {
                            Text("Ver más")
                        }
                    }
                }
            }
        }
    }
}