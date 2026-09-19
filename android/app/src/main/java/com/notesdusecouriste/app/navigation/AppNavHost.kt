package com.notesdusecouriste.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.notesdusecouriste.app.ui.components.AideMemoireThemeActions
import com.notesdusecouriste.app.ui.components.ThemeToggleIconButton
import com.notesdusecouriste.app.ui.home.HomeScreen
import com.notesdusecouriste.app.ui.onboarding.OnboardingGateViewModel
import com.notesdusecouriste.app.ui.settings.SecouristeProfileScreen
import com.notesdusecouriste.app.ui.settings.SettingsScreen
import com.notesdusecouriste.app.ui.theme.appThemeViewModel
import com.notesdusecouriste.feature.aidememoire.ui.AideMemoirePlaceholderScreen
import com.notesdusecouriste.feature.interventionnotes.ui.InterventionNotesScreen
import com.notesdusecouriste.feature.interventionnotes.ui.recap.InterventionRecapScreen
import com.notesdusecouriste.feature.onboarding.ui.ChangelogJournalScreen
import com.notesdusecouriste.feature.onboarding.ui.WelcomeCarouselScreen

@Composable
fun AppNavHost(
    onboardingGateViewModel: OnboardingGateViewModel = hiltViewModel(),
) {
    val onboardingCompleted by onboardingGateViewModel.onboardingCompleted.collectAsStateWithLifecycle()
    val themeViewModel = appThemeViewModel()
    val isDark by themeViewModel.effectiveIsDark.collectAsStateWithLifecycle()

    when (onboardingCompleted) {
        null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
        else -> {
            key(onboardingCompleted) {
                val navController = rememberNavController()
                val startDestination = if (onboardingCompleted == false) {
                    Routes.onboarding(preview = false)
                } else {
                    Routes.HOME
                }

                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                ) {
                    composable(
                        route = Routes.ONBOARDING,
                        arguments = listOf(
                            navArgument("preview") {
                                type = NavType.BoolType
                                defaultValue = false
                            },
                        ),
                    ) { entry ->
                        val preview = entry.arguments?.getBoolean("preview") ?: false
                        WelcomeCarouselScreen(
                            previewMode = preview,
                            onFinished = {
                                if (preview) {
                                    navController.popBackStack()
                                } else {
                                    onboardingGateViewModel.completeOnboarding { }
                                }
                            },
                        )
                    }
                    composable(Routes.CHANGELOG) {
                        ChangelogJournalScreen(
                            onBack = { navController.popBackStack() },
                        )
                    }
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
                        arguments = listOf(
                            navArgument("interventionId") { type = NavType.LongType },
                        ),
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
                        arguments = listOf(
                            navArgument("interventionId") { type = NavType.LongType },
                        ),
                    ) { entry ->
                        val id = entry.arguments?.getLong("interventionId") ?: return@composable
                        InterventionRecapScreen(
                            onBack = { navController.popBackStack() },
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
                            onOpenProfile = {
                                navController.navigate(Routes.SECOURISTE_PROFILE)
                            },
                            onOpenWelcome = {
                                navController.navigate(Routes.onboarding(preview = true))
                            },
                            onOpenChangelog = {
                                navController.navigate(Routes.CHANGELOG)
                            },
                        )
                    }
                    composable(Routes.SECOURISTE_PROFILE) {
                        SecouristeProfileScreen(
                            onBack = { navController.popBackStack() },
                        )
                    }
                }
            }
        }
    }
}
