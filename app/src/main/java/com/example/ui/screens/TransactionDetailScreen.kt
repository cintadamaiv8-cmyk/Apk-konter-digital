package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.Converters
import com.example.ui.theme.*
import com.example.viewmodel.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(viewModel: StoreViewModel, navController: NavController, txId: Int) {
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val tx = allTransactions.find { it.id == txId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Transaksi", color = NebulaCyan) },
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
        if (tx == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Transaksi tidak ditemukan", color = DangerRed)
            }
            return@Scaffold
        }

        val cartItems = Converters().toCartItemList(tx.itemsJson)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Info Pelanggan", color = NebulaCyan, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Nama: ${tx.customerName}", color = StarlightSilver)
                        if (tx.customerPhone.isNotBlank()) {
                            Text("No HP: ${tx.customerPhone}", color = StarlightSilver)
                        }
                        Text("Tanggal: ${formatDate(tx.timestamp)}", color = StarlightSilver)
                    }
                }
            }
            item {
                Text("Daftar Item", color = StarlightSilver, style = MaterialTheme.typography.titleMedium)
            }
            items(cartItems) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(item.name, color = StarlightSilver, fontWeight = FontWeight.Bold)
                            Text(formatCurrency(item.price), color = StarlightSilver, style = MaterialTheme.typography.bodySmall)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(formatCurrency(item.subtotal), color = NebulaCyan, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total", color = StarlightSilver, fontWeight = FontWeight.Bold)
                            Text(formatCurrency(tx.totalAmount), color = NebulaCyan, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Bayar", color = StarlightSilver)
                            Text(formatCurrency(tx.amountPaid), color = StarlightSilver)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Kembalian", color = StarlightSilver)
                            Text(formatCurrency(tx.changeAmount.coerceAtLeast(0.0)), color = if (tx.changeAmount >= 0) SuccessGreen else DangerRed)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = SurfaceCardBorder)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Keuntungan", color = StarlightSilver, fontWeight = FontWeight.Bold)
                            Text(formatCurrency(tx.profit), color = SuccessGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
