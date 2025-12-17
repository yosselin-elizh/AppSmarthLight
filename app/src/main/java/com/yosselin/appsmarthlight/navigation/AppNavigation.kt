package com.yosselin.appsmarthlight.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yosselin.appsmarthlight.screens.ConfigurationScreen
import com.yosselin.appsmarthlight.screens.HomeScreen
import com.yosselin.appsmarthlight.screens.LoginScreen
import com.yosselin.appsmarthlight.screens.SplashScreen
import com.yosselin.appsmarthlight.screens.StatusScreen
import com.yosselin.appsmarthlight.screens.WelcomeScreen
import com.yosselin.appsmarthlight.ui.viewmodels.ConfigurationViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val configViewModel: ConfigurationViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("welcome") { WelcomeScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("menu") { HomeScreen(navController) }
        composable("system_status") { StatusScreen(navController) }
        composable("configuration") { ConfigurationScreen(navController, configViewModel) }
    }
}