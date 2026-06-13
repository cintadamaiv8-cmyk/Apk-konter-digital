package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val customerName: String,
    val customerPhone: String,
    val totalAmount: Double,
    val amountPaid: Double,
    val changeAmount: Double,
    val itemsJson: String, // Storing items as JSON string for simplicity
    val profit: Double
)

// Simple class not stored as a separate table
data class CartItem(
    val stockId: Int,
    val name: String,
    val price: Double,
    val costPrice: Double,
    val quantity: Int,
    val subtotal: Double
)
