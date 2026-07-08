package com.syed.jetpacktwo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.syed.jetpacktwo.ui.home.HomeScreen
import com.syed.jetpacktwo.ui.login.LoginScreen

import com.syed.jetpacktwo.ui.splash.SplashScreen

import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import com.syed.jetpacktwo.presentation.rfid.RfidViewModel

@Composable
fun NavGraph(
    onExit: () -> Unit,
    viewModel: RfidViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                // App is going to background
                viewModel.stopReader()
                
                // Hijack the OS snapshot by navigating *before* it's fully backgrounded
                val currentRoute = navController.currentDestination?.route
                if (currentRoute != null && currentRoute != "splash" && currentRoute != "login" && currentRoute != "home") {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                    }
                }
            }
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(observer)
        onDispose {
            ProcessLifecycleOwner.get().lifecycle.removeObserver(observer)
        }
    }

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
        composable(
            route = "login",
            enterTransition = {
                androidx.compose.animation.slideInHorizontally(
                    initialOffsetX = { -it }, // starts from left and slides to 0 (rightward)
                    animationSpec = androidx.compose.animation.core.tween(700, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                ) + androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(700))
            }
        ) {
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
                onUploadClick = { /* Deprecated/Not used directly in HomeScreen anymore if handled via LaunchedEffect */ },
                onDownloadRackClick = { 
                    // TODO: Navigate to download rack screen or trigger download rack functionality
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onReportClick = { type ->
                    if (type == "rack_status") {
                        navController.navigate("rack_status")
                    } else if (type == "stock_status") {
                        navController.navigate("stock_status")
                    } else if (type == "to_be_uploaded") {
                        navController.navigate("to_be_uploaded")
                    }
                }
            )
        }
        composable("scan") {
            com.syed.jetpacktwo.ui.scan.ScanScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("rack_status") {
            com.syed.jetpacktwo.ui.report.RackStatusScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("stock_status") {
            com.syed.jetpacktwo.ui.report.StockStatusScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("to_be_uploaded") {
            com.syed.jetpacktwo.ui.report.ToBeUploadedScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
