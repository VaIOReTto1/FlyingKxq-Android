package com.atcumt.kxq.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.atcumt.kxq.page.login.view.LoginPage.LoginPage
import com.atcumt.kxq.page.MainPage
import com.atcumt.kxq.page.login.view.RegisterPage.RegisterPage
import com.atcumt.kxq.page.login.view.RegisterPage.SchoolCertificationPage
import com.atcumt.kxq.page.profile.edit.NameEidtPage

@Composable
fun NavigationSetup() {
    // 创建 NavController
    val navController = rememberNavController()

    LaunchedEffect(navController) {
        NavViewModel.navController.value = navController
    }
    NavHost(
        navController = navController,
        startDestination = "main"
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
        composable("school") {
            SchoolCertificationPage(navController = navController)
        }
        composable("name") {
            NameEidtPage()
        }
    }
}

object NavViewModel : ViewModel() {
    val navController = MutableLiveData<NavController>()
}