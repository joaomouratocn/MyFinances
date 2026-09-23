package br.com.arthiviatech.myfinances.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.EventNote
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

enum class MainTab(val label: String, val symbol: ImageVector) {
    HOME("Início", Icons.Rounded.Home),
    FUTURE("Futuras", Icons.AutoMirrored.Rounded.EventNote),
    REPORTS("Relatórios", Icons.Rounded.BarChart),
    MORE("Mais", Icons.Rounded.MoreHoriz),
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
