package br.com.arthiviatech.myfinances.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

enum class MainTab(val label: String, val symbol: String) {
    HOME("Início", "I"),
    FUTURE("Futuras", "F"),
    REPORTS("Relatórios", "R"),
    MORE("Mais", "M")
}

private inline fun <reified T : Any> NavHostController.navigateToApp(route: T) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavHostController.navigateToApp(tab: MainTab) {
    when (tab) {
        MainTab.HOME -> navigateToApp(Home)
        MainTab.FUTURE -> navigateToApp(FutureAccounts)
        MainTab.REPORTS -> navigateToApp(Reports)
        MainTab.MORE -> navigateToApp(More)
    }
}
