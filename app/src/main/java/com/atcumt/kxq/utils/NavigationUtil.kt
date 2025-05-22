package com.atcumt.kxq.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.atcumt.kxq.utils.Store.FlyKeyChain.FlyKeyChain
import com.atcumt.kxq.utils.Store.FlyKeyChain.FlyKeyChainType
import com.atcumt.kxq.utils.Store.UserDefaults.FlyUserDefaults
import com.atcumt.kxq.utils.Store.UserDefaults.FlyUserDefaultsKey

@Composable
fun NavigationSetup() {
    // 创建 NavController
    val navController = rememberNavController()

    val authVm: AuthViewModel = hiltViewModel()

    // 立即读取一次 isTokenValid，决定起点
    val start = remember { if (authVm.isTokenValid) "login" else "login" }


    LaunchedEffect(navController) {
        NavViewModel.navController.value = navController
    }
    NavHost(
        navController = navController,
        startDestination = start
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