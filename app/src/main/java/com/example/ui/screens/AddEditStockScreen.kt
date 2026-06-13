package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.Stock
import com.example.ui.theme.*
import com.example.viewmodel.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditStockScreen(
    viewModel: StoreViewModel,
    navController: NavController,
    stockId: Int? = null
) {
    val stocks by viewModel.allStocks.collectAsStateWithLifecycle()
    val existingStock = stocks.find { it.id == stockId }

    var name by remember { mutableStateOf(existingStock?.name ?: "") }
    var costPrice by remember { mutableStateOf(existingStock?.costPrice?.toString() ?: "") }
    var sellingPrice by remember { mutableStateOf(existingStock?.sellingPrice?.toString() ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (stockId == null) "Tambah Produk" else "Edit Produk", color = NebulaCyan) },
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
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Produk", color = StarlightSilver) },
                modifier = Modifier.fillMaxWidth(),
                colors = defaultTextFieldColors()
            )
            OutlinedTextField(
                value = costPrice,
                onValueChange = { costPrice = it },
                label = { Text("Harga Modal", color = StarlightSilver) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = defaultTextFieldColors()
            )
            OutlinedTextField(
                value = sellingPrice,
                onValueChange = { sellingPrice = it },
                label = { Text("Harga Jual", color = StarlightSilver) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = defaultTextFieldColors()
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val cp = costPrice.toDoubleOrNull() ?: 0.0
                    val sp = sellingPrice.toDoubleOrNull() ?: 0.0
                    
                    if (stockId == null) {
                        viewModel.addStock(name, cp, sp)
                    } else {
                        viewModel.updateStock(Stock(stockId, name, cp, sp))
                    }
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CosmicPurple),
                enabled = name.isNotBlank() && costPrice.isNotBlank() && sellingPrice.isNotBlank()
            ) {
                Text("Simpan", color = VoidBlack, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun defaultTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = NebulaCyan,
    unfocusedBorderColor = StarlightSilver,
    focusedTextColor = StarlightSilver,
    unfocusedTextColor = StarlightSilver,
    cursorColor = NebulaCyan
)
