package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {
    // Stock queries
    @Query("SELECT * FROM stocks ORDER BY name ASC")
    fun getAllStocks(): Flow<List<Stock>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStock(stock: Stock)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionRaw(transaction: TransactionEntity)

    @Update
    suspend fun updateStock(stock: Stock)

    @Delete
    suspend fun deleteStock(stock: Stock)

    @Query("SELECT * FROM stocks WHERE id = :stockId LIMIT 1")
    suspend fun getStockById(stockId: Int): Stock?

    // Transaction queries
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getTransactionsBetween(startTime: Long, endTime: Long): Flow<List<TransactionEntity>>

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity): Long
    
    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)
    
    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()

    @Query("DELETE FROM stocks")
    suspend fun clearStocks()
    @Query("SELECT * FROM debts ORDER BY timestamp DESC")
    fun getAllDebts(): Flow<List<DebtEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: DebtEntity)

    @Update
    suspend fun updateDebt(debt: DebtEntity)

    @Delete
    suspend fun deleteDebt(debt: DebtEntity)

    @Query("DELETE FROM debts")
    suspend fun clearDebts()
}
