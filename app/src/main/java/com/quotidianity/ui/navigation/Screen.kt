package com.quotidianity.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object TaskListDetails : Screen("taskListDetails/{listId}") {
        fun createRoute(listId: Int) = "taskListDetails/$listId"
    }
    object AddTaskList : Screen("addTaskList")
}
