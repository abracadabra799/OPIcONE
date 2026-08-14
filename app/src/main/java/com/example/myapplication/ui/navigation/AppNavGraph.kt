package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.di.AppContainer
import com.example.myapplication.ui.favorites.FavoritesScreen
import com.example.myapplication.ui.favorites.FavoritesViewModel
import com.example.myapplication.ui.home.HomeScreen
import com.example.myapplication.ui.set.PracticeScreen
import com.example.myapplication.ui.set.PracticeSetViewModel
import com.example.myapplication.ui.set.SetCompleteScreen
import com.example.myapplication.ui.settings.SettingsScreen
import com.example.myapplication.ui.settings.SettingsViewModel

@Composable
fun AppNavGraph(appContainer: AppContainer) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.Home.route) {
        composable(Routes.Home.route) {
            HomeScreen(
                onCategorySelected = { category ->
                    navController.navigate(Routes.Practice.createRoute(category))
                },
                onOpenFavorites = { navController.navigate(Routes.Favorites.route) },
                onOpenSettings = { navController.navigate(Routes.Settings.route) }
            )
        }
        composable(
            route = Routes.Practice.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("category")
                ?: PracticeCategory.SELF_INTRODUCTION.name
            val category = PracticeCategory.valueOf(categoryName)
            val viewModel: PracticeSetViewModel = viewModel(
                factory = SimpleViewModelFactory {
                    PracticeSetViewModel(
                        category = category,
                        practiceRepository = appContainer.practiceRepository,
                        favoriteRepository = appContainer.favoriteRepository,
                        apiKeyStore = appContainer.apiKeyStore,
                        speechPlayer = appContainer.newSpeechPlayer(),
                        voiceRecorder = appContainer.newVoiceRecorder(),
                        voicePlayer = appContainer.newVoicePlayer(),
                        recordingFile = appContainer.newRecordingFile()
                    )
                }
            )
            PracticeScreen(
                viewModel = viewModel,
                onSetComplete = { questionCount ->
                    navController.navigate(Routes.SetComplete.createRoute(questionCount)) {
                        popUpTo(Routes.Home.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.SetComplete.route,
            arguments = listOf(navArgument("questionCount") { type = NavType.IntType })
        ) { backStackEntry ->
            val questionCount = backStackEntry.arguments?.getInt("questionCount") ?: 0
            SetCompleteScreen(
                questionCount = questionCount,
                onBackToHome = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.Favorites.route) {
            val viewModel: FavoritesViewModel = viewModel(
                factory = SimpleViewModelFactory { FavoritesViewModel(appContainer.favoriteRepository) }
            )
            FavoritesScreen(viewModel = viewModel)
        }
        composable(Routes.Settings.route) {
            val viewModel: SettingsViewModel = viewModel(
                factory = SimpleViewModelFactory { SettingsViewModel(appContainer.apiKeyStore) }
            )
            SettingsScreen(viewModel = viewModel)
        }
    }
}
