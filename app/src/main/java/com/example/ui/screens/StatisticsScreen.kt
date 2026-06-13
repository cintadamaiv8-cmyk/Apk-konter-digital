package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.viewmodel.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(viewModel: StoreViewModel, navController: androidx.navigation.NavController) {
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    
    val totalTransactions = allTransactions.size
    val totalCustomers = allTransactions.map { it.customerPhone }.distinct().size
    val totalSales = allTransactions.sumOf { it.totalAmount }
    val totalProfit = allTransactions.sumOf { it.profit }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistik", color = NebulaCyan, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = NebulaCyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        },
        containerColor = VoidBlack
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SummaryCard(
                title = "Total Transaksi",
                value = totalTransactions.toString(),
                modifier = Modifier.fillMaxWidth()
            )
            SummaryCard(
                title = "Total Pelanggan",
                value = totalCustomers.toString(),
                modifier = Modifier.fillMaxWidth()
            )
            SummaryCard(
                title = "Total Penjualan",
                value = formatCurrency(totalSales),
                modifier = Modifier.fillMaxWidth()
            )
            SummaryCard(
                title = "Total Keuntungan",
                value = formatCurrency(totalProfit),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            // Pseudo chart structure as requested "Grafik sederhana bertema Cosmic Dark"
            // We can just draw basic bars using Box
            if (allTransactions.isEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Belum ada data statistik", color = StarlightSilver, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            } else {
                Text("Grafik Penjualan", color = StarlightSilver, style = MaterialTheme.typography.titleMedium)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
                ) {
                    val grouped = allTransactions.groupBy { 
                        val cal = java.util.Calendar.getInstance()
                        cal.timeInMillis = it.timestamp
                        val d = cal.get(java.util.Calendar.DAY_OF_MONTH)
                        val m = cal.get(java.util.Calendar.MONTH) + 1
                        "$d/$m"
                    }
                    val dailyTotals = grouped.mapValues { entry -> entry.value.sumOf { it.totalAmount } }
                    val maxSales = dailyTotals.values.maxOrNull() ?: 1.0
                    val maxS = if (maxSales <= 0) 1.0 else maxSales
                    
                    Row(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = androidx.compose.ui.Alignment.Bottom
                    ) {
                        dailyTotals.toList().takeLast(7).forEach { (dayStr, total) ->
                            val h = (total / maxS).toFloat().coerceIn(0f, 1f)
                            Column(
                                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f).fillMaxHeight()
                            ) {
                                Surface(
                                    color = GalaxyBlue,
                                    modifier = Modifier
                                        .width(30.dp)
                                        .fillMaxHeight(h.coerceAtLeast(0.01f)),
                                    shape = MaterialTheme.shapes.small
                                ) {}
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(dayStr, color = StarlightSilver, style = MaterialTheme.typography.bodySmall, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
