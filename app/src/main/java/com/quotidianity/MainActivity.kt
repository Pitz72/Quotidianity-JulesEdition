package com.quotidianity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.quotidianity.ui.add_task_list.AddTaskListScreen
import com.quotidianity.ui.home.HomeScreen
import com.quotidianity.ui.navigation.Screen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.quotidianity.ui.task_list_details.TaskListDetailsScreen
import com.quotidianity.ui.theme.QuotidianityTheme

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
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onFabClick = { navController.navigate(Screen.AddTaskList.route) },
                onTaskListClick = { listId ->
                    navController.navigate(Screen.TaskListDetails.createRoute(listId))
                }
            )
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
        composable(Screen.AddTaskList.route) {
            AddTaskListScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
