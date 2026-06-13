package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.TransactionEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionCard(tx: TransactionEntity, onClick: () -> Unit, onDelete: (() -> Unit)? = null) {
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
                    Text(text = tx.customerName, color = StarlightSilver, fontWeight = FontWeight.Bold)
                    if (tx.customerPhone.isNotBlank()) {
                        Text(text = tx.customerPhone, color = StarlightSilver, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = formatCurrency(tx.totalAmount), color = NebulaCyan, fontWeight = FontWeight.Bold)
                    if (onDelete != null) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(24.dp).padding(top = 8.dp)
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete Transaction", tint = DangerRed)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = formatDate(tx.timestamp), color = StarlightSilver, style = MaterialTheme.typography.bodySmall)
                Text(text = "Keuntungan: ${formatCurrency(tx.profit)}", color = SuccessGreen, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale("id", "ID"))
    return sdf.format(Date(timestamp))
}
