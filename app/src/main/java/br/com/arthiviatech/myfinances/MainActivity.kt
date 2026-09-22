package br.com.arthiviatech.myfinances

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.com.arthiviatech.myfinances.navigation.Cards
import br.com.arthiviatech.myfinances.navigation.Categories
import br.com.arthiviatech.myfinances.navigation.ExpenseDetails
import br.com.arthiviatech.myfinances.navigation.FutureAccounts
import br.com.arthiviatech.myfinances.navigation.Home
import br.com.arthiviatech.myfinances.navigation.MainTab
import br.com.arthiviatech.myfinances.navigation.More
import br.com.arthiviatech.myfinances.navigation.NewExpense
import br.com.arthiviatech.myfinances.navigation.Reports
import br.com.arthiviatech.myfinances.navigation.Revenues
import br.com.arthiviatech.myfinances.navigation.navigateToApp
import br.com.arthiviatech.myfinances.ui.cards.CardsCompose
import br.com.arthiviatech.myfinances.ui.categories.CategoriesCompose
import br.com.arthiviatech.myfinances.ui.expense.ExpenseDetailsCompose
import br.com.arthiviatech.myfinances.ui.expense.NewExpensive
import br.com.arthiviatech.myfinances.ui.future.FutureCompose
import br.com.arthiviatech.myfinances.ui.home.HomeCompose
import br.com.arthiviatech.myfinances.ui.more.MoreCompose
import br.com.arthiviatech.myfinances.ui.reports.ReportsCompose
import br.com.arthiviatech.myfinances.ui.revenue.RevenueCompose
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { MyFinancesTheme { MinhasFinancasApp() } }
    }
}

@Composable
private fun MinhasFinancasApp() {
    val navController = rememberNavController()
    val entry by navController.currentBackStackEntryAsState()
    val destination = entry?.destination
    val selectedTab = when {
        destination?.hasRoute(Home::class) == true -> MainTab.HOME
        destination?.hasRoute(FutureAccounts::class) == true -> MainTab.FUTURE
        destination?.hasRoute(Reports::class) == true -> MainTab.REPORTS
        destination?.hasRoute(More::class) == true ||
                destination?.hasRoute(Revenues::class) == true ||
                destination?.hasRoute(Cards::class) == true ||
                destination?.hasRoute(Categories::class) == true -> MainTab.MORE

        else -> null
    }

    BoxWithConstraints {
        if (maxWidth >= 600.dp) {
            Row(Modifier.fillMaxSize()) {
                NavigationRail {
                    MainTab.entries.forEach { tab ->
                        NavigationRailItem(
                            selected = selectedTab == tab,
                            onClick = { navController.navigateToApp(tab) },
                            icon = { NavigationSymbol(tab.symbol) },
                            label = { Text(tab.label) },
                            alwaysShowLabel = true
                        )
                    }
                }
                AppContent(Modifier.weight(1f), navController)
            }
        } else {
            Scaffold(bottomBar = {
                NavigationBar {
                    MainTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = { navController.navigateToApp(tab) },
                            icon = { NavigationSymbol(tab.symbol) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }) { padding ->
                AppContent(Modifier.padding(padding), navController)
            }
        }
    }
}
@Composable
private fun AppContent(modifier: Modifier, navController: NavHostController) {
    NavHost(navController, startDestination = Home, modifier = modifier.fillMaxSize()) {
        composable<Home> { HomeCompose() }
        composable<More> { MoreCompose() }
        composable<Reports> { ReportsCompose() }
        composable<NewExpense> { NewExpensive() }
        composable<FutureAccounts> { FutureCompose() }
        composable<Revenues> { RevenueCompose() }
        composable<Cards> { CardsCompose() }
        composable<Categories> { CategoriesCompose() }
        composable<ExpenseDetails> { ExpenseDetailsCompose() }
    }
}

@Composable
private fun NavigationSymbol(symbol: String) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            symbol,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
