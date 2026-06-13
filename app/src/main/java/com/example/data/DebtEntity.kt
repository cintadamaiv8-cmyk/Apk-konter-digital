package com.example.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val transactionId: Int = 0,
    val customerName: String,
    val customerPhone: String,
    val totalDebt: Double,
    val totalPaid: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
) {
    @Ignore
    val remainingDebt: Double = totalDebt - totalPaid
    
    @Ignore
    val isPaid: Boolean = remainingDebt <= 0.0

    @Ignore
    val statusText: String = if (isPaid) "Lunas" else "Belum Lunas"
}
