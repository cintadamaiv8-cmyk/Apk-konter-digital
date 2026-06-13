package com.example.data

import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class StoreRepository(private val storeDao: StoreDao) {
    val allStocks: Flow<List<Stock>> = storeDao.getAllStocks()
    val allTransactions: Flow<List<TransactionEntity>> = storeDao.getAllTransactions()
    val allDebts: Flow<List<DebtEntity>> = storeDao.getAllDebts()

    suspend fun insertDebt(debt: DebtEntity) {
        storeDao.insertDebt(debt)
    }

    suspend fun updateDebt(debt: DebtEntity) {
        storeDao.updateDebt(debt)
    }

    suspend fun deleteDebt(debt: DebtEntity) {
        storeDao.deleteDebt(debt)
    }

    fun getTodayTransactions(): Flow<List<TransactionEntity>> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.timeInMillis
        
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val endTime = calendar.timeInMillis - 1
        
        return storeDao.getTransactionsBetween(startTime, endTime)
    }

    suspend fun insertStock(stock: Stock) {
        storeDao.insertStock(stock)
    }

    suspend fun insertTransactionRaw(transaction: TransactionEntity) {
        storeDao.insertTransactionRaw(transaction)
    }

    suspend fun updateStock(stock: Stock) {
        storeDao.updateStock(stock)
    }

    suspend fun deleteStock(stock: Stock) {
        storeDao.deleteStock(stock)
    }

    suspend fun processTransaction(
        customerName: String,
        customerPhone: String,
        amountPaid: Double,
        cartItems: List<CartItem>
    ): Long {
        var totalAmount = 0.0
        var totalProfit = 0.0
        
        for (item in cartItems) {
            totalAmount += item.subtotal
            // profit = subtotal - (costPrice * quantity)
            val costSum = item.costPrice * item.quantity
            totalProfit += (item.subtotal - costSum)
        }
        
        val change = amountPaid - totalAmount
        
        val converters = Converters()
        val itemsJson = converters.fromCartItemList(cartItems)
        
        val transaction = TransactionEntity(
            customerName = customerName,
            customerPhone = customerPhone,
            totalAmount = totalAmount,
            amountPaid = amountPaid,
            changeAmount = change,
            itemsJson = itemsJson,
            profit = totalProfit
        )
        return storeDao.insertTransaction(transaction)
    }

    suspend fun resetAppData() {
        storeDao.clearTransactions()
        storeDao.clearStocks()
        storeDao.clearDebts()
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        storeDao.deleteTransaction(transaction)
    }
}
