package com.atcumt.kxq.utils

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.atcumt.kxq.page.login.view.LoginPage.LoginPage
import com.atcumt.kxq.page.MainPage
import com.atcumt.kxq.page.login.view.RegisterPage.RegisterPage

@Composable
fun NavigationSetup() {
    // 创建 NavController
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginPage(navController = navController)
        }
        composable("register") {
            RegisterPage(navController = navController)
        }
        composable("main") {
            MainPage()
        }
    }
}