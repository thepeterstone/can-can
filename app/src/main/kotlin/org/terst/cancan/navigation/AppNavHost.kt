package org.terst.cancan.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import org.terst.cancan.cooking.CookingScreen
import org.terst.cancan.inventory.InventoryScreen
import org.terst.cancan.reading_room.PdfViewerScreen
import org.terst.cancan.reading_room.ReadingRoomScreen
import org.terst.cancan.recipes.RecipesScreen
import org.terst.cancan.reference.ReferenceScreen

sealed class Screen(val route: String, val label: String) {
    data object Recipes : Screen("recipes", "Recipes")
    data object Cooking : Screen("cooking", "Cooking")
    data object Inventory : Screen("inventory", "Inventory")
    data object Reference : Screen("reference", "Reference")
    data object ReadingRoom : Screen("reading_room", "Library")
}

private val topLevelScreens = listOf(
    Screen.Recipes,
    Screen.Inventory,
    Screen.Reference,
    Screen.ReadingRoom,
)

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val analytics = Firebase.analytics

    var isFullscreen by remember { mutableStateOf(false) }
    val currentRoute = currentDestination?.route ?: ""

    LaunchedEffect(navBackStackEntry) {
        val route = navBackStackEntry?.destination?.route ?: return@LaunchedEffect
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, route)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, route)
        }
        if (!route.startsWith("pdf_viewer")) isFullscreen = false
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = !isFullscreen,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NavigationBar {
                    topLevelScreens.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = when (screen) {
                                        Screen.Recipes -> Icons.Default.MenuBook
                                        Screen.Inventory -> Icons.Default.Inventory
                                        Screen.Reference -> Icons.Default.Book
                                        Screen.Cooking -> Icons.Default.Restaurant
                                        Screen.ReadingRoom -> Icons.Default.LibraryBooks
                                    },
                                    contentDescription = screen.label
                                )
                            },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Recipes.route,
            modifier = if (isFullscreen) Modifier.fillMaxSize() else Modifier.padding(innerPadding)
        ) {
            composable(Screen.Recipes.route) { RecipesScreen(navController) }
            composable(Screen.Cooking.route) { CookingScreen(navController) }
            composable(Screen.Inventory.route) { InventoryScreen(navController) }
            composable(Screen.Reference.route) { ReferenceScreen(navController) }
            composable(Screen.ReadingRoom.route) { ReadingRoomScreen(navController) }
            composable(
                route = "pdf_viewer/{documentId}",
                arguments = listOf(navArgument("documentId") { type = NavType.StringType })
            ) {
                PdfViewerScreen(
                    navController = navController,
                    isFullscreen = isFullscreen,
                    onToggleFullscreen = { isFullscreen = !isFullscreen }
                )
            }
        }
    }
}
