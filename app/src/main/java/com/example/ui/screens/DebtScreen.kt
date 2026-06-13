package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.DebtEntity
import com.example.ui.theme.*
import com.example.viewmodel.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtScreen(viewModel: StoreViewModel, navController: NavController) {
    val allDebts by viewModel.allDebts.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredDebts = allDebts.filter { 
        it.customerName.contains(searchQuery, ignoreCase = true) || 
        it.customerPhone.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Data Hutang", color = NebulaCyan, fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cari Pelanggan", color = StarlightSilver) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", tint = StarlightSilver) },
                modifier = Modifier.fillMaxWidth(),
                colors = defaultTextFieldColors(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredDebts) { debt ->
                    DebtCard(debt = debt, onClick = { navController.navigate("debt_detail/${debt.id}") })
                }
                if (filteredDebts.isEmpty()) {
                    item {
                        Text(
                            "Belum ada data hutang",
                            color = StarlightSilver,
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DebtCard(debt: DebtEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = debt.customerName, color = StarlightSilver, fontWeight = FontWeight.Bold)
                    if (debt.customerPhone.isNotBlank()) {
                        Text(text = debt.customerPhone, color = StarlightSilver, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = formatCurrency(debt.remainingDebt), color = NebulaCyan, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = debt.statusText,
                        color = if (debt.isPaid) SuccessGreen else DangerRed,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
