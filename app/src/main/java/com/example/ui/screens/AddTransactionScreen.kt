package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import com.example.data.CartItem
import com.example.data.Converters
import com.example.data.Stock
import com.example.data.TransactionEntity
import com.example.ui.theme.*
import com.example.viewmodel.StoreViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(viewModel: StoreViewModel, navController: NavController) {
    val context = LocalContext.current
    val stocks by viewModel.allStocks.collectAsStateWithLifecycle()
    
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var amountPaidStr by remember { mutableStateOf("") }
    var recordAsDebt by remember { mutableStateOf(false) }
    
    val cartItems = remember { mutableStateListOf<CartItem>() }
    
    var showStockPicker by remember { mutableStateOf(false) }
    
    val totalAmount = cartItems.sumOf { it.subtotal }
    val amountPaid = amountPaidStr.toDoubleOrNull() ?: 0.0
    val change = amountPaid - totalAmount

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaksi Baru", color = NebulaCyan) },
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
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Nama Pelanggan", color = StarlightSilver) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = defaultTextFieldColors()
                    )
                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("Nomor HP", color = StarlightSilver) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        colors = defaultTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
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
                                IconButton(onClick = { cartItems.remove(item) }) {
                                    Icon(Icons.Filled.Delete, "Hapus", tint = DangerRed)
                                }
                            }
                        }
                    }
                }
                
                item {
                    Button(
                        onClick = { showStockPicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark, contentColor = NebulaCyan)
                    ) {
                        Text("+ Tambah Item")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = SurfaceDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Transaksi", color = StarlightSilver, fontWeight = FontWeight.Bold)
                        Text(formatCurrency(totalAmount), color = NebulaCyan, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = amountPaidStr,
                        onValueChange = { amountPaidStr = it },
                        label = { Text("Uang Pelanggan", color = StarlightSilver) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = defaultTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(if (change >= 0) "Kembalian" else "Kurang", color = StarlightSilver)
                        Text(
                            formatCurrency(kotlin.math.abs(change)),
                            color = if (change >= 0) SuccessGreen else DangerRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (change < 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().clickable { recordAsDebt = !recordAsDebt }
                        ) {
                            Checkbox(
                                checked = recordAsDebt,
                                onCheckedChange = { recordAsDebt = it },
                                colors = CheckboxDefaults.colors(checkedColor = CosmicPurple, checkmarkColor = VoidBlack)
                            )
                            Text("Catat Sebagai Hutang", color = StarlightSilver)
                        }
                    }
                }
            }
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val text = generateShareText(customerName, customerPhone, cartItems, totalAmount, amountPaid, change)
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            putExtra(Intent.EXTRA_TEXT, text)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GalaxyBlue),
                    enabled = cartItems.isNotEmpty()
                ) {
                    Icon(Icons.Filled.Share, "Bagikan", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Bagikan", color = StarlightSilver)
                }

                Button(
                    onClick = {
                        viewModel.processTransaction(customerName, customerPhone, amountPaid, cartItems, recordAsDebt) {
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicPurple),
                    enabled = cartItems.isNotEmpty() && (amountPaid >= totalAmount || recordAsDebt) && customerName.isNotBlank()
                ) {
                    Text("Simpan", color = VoidBlack, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showStockPicker) {
        StockPickerDialog(
            stocks = stocks,
            onDismiss = { showStockPicker = false },
            onSelect = { stock, qty ->
                cartItems.add(
                    CartItem(
                        stockId = stock.id,
                        name = stock.name,
                        price = stock.sellingPrice,
                        costPrice = stock.costPrice,
                        quantity = qty,
                        subtotal = stock.sellingPrice * qty
                    )
                )
                showStockPicker = false
            }
        )
    }
}

@Composable
fun StockPickerDialog(stocks: List<Stock>, onDismiss: () -> Unit, onSelect: (Stock, Int) -> Unit) {
    var selectedStock by remember { mutableStateOf<Stock?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilih Produk", color = NebulaCyan) },
        text = {
            Column {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(stocks) { stock ->
                        Text(
                            "${stock.name} - ${formatCurrency(stock.sellingPrice)}",
                            color = StarlightSilver,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(stock, 1) }
                                .padding(vertical = 12.dp)
                        )
                        HorizontalDivider(color = SurfaceDark)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = StarlightSilver)
            }
        },
        containerColor = SurfaceDark
    )
}

fun generateShareText(
    name: String, 
    phone: String, 
    items: List<CartItem>, 
    total: Double, 
    paid: Double, 
    change: Double
): String {
    val sb = StringBuilder()
    sb.append("SHINFOX STORE ULTRA X\n\n")
    sb.append("Nama Pelanggan: $name\n")
    if (phone.isNotBlank()) sb.append("Nomor HP: $phone\n")
    sb.append("\nProduk:\n")
    for (item in items) {
        // e.g. Pulsa 10K
        if (item.quantity > 1) {
            sb.append("${item.name} x${item.quantity}\n")
        } else {
            sb.append("${item.name}\n")
        }
    }
    sb.append("\nTotal: ${formatCurrency(total)}\n")
    sb.append("Bayar: ${formatCurrency(paid)}\n")
    sb.append("Kembalian: ${formatCurrency(change)}\n\n")
    
    val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
    sb.append("Tanggal: ${sdf.format(Date())}")
    return sb.toString()
}
