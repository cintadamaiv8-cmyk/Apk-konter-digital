package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.ui.theme.*
import com.example.viewmodel.StoreViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtDetailScreen(viewModel: StoreViewModel, navController: NavController, debtId: Int) {
    val context = LocalContext.current
    val allDebts by viewModel.allDebts.collectAsStateWithLifecycle()
    val debt = allDebts.find { it.id == debtId }
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf(false) }

    if (debt == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Data hutang tidak ditemukan", color = StarlightSilver)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Hutang", color = NebulaCyan, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = NebulaCyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark),
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Filled.Delete, "Hapus", tint = DangerRed)
                    }
                }
            )
        },
        containerColor = VoidBlack
    ) { padding ->
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
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Nama", color = StarlightSilver)
                            Text(debt.customerName, color = StarlightSilver, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Nomor HP", color = StarlightSilver)
                            Text(debt.customerPhone.ifBlank { "-" }, color = StarlightSilver, fontWeight = FontWeight.Bold)
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
                        Text("Ringkasan Hutang", color = NebulaCyan, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Tanggal", color = StarlightSilver)
                            Text(formatDate(debt.timestamp), color = StarlightSilver)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Hutang", color = StarlightSilver)
                            Text(formatCurrency(debt.totalDebt), color = StarlightSilver, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Dibayar", color = StarlightSilver)
                            Text(formatCurrency(debt.totalPaid), color = StarlightSilver, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = SurfaceCardBorder)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Sisa Hutang", color = StarlightSilver, fontWeight = FontWeight.Bold)
                            Text(formatCurrency(debt.remainingDebt), color = NebulaCyan, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Status", color = StarlightSilver, fontWeight = FontWeight.Bold)
                            Text(
                                debt.statusText,
                                color = if (debt.isPaid) SuccessGreen else DangerRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (debt.notes.isNotBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Keterangan", color = NebulaCyan, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(debt.notes, color = StarlightSilver)
                        }
                    }
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("id", "ID"))
                            val dateStr = sdf.format(Date(debt.timestamp))
                            val textStr = """
                                SHINFOX STORE ULTRA X
                                
                                DATA HUTANG
                                
                                Nama Pelanggan:
                                ${debt.customerName}
                                
                                Nomor HP:
                                ${debt.customerPhone.ifBlank { "-" }}
                                
                                Total Hutang:
                                ${formatCurrency(debt.totalDebt)}
                                
                                Total Dibayar:
                                ${formatCurrency(debt.totalPaid)}
                                
                                Sisa Hutang:
                                ${formatCurrency(debt.remainingDebt)}
                                
                                Status:
                                ${debt.statusText}
                                
                                Tanggal:
                                $dateStr
                                
                                ${debt.notes.ifBlank { "-" }}
                            """.trimIndent()

                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                putExtra(Intent.EXTRA_TEXT, textStr)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, null))
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GalaxyBlue)
                    ) {
                        Icon(Icons.Filled.Share, "Bagikan", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Bagikan")
                    }

                    if (!debt.isPaid) {
                        Button(
                            onClick = { showPaymentDialog = true },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                        ) {
                            Text("Bayar Cicilan", color = VoidBlack, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Hutang", color = DangerRed) },
            text = { Text("Yakin ingin menghapus data hutang ini?", color = StarlightSilver) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteDebt(debt)
                    showDeleteDialog = false
                    navController.popBackStack()
                }) {
                    Text("Hapus", color = DangerRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal", color = NebulaCyan)
                }
            },
            containerColor = SurfaceDark
        )
    }

    if (showPaymentDialog) {
        var paymentAmountStr by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showPaymentDialog = false },
            title = { Text("Bayar Cicilan", color = NebulaCyan) },
            text = {
                Column {
                    Text("Sisa Hutang: ${formatCurrency(debt.remainingDebt)}", color = StarlightSilver, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = paymentAmountStr,
                        onValueChange = { paymentAmountStr = it },
                        label = { Text("Nominal Pembayaran") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = defaultTextFieldColors()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val amount = paymentAmountStr.toDoubleOrNull() ?: 0.0
                    if (amount > 0.0) {
                        viewModel.payDebt(debt, amount)
                        showPaymentDialog = false
                    }
                }) {
                    Text("Simpan", color = NebulaCyan)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPaymentDialog = false }) {
                    Text("Batal", color = StarlightSilver)
                }
            },
            containerColor = SurfaceDark
        )
    }
}
