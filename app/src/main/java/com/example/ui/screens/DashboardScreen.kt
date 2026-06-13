package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.content.Context
import com.example.data.TransactionEntity
import com.example.viewmodel.StoreViewModel
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: StoreViewModel, onNavigateToTransaction: (Int) -> Unit = {}) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("store_prefs", Context.MODE_PRIVATE) }
    var storeName by remember { mutableStateOf(sharedPreferences.getString("store_name", "GALAXY STORE") ?: "GALAXY STORE") }
    var isEditingStoreName by remember { mutableStateOf(false) }
    var transactionToDelete by remember { mutableStateOf<TransactionEntity?>(null) }

    val todayTransactions by viewModel.todayTransactions.collectAsStateWithLifecycle()
    val allDebts by viewModel.allDebts.collectAsStateWithLifecycle()
    
    val totalBalance = todayTransactions.sumOf { it.totalAmount }
    val totalProfit = todayTransactions.sumOf { it.profit }
    val totalCustomers = todayTransactions.map { it.customerPhone }.distinct().size

    val activeDebts = allDebts.filter { !it.isPaid }
    val totalPiutang = activeDebts.sumOf { it.remainingDebt }
    val totalCustomerBerhutang = activeDebts.distinctBy { it.customerName }.size
    val totalHutangLunas = allDebts.count { it.isPaid }

    Scaffold(
        topBar = {
            // Replaced with custom header
        },
        containerColor = VoidBlack
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "SHINFOX STORE",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 24.sp,
                                    letterSpacing = 2.sp,
                                    brush = Brush.linearGradient(colors = listOf(CosmicPurple, NebulaCyan))
                                )
                            )
                            Text(
                                text = "ULTRA X",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    letterSpacing = 6.sp,
                                    color = StarlightSilver
                                )
                            )
                        }
                        
                        Image(
                            painter = painterResource(id = com.example.R.drawable.img_app_logo_1781377515277),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isEditingStoreName) {
                            OutlinedTextField(
                                value = storeName,
                                onValueChange = { storeName = it },
                                modifier = Modifier.weight(1f),
                                textStyle = TextStyle(color = StarlightSilver, fontWeight = FontWeight.Bold),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    sharedPreferences.edit().putString("store_name", storeName).apply()
                                    isEditingStoreName = false
                                }),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NebulaCyan,
                                    unfocusedBorderColor = CosmicPurple
                                )
                            )
                            IconButton(onClick = {
                                sharedPreferences.edit().putString("store_name", storeName).apply()
                                isEditingStoreName = false
                            }) {
                                Icon(Icons.Filled.Check, contentDescription = "Simpan Nama Toko", tint = SuccessGreen)
                            }
                        } else {
                            Text(
                                text = storeName,
                                style = MaterialTheme.typography.titleLarge,
                                color = NebulaCyan,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { isEditingStoreName = true }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit Nama Toko", tint = StarlightSilver)
                            }
                        }
                    }
                }
            }

            item {
                Text("Ringkasan Hari Ini", style = MaterialTheme.typography.titleMedium, color = StarlightSilver)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SummaryCard(
                        title = "Saldo",
                        value = formatCurrency(totalBalance),
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = "Keuntungan",
                        value = formatCurrency(totalProfit),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SummaryCard(
                        title = "Transaksi",
                        value = todayTransactions.size.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = "Pelanggan",
                        value = totalCustomers.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Ringkasan Hutang (Piutang)", style = MaterialTheme.typography.titleMedium, color = StarlightSilver)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SummaryCard(
                        title = "Total Piutang",
                        value = formatCurrency(totalPiutang),
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = "Orang Berhutang",
                        value = totalCustomerBerhutang.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                SummaryCard(
                    title = "Hutang Lunas",
                    value = totalHutangLunas.toString(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Aktivitas Terbaru", style = MaterialTheme.typography.titleMedium, color = StarlightSilver)
            }
            
            items(todayTransactions.take(5)) { tx ->
                TransactionCard(
                    tx = tx, 
                    onClick = { onNavigateToTransaction(tx.id) },
                    onDelete = { transactionToDelete = tx }
                )
            }
            
            if (todayTransactions.isEmpty()) {
                item {
                    Text(
                        "Belum ada transaksi hari ini.",
                        color = StarlightSilver,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }
        }
    }

    if (transactionToDelete != null) {
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Hapus Transaksi", color = DangerRed) },
            text = { Text("Yakin ingin menghapus transaksi ini?", color = StarlightSilver) },
            confirmButton = {
                TextButton(onClick = {
                    transactionToDelete?.let { viewModel.deleteTransaction(it) }
                    transactionToDelete = null
                }) {
                    Text("Hapus", color = DangerRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) {
                    Text("Batal", color = NebulaCyan)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun SummaryCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, color = StarlightSilver, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, color = NebulaCyan, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount)
}
