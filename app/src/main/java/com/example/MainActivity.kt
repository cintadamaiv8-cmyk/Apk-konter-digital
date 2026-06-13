package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.AppDatabase
import com.example.data.StoreRepository
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.StoreViewModel
import com.example.viewmodel.StoreViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = AppDatabase.getDatabase(this)
        val repository = StoreRepository(database.storeDao())
        val factory = StoreViewModelFactory(repository)

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val viewModel: StoreViewModel = viewModel(factory = factory)

                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") {
                        SplashScreen(onNavigateToDashboard = {
                            navController.navigate("main") {
                                popUpTo("splash") { inclusive = true }
                            }
                        })
                    }
                    composable("main") {
                        val mainNavController = rememberNavController()
                        Scaffold(
                            bottomBar = { AppBottomNavigation(mainNavController) }
                        ) { padding ->
                            NavHost(
                                navController = mainNavController,
                                startDestination = "dashboard",
                                modifier = Modifier.padding(padding)
                            ) {
                                composable("dashboard") { 
                                    DashboardScreen(
                                        viewModel = viewModel, 
                                        onNavigateToTransaction = { txId -> mainNavController.navigate("transaction_detail/$txId") }
                                    ) 
                                }
                                composable("transactions") { TransactionsScreen(viewModel, mainNavController) }
                                composable("stock") { StockScreen(viewModel, mainNavController) }
                                composable("statistics") { StatisticsScreen(viewModel, mainNavController) }
                                composable("settings") { SettingsScreen(viewModel, mainNavController) }
                                
                                // Sub-screens defined here so bottom nav is preserved or you can put them in root
                                composable("add_stock") { AddEditStockScreen(viewModel, mainNavController) }
                                composable(
                                    "edit_stock/{stockId}",
                                    arguments = listOf(navArgument("stockId") { type = NavType.IntType })
                                ) { backStackEntry ->
                                    val stockId = backStackEntry.arguments?.getInt("stockId")
                                    AddEditStockScreen(viewModel, mainNavController, stockId)
                                }
                                composable("add_transaction") { AddTransactionScreen(viewModel, mainNavController) }
                                composable(
                                    "transaction_detail/{txId}",
                                    arguments = listOf(navArgument("txId") { type = NavType.IntType })
                                ) { backStackEntry ->
                                    val txId = backStackEntry.arguments?.getInt("txId") ?: 0
                                    TransactionDetailScreen(viewModel, mainNavController, txId)
                                }
                                composable("debts") { DebtScreen(viewModel, mainNavController) }
                                composable(
                                    "debt_detail/{debtId}",
                                    arguments = listOf(navArgument("debtId") { type = NavType.IntType })
                                ) { backStackEntry ->
                                    val debtId = backStackEntry.arguments?.getInt("debtId") ?: 0
                                    DebtDetailScreen(viewModel, mainNavController, debtId)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
