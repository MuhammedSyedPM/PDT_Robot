package com.syed.jetpacktwo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.syed.jetpacktwo.ui.home.HomeScreen
import com.syed.jetpacktwo.ui.login.LoginScreen

import com.syed.jetpacktwo.ui.splash.SplashScreen

@Composable
fun NavGraph(
    onExit: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onTimeout = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onExit = onExit
            )
        }
        composable("home") {
            HomeScreen(
                onScanClick = { navController.navigate("scan") },
                onUploadClick = { /* Navigate to Upload Screen */ },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
        composable("scan") {
            com.syed.jetpacktwo.ui.scan.ScanScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
