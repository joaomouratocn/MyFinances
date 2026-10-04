package br.com.arthiviatech.myfinances

import android.app.KeyguardManager
import android.os.Bundle
import android.os.SystemClock
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import br.com.arthiviatech.myfinances.navigation.Cards
import br.com.arthiviatech.myfinances.navigation.Categories
import br.com.arthiviatech.myfinances.navigation.ExpenseDetails
import br.com.arthiviatech.myfinances.navigation.FutureAccounts
import br.com.arthiviatech.myfinances.navigation.Home
import br.com.arthiviatech.myfinances.navigation.Invoices
import br.com.arthiviatech.myfinances.navigation.MainTab
import br.com.arthiviatech.myfinances.navigation.More
import br.com.arthiviatech.myfinances.navigation.NewExpense
import br.com.arthiviatech.myfinances.navigation.Reports
import br.com.arthiviatech.myfinances.navigation.Revenues
import br.com.arthiviatech.myfinances.navigation.navigateToApp
import br.com.arthiviatech.myfinances.security.SessionLockPolicy
import br.com.arthiviatech.myfinances.ui.cards.CardsCompose
import br.com.arthiviatech.myfinances.ui.categories.CategoriesCompose
import br.com.arthiviatech.myfinances.ui.expense.ExpenseDetailsCompose
import br.com.arthiviatech.myfinances.ui.expense.NewExpenseCompose
import br.com.arthiviatech.myfinances.ui.future.FutureCompose
import br.com.arthiviatech.myfinances.ui.home.HomeCompose
import br.com.arthiviatech.myfinances.ui.invoices.InvoicesCompose
import br.com.arthiviatech.myfinances.ui.more.MoreCompose
import br.com.arthiviatech.myfinances.ui.reports.ReportsCompose
import br.com.arthiviatech.myfinances.ui.revenue.RevenueCompose
import br.com.arthiviatech.myfinances.ui.security.LockedContent
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme

class MainActivity : FragmentActivity() {
    private val securityPreferences by lazy {
        getSharedPreferences(SECURITY_PREFERENCES, MODE_PRIVATE)
    }
    private val lockPolicy = SessionLockPolicy()
    private var appLockEnabled by mutableStateOf(true)
    private var contentUnlocked by mutableStateOf(false)
    private var authenticatedInSession = false
    private var backgroundedAtMillis: Long? = null
    private var authenticationRequested = false
    private lateinit var biometricPrompt: BiometricPrompt

    private val deviceIsSecure: Boolean
        get() = (getSystemService(KEYGUARD_SERVICE) as KeyguardManager).isDeviceSecure

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appLockEnabled = securityPreferences.getBoolean(APP_LOCK_ENABLED, true)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        enableEdgeToEdge()
        configureAuthentication()
        contentUnlocked = !deviceIsSecure
        setContent {
            MyFinancesTheme {
                if (contentUnlocked) {
                    MinhasFinancasApp(
                        appLockEnabled = appLockEnabled,
                        onAppLockEnabledChange = ::updateAppLockEnabled,
                    )
                } else LockedContent(onUnlock = ::requestAuthentication)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!appLockEnabled) {
            authenticatedInSession = true
            backgroundedAtMillis = null
            contentUnlocked = true
            return
        }
        val mustAuthenticate = lockPolicy.requiresAuthentication(
            deviceIsSecure = deviceIsSecure,
            authenticatedInSession = authenticatedInSession,
            backgroundedAtMillis = backgroundedAtMillis,
            nowMillis = SystemClock.elapsedRealtime(),
        )
        if (mustAuthenticate) {
            authenticatedInSession = false
            contentUnlocked = false
        } else {
            contentUnlocked = true
            backgroundedAtMillis = null
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        if (!contentUnlocked) requestAuthentication()
    }

    override fun onStop() {
        if (appLockEnabled && !isChangingConfigurations && authenticatedInSession && !authenticationRequested) {
            backgroundedAtMillis = SystemClock.elapsedRealtime()
        }
        super.onStop()
    }

    private fun configureAuthentication() {
        biometricPrompt = BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    authenticationRequested = false
                    authenticatedInSession = true
                    backgroundedAtMillis = null
                    contentUnlocked = true
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    authenticationRequested = false
                    authenticatedInSession = false
                    contentUnlocked = false
                }
            },
        )
    }

    private fun requestAuthentication() {
        if (!appLockEnabled || !deviceIsSecure) {
            authenticatedInSession = true
            contentUnlocked = true
            return
        }
        if (authenticationRequested) return
        authenticationRequested = true
        biometricPrompt.authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("Desbloquear Minhas Finanças")
                .setSubtitle("Use a biometria ou o bloqueio do dispositivo")
                .setAllowedAuthenticators(
                    BiometricManager.Authenticators.BIOMETRIC_WEAK or
                            BiometricManager.Authenticators.DEVICE_CREDENTIAL,
                )
                .build(),
        )
    }

    private fun updateAppLockEnabled(enabled: Boolean) {
        appLockEnabled = enabled
        securityPreferences.edit { putBoolean(APP_LOCK_ENABLED, enabled) }
        if (!enabled) {
            authenticatedInSession = true
            backgroundedAtMillis = null
            contentUnlocked = true
        }
    }

    private companion object {
        const val SECURITY_PREFERENCES = "security_preferences"
        const val APP_LOCK_ENABLED = "app_lock_enabled"
    }
}

@Composable
private fun MinhasFinancasApp(
    appLockEnabled: Boolean,
    onAppLockEnabledChange: (Boolean) -> Unit,
) {
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
                destination?.hasRoute(Invoices::class) == true ||
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
                            icon = { NavigationSymbol(tab.symbol, tab.label) },
                            label = { Text(tab.label) },
                            alwaysShowLabel = true
                        )
                    }
                }
                AppContent(
                    Modifier.weight(1f),
                    navController,
                    appLockEnabled,
                    onAppLockEnabledChange
                )
            }
        } else {
            Scaffold(bottomBar = {
                NavigationBar(
                    modifier = Modifier.height(
                        64.dp + NavigationBarDefaults.windowInsets.asPaddingValues()
                            .calculateBottomPadding(),
                    ),
                ) {
                    MainTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = { navController.navigateToApp(tab) },
                            icon = { NavigationSymbol(tab.symbol, tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }) { padding ->
                AppContent(
                    Modifier
                        .padding(padding)
                        .consumeWindowInsets(padding),
                    navController,
                    appLockEnabled,
                    onAppLockEnabledChange,
                )
            }
        }
    }

}

@Composable
private fun AppContent(
    modifier: Modifier,
    navController: NavHostController,
    appLockEnabled: Boolean,
    onAppLockEnabledChange: (Boolean) -> Unit,
) {
    NavHost(navController, startDestination = Home, modifier = modifier.fillMaxSize()) {
        composable<Home> {
            HomeCompose(
                onNewExpense = { navController.navigate(NewExpense) },
                onFutureAccounts = { navController.navigate(FutureAccounts) },
                onExpenseClick = { expenseId -> navController.navigate(ExpenseDetails(expenseId)) },
            )
        }
        composable<More> {
            MoreCompose(
                onRevenues = { navController.navigate(Revenues) },
                onCards = { navController.navigate(Cards) },
                onInvoices = { navController.navigate(Invoices) },
                onCategories = { navController.navigate(Categories) },
                appLockEnabled = appLockEnabled,
                onAppLockEnabledChange = onAppLockEnabledChange,
            )
        }
        composable<Reports> { ReportsCompose() }
        composable<NewExpense> {
            NewExpenseCompose(
                onBack = navController::popBackStack,
                onSaved = navController::popBackStack,
            )
        }
        composable<FutureAccounts> { FutureCompose() }
        composable<Revenues> { RevenueCompose(onBack = navController::popBackStack) }
        composable<Cards> { CardsCompose(onBack = navController::popBackStack) }
        composable<Invoices> {
            InvoicesCompose(
                onBack = navController::popBackStack,
                onExpenseClick = { navController.navigate(ExpenseDetails(it)) },
            )
        }
        composable<Categories> { CategoriesCompose(onBack = navController::popBackStack) }
        composable<ExpenseDetails> { entry ->
            val destination = entry.toRoute<ExpenseDetails>()
            ExpenseDetailsCompose(
                expenseId = destination.expenseId,
                onBack = navController::popBackStack,
                onDeleted = navController::popBackStack,
            )
        }
    }
}

@Composable
private fun NavigationSymbol(symbol: ImageVector, label: String) {
    Icon(
        imageVector = symbol,
        contentDescription = label,
    )
}
