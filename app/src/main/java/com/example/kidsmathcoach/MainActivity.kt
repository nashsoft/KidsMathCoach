package com.example.kidsmathcoach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kidsmathcoach.ui.theme.KidsMathCoachTheme
import androidx.compose.ui.platform.LocalContext
import com.example.kidsmathcoach.repository.SettingsRepositoryImpl
import com.example.kidsmathcoach.ui.screens.MainScreen
import com.example.kidsmathcoach.ui.screens.SettingsScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KidsMathCoachTheme {
                // Загрузка первоначальных настроек из репозитория
                val context = LocalContext.current
                val repository = SettingsRepositoryImpl(context)
                var settings = remember { repository.loadSettings() }

                val navController = rememberNavController()

                NavHost(navController, startDestination = "MainScreen") {
                    composable("MainScreen") {
                        MainScreen(navController, settings, repository)
                    }
                    composable("SettingsScreen") {
                        SettingsScreen(navController, context, settings, repository)
                    }
                }
            }
        }
    }
}