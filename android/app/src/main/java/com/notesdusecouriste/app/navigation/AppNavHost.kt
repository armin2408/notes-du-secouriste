package com.notesdusecouriste.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.notesdusecouriste.app.ui.components.AideMemoireThemeActions
import com.notesdusecouriste.app.ui.components.ThemeToggleIconButton
import com.notesdusecouriste.app.ui.home.HomeScreen
import com.notesdusecouriste.app.ui.settings.SecouristeProfileScreen
import com.notesdusecouriste.app.ui.settings.SettingsScreen
import com.notesdusecouriste.app.ui.theme.appThemeViewModel
import com.notesdusecouriste.feature.aidememoire.ui.AideMemoirePlaceholderScreen
import com.notesdusecouriste.feature.interventionnotes.ui.InterventionNotesScreen
import com.notesdusecouriste.feature.interventionnotes.ui.recap.InterventionRecapScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val themeViewModel = appThemeViewModel()
    val isDark by themeViewModel.effectiveIsDark.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onNewIntervention = { id ->
                    navController.navigate(Routes.intervention(id)) {
                        launchSingleTop = true
                    }
                },
                onAideMemoire = {
                    navController.navigate(Routes.AIDE_MEMOIRE)
                },
                onOpenIntervention = { id ->
                    navController.navigate(Routes.intervention(id)) {
                        launchSingleTop = true
                    }
                },
                onOpenRecap = { id ->
                    navController.navigate(Routes.interventionRecap(id)) {
                        launchSingleTop = true
                    }
                },
                onSettings = {
                    navController.navigate(Routes.SETTINGS)
                },
            )
        }
        composable(
            route = Routes.INTERVENTION,
            arguments = listOf(navArgument("interventionId") { type = NavType.LongType }),
        ) { entry ->
            val id = entry.arguments?.getLong("interventionId") ?: return@composable
            InterventionNotesScreen(
                interventionId = id,
                onBack = { navController.popBackStack() },
                onRecap = {
                    navController.navigate(Routes.interventionRecap(id)) {
                        launchSingleTop = true
                    }
                },
                actions = {
                    AideMemoireThemeActions(
                        onAideMemoire = {
                            navController.navigate(Routes.AIDE_MEMOIRE)
                        },
                        isDark = isDark,
                        onToggleTheme = { themeViewModel.toggleTheme(isDark) },
                    )
                },
            )
        }
        composable(
            route = Routes.INTERVENTION_RECAP,
            arguments = listOf(navArgument("interventionId") { type = NavType.LongType }),
        ) { entry ->
            val id = entry.arguments?.getLong("interventionId") ?: return@composable
            InterventionRecapScreen(
                onBack = { navController.popBackStack() },
                onComplete = {
                    val returnedToNotes = navController.popBackStack(
                        route = Routes.intervention(id),
                        inclusive = false,
                    )
                    if (!returnedToNotes) {
                        navController.navigate(Routes.intervention(id)) {
                            launchSingleTop = true
                        }
                    }
                },
                onAideMemoire = {
                    navController.navigate(Routes.AIDE_MEMOIRE)
                },
                actions = {
                    AideMemoireThemeActions(
                        onAideMemoire = {
                            navController.navigate(Routes.AIDE_MEMOIRE)
                        },
                        isDark = isDark,
                        onToggleTheme = { themeViewModel.toggleTheme(isDark) },
                    )
                },
            )
        }
        composable(Routes.AIDE_MEMOIRE) {
            AideMemoirePlaceholderScreen(
                onBack = { navController.popBackStack() },
                actions = {
                    ThemeToggleIconButton(
                        isDark = isDark,
                        onToggle = { themeViewModel.toggleTheme(isDark) },
                    )
                },
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onOpenProfile = { navController.navigate(Routes.SECOURISTE_PROFILE) },
            )
        }
        composable(Routes.SECOURISTE_PROFILE) {
            SecouristeProfileScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
