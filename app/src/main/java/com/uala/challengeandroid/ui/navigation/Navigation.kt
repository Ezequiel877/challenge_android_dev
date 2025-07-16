package com.uala.challengeandroid.ui.navigation

import DetailScreenCity
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.uala.challengeandroid.ui.MapScreen
import com.uala.challengeandroid.ui.ScreenCityList
import com.uala.challengeandroid.utils.Screen


@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.CityList.route) {
        composable(Screen.CityList.route) {
            ScreenCityList (
                onCityClick = { city ->
                    navController.navigate(Screen.Map.createRoute(city.coord.lat, city.coord.lon))
                }
            )
        }
        composable(
            route = Screen.Map.route,
            arguments = listOf(navArgument("lat") { type = NavType.FloatType },
                navArgument("lon") { type = NavType.FloatType })
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0
            val lon = backStackEntry.arguments?.getFloat("lon")?.toDouble() ?: 0.0
            MapScreen(lat, lon,onCityClick = { paramLat ->
                navController.navigate(Screen.CityDetail.createRoute(lat))
            })
        }
        composable(
            route = Screen.CityDetail.route,
            arguments = listOf(
                navArgument("lat") { type = NavType.FloatType },
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0
            DetailScreenCity(lat,
                onBack = {
                    navController.popBackStack()
                })
        }
    }
}
