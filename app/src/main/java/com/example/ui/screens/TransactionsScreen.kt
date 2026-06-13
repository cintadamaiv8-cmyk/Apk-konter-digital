package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.TransactionEntity
import com.example.ui.theme.*
import com.example.viewmodel.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(viewModel: StoreViewModel, navController: NavController) {
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    
    var searchQuery by remember { mutableStateOf("") }
    
    var transactionToDelete by remember { mutableStateOf<TransactionEntity?>(null) }
    
    val filteredTransactions = allTransactions.filter { 
        it.customerName.contains(searchQuery, ignoreCase = true) || 
        it.customerPhone.contains(searchQuery, ignoreCase = true) ||
        it.itemsJson.contains(searchQuery, ignoreCase = true) // simple way to search by product name
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Transaksi", color = NebulaCyan, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = NebulaCyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_transaction") },
                containerColor = CosmicPurple,
                contentColor = VoidBlack
            ) {
                Icon(Icons.Filled.Add, "Tambah Transaksi")
            }
        },
        containerColor = VoidBlack
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cari Pelanggan / HP / Produk", color = StarlightSilver) },
                modifier = Modifier.fillMaxWidth(),
                colors = defaultTextFieldColors()
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredTransactions) { tx ->
                    TransactionCard(
                        tx = tx, 
                        onClick = {
                            navController.navigate("transaction_detail/${tx.id}")
                        },
                        onDelete = {
                            transactionToDelete = tx
                        }
                    )
                }
                if (filteredTransactions.isEmpty()) {
                    item {
                        Text(
                            "Belum ada transaksi.",
                            color = StarlightSilver,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
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
