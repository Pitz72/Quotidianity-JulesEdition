package com.quotidianity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.quotidianity.ui.add_task_list.AddTaskListScreen
import com.quotidianity.ui.add_task_list.AddTaskListViewModel
import com.quotidianity.ui.agenda.AgendaScreen
import com.quotidianity.ui.home.HomeScreen
import com.quotidianity.ui.navigation.Screen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.quotidianity.ui.settings.SettingsScreen
import com.quotidianity.ui.task_list_details.TaskListDetailsScreen
import com.quotidianity.ui.theme.QuotidianityTheme
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            QuotidianityTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val bottomNavItems = listOf(Screen.Home, Screen.Agenda)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            when (screen) {
                                Screen.Home -> Icon(Icons.Default.List, contentDescription = "Lists")
                                Screen.Agenda -> Icon(Icons.Default.DateRange, contentDescription = "Agenda")
                                else -> {}
                            }
                        },
                        label = { Text(screen.route.replaceFirstChar { it.uppercase() }) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
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
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Home.route, Modifier.padding(innerPadding)) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onFabClick = { navController.navigate(Screen.AddTaskList.createRoute()) },
                    onTaskListClick = { listId ->
                        navController.navigate(Screen.TaskListDetails.createRoute(listId))
                    },
                    onSettingsClick = { navController.navigate(Screen.Settings.route) },
                    onTaskListEdit = { listId ->
                        navController.navigate(Screen.AddTaskList.createEditRoute(listId))
                    }
                )
            }
            composable(Screen.Agenda.route) {
                AgendaScreen()
            }
            composable(
                route = Screen.TaskListDetails.route,
                arguments = listOf(navArgument("listId") { type = NavType.IntType })
            ) { backStackEntry ->
                val listId = backStackEntry.arguments?.getInt("listId") ?: 0
                TaskListDetailsScreen(
                    listId = listId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.AddTaskList.route,
                arguments = listOf(navArgument(Screen.AddTaskList.ARG_LIST_ID) {
                    type = NavType.IntType
                    defaultValue = -1
                })
            ) {
                val app = LocalContext.current.applicationContext as QuotidianityApplication
                val viewModel: AddTaskListViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return AddTaskListViewModel(app.repository, it.savedStateHandle) as T
                        }
                    }
                )
                AddTaskListScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
