package com.quotidianity.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object TaskListDetails : Screen("taskListDetails/{listId}") {
        fun createRoute(listId: Int) = "taskListDetails/$listId"
    }
    object AddTaskList : Screen("addTaskList?listId={listId}") {
        const val ROUTE = "addTaskList"
        const val ARG_LIST_ID = "listId"
        fun createRoute() = ROUTE
        fun createEditRoute(listId: Int) = "$ROUTE?$ARG_LIST_ID=$listId"
    }
    object Settings : Screen("settings")
    object Agenda : Screen("agenda")
}
