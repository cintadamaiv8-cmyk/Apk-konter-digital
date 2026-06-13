package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ui.theme.*

@Composable
fun AppBottomNavigation(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = SurfaceDark,
        contentColor = StarlightSilver
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Dashboard") },
            label = { Text("Dashboard") },
            selected = currentRoute == "dashboard",
            onClick = {
                if (currentRoute != "dashboard") {
                    navController.navigate("dashboard") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = VoidBlack,
                selectedTextColor = CosmicPurple,
                indicatorColor = CosmicPurple,
                unselectedIconColor = StarlightSilver,
                unselectedTextColor = StarlightSilver
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.List, contentDescription = "Transaksi") },
            label = { Text("Transaksi") },
            selected = currentRoute == "transactions",
            onClick = {
                if (currentRoute != "transactions") {
                    navController.navigate("transactions") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = VoidBlack,
                selectedTextColor = CosmicPurple,
                indicatorColor = CosmicPurple,
                unselectedIconColor = StarlightSilver,
                unselectedTextColor = StarlightSilver
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Inventory, contentDescription = "Produk") },
            label = { Text("Produk") },
            selected = currentRoute == "stock",
            onClick = {
                if (currentRoute != "stock") {
                    navController.navigate("stock") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = VoidBlack,
                selectedTextColor = CosmicPurple,
                indicatorColor = CosmicPurple,
                unselectedIconColor = StarlightSilver,
                unselectedTextColor = StarlightSilver
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.BarChart, contentDescription = "Statistik") },
            label = { Text("Statistik") },
            selected = currentRoute == "statistics",
            onClick = {
                if (currentRoute != "statistics") {
                    navController.navigate("statistics") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = VoidBlack,
                selectedTextColor = CosmicPurple,
                indicatorColor = CosmicPurple,
                unselectedIconColor = StarlightSilver,
                unselectedTextColor = StarlightSilver
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.AccountBalanceWallet, contentDescription = "Hutang") },
            label = { Text("Hutang") },
            selected = currentRoute == "debts",
            onClick = {
                if (currentRoute != "debts") {
                    navController.navigate("debts") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = VoidBlack,
                selectedTextColor = CosmicPurple,
                indicatorColor = CosmicPurple,
                unselectedIconColor = StarlightSilver,
                unselectedTextColor = StarlightSilver
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Pengaturan") },
            label = { Text("Pengaturan") },
            selected = currentRoute == "settings",
            onClick = {
                if (currentRoute != "settings") {
                    navController.navigate("settings") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = VoidBlack,
                selectedTextColor = CosmicPurple,
                indicatorColor = CosmicPurple,
                unselectedIconColor = StarlightSilver,
                unselectedTextColor = StarlightSilver
            )
        )
    }
}
