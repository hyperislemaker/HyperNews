package com.example.telegramstyle.newsapp.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.telegramstyle.newsapp.presentation.screens.admin.AdminPanelScreen
import com.example.telegramstyle.newsapp.presentation.screens.auth.LoginScreen
import com.example.telegramstyle.newsapp.presentation.screens.auth.ProfileSetupScreen
import com.example.telegramstyle.newsapp.presentation.screens.detail.NewsDetailScreen
import com.example.telegramstyle.newsapp.presentation.screens.favorites.FavoritesScreen
import com.example.telegramstyle.newsapp.presentation.screens.feed.NewsFeedScreen
import com.example.telegramstyle.newsapp.presentation.screens.onboarding.OnboardingScreen
import com.example.telegramstyle.newsapp.presentation.screens.profile.ProfileScreen
import com.example.telegramstyle.newsapp.presentation.screens.rss.RssManagementScreen
import com.example.telegramstyle.newsapp.presentation.screens.search.SearchScreen
import com.example.telegramstyle.newsapp.presentation.screens.settings.SettingsScreen

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Login : Screen("login")
    data object ProfileSetup : Screen("profile_setup")
    data object Feed : Screen("feed")
    data object Favorites : Screen("favorites")
    data object Search : Screen("search")
    data object Settings : Screen("settings")
    data object NewsDetail : Screen("news_detail/{newsId}") {
        fun createRoute(newsId: String) = "news_detail/$newsId"
    }
    data object Profile : Screen("profile")
    data object RssManagement : Screen("rss_management")
    data object AdminPanel : Screen("admin_panel")
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Feed : BottomNavItem(
        route = Screen.Feed.route,
        title = "Haberler",
        selectedIcon = Icons.Filled.Newspaper,
        unselectedIcon = Icons.Outlined.Newspaper
    )
    data object Favorites : BottomNavItem(
        route = Screen.Favorites.route,
        title = "Favoriler",
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder
    )
    data object Search : BottomNavItem(
        route = Screen.Search.route,
        title = "Ara",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    )
    data object Settings : BottomNavItem(
        route = Screen.Settings.route,
        title = "Ayarlar",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
}

@Composable
fun MainNavigation(
    startDestination: String = Screen.Onboarding.route
) {
    val navController = rememberNavController()
    val bottomNavItems = listOf(
        BottomNavItem.Feed,
        BottomNavItem.Favorites,
        BottomNavItem.Search,
        BottomNavItem.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.hierarchy?.any { dest ->
        bottomNavItems.any { it.route == dest.route }
    } == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { 
                            it.route == item.route 
                        } == true

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onComplete = {
                        navController.navigate(Screen.Feed.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { isNewUser ->
                        if (isNewUser) {
                            navController.navigate(Screen.ProfileSetup.route)
                        } else {
                            navController.popBackStack()
                        }
                    },
                    onSkip = { navController.popBackStack() }
                )
            }

            composable(Screen.ProfileSetup.route) {
                ProfileSetupScreen(
                    onSetupComplete = {
                        navController.navigate(Screen.Feed.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Feed.route) {
                NewsFeedScreen(
                    onNewsClick = { newsId ->
                        navController.navigate(Screen.NewsDetail.createRoute(newsId))
                    }
                )
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    onNewsClick = { newsId ->
                        navController.navigate(Screen.NewsDetail.createRoute(newsId))
                    }
                )
            }

            composable(Screen.Search.route) {
                SearchScreen(
                    onNewsClick = { newsId ->
                        navController.navigate(Screen.NewsDetail.createRoute(newsId))
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                    onNavigateToRssManagement = { navController.navigate(Screen.RssManagement.route) },
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) }
                )
            }

            composable(
                route = Screen.NewsDetail.route,
                arguments = listOf(navArgument("newsId") { type = NavType.StringType }),
                deepLinks = listOf(
                    navDeepLink { uriPattern = "newsapp://news/{newsId}" }
                )
            ) { backStackEntry ->
                val newsId = backStackEntry.arguments?.getString("newsId") ?: return@composable
                NewsDetailScreen(
                    newsId = newsId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onLogout = {
                        navController.navigate(Screen.Feed.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.RssManagement.route) {
                RssManagementScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AdminPanel.route) {
                AdminPanelScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
