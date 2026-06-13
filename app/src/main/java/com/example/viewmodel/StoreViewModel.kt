package com.example.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CartItem
import com.example.data.StoreRepository
import com.example.data.Stock
import com.example.data.TransactionEntity
import com.example.data.DebtEntity
import com.example.data.Converters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.io.OutputStream

class StoreViewModel(private val repository: StoreRepository) : ViewModel() {
    val allStocks = repository.allStocks.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    
    val allTransactions = repository.allTransactions.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val todayTransactions = repository.getTodayTransactions().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun addStock(name: String, costPrice: Double, sellingPrice: Double) {
        viewModelScope.launch {
            repository.insertStock(Stock(name = name, costPrice = costPrice, sellingPrice = sellingPrice))
        }
    }

    fun updateStock(stock: Stock) {
        viewModelScope.launch {
            repository.updateStock(stock)
        }
    }

    fun deleteStock(stock: Stock) {
        viewModelScope.launch {
            repository.deleteStock(stock)
        }
    }

    val allDebts = repository.allDebts.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun payDebt(debt: DebtEntity, amount: Double) {
        viewModelScope.launch {
            val updated = debt.copy(totalPaid = debt.totalPaid + amount)
            repository.updateDebt(updated)
        }
    }
    
    fun updateDebt(debt: DebtEntity) {
        viewModelScope.launch {
            repository.updateDebt(debt)
        }
    }

    fun deleteDebt(debt: DebtEntity) {
        viewModelScope.launch {
            repository.deleteDebt(debt)
        }
    }

    fun processTransaction(customerName: String, customerPhone: String, amountPaid: Double, cartItems: List<CartItem>, recordAsDebt: Boolean, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val txId = repository.processTransaction(customerName, customerPhone, amountPaid, cartItems)
            if (recordAsDebt) {
                var totalAmount = 0.0
                cartItems.forEach { totalAmount += it.subtotal }
                if (amountPaid < totalAmount) {
                    val debtAmount = totalAmount - amountPaid
                    val productsStr = cartItems.joinToString("\n") { "- ${it.name}" }
                    repository.insertDebt(DebtEntity(
                        transactionId = txId.toInt(),
                        customerName = customerName,
                        customerPhone = customerPhone,
                        totalDebt = debtAmount,
                        totalPaid = 0.0,
                        notes = "Produk Transaksi:\n$productsStr"
                    ))
                }
            }
            onSuccess()
        }
    }

    fun resetAppData(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.resetAppData()
            onComplete()
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            val debt = allDebts.value.find { it.transactionId == transaction.id }
            if (debt != null) {
                repository.deleteDebt(debt)
            }
        }
    }

    fun exportData(context: Context, uri: Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val stocks = allStocks.value
                val transactions = allTransactions.value
                val debts = allDebts.value

                val jsonObject = JSONObject()
                val stocksArray = JSONArray()
                stocks.forEach { s ->
                    val obj = JSONObject()
                    obj.put("id", s.id)
                    obj.put("name", s.name)
                    obj.put("costPrice", s.costPrice)
                    obj.put("sellingPrice", s.sellingPrice)
                    stocksArray.put(obj)
                }
                jsonObject.put("stocks", stocksArray)

                val txArray = JSONArray()
                transactions.forEach { t ->
                    val obj = JSONObject()
                    obj.put("id", t.id)
                    obj.put("customerName", t.customerName)
                    obj.put("customerPhone", t.customerPhone)
                    obj.put("totalAmount", t.totalAmount)
                    obj.put("amountPaid", t.amountPaid)
                    obj.put("changeAmount", t.changeAmount)
                    obj.put("itemsJson", t.itemsJson)
                    obj.put("profit", t.profit)
                    obj.put("timestamp", t.timestamp)
                    txArray.put(obj)
                }
                jsonObject.put("transactions", txArray)

                val debtsArray = JSONArray()
                debts.forEach { d ->
                    val obj = JSONObject()
                    obj.put("id", d.id)
                    obj.put("transactionId", d.transactionId)
                    obj.put("customerName", d.customerName)
                    obj.put("customerPhone", d.customerPhone)
                    obj.put("totalDebt", d.totalDebt)
                    obj.put("totalPaid", d.totalPaid)
                    obj.put("timestamp", d.timestamp)
                    obj.put("notes", d.notes)
                    debtsArray.put(obj)
                }
                jsonObject.put("debts", debtsArray)

                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(jsonObject.toString().toByteArray())
                }
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun importData(context: Context, uri: Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                var jsonStr = ""
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    jsonStr = inputStream.bufferedReader().use { it.readText() }
                }
                val jsonObject = JSONObject(jsonStr)

                // First clear existing data
                repository.resetAppData()

                val stocksArray = jsonObject.optJSONArray("stocks")
                if (stocksArray != null) {
                    for (i in 0 until stocksArray.length()) {
                        val obj = stocksArray.getJSONObject(i)
                        val s = Stock(
                            // Don't keep old ID to prevent issues, but wait, Room might auto-generate if we omit? 
                            // In SQLite with REPLACE, we can keep the ID. For simplicity, just insert them as is.
                            id = obj.getInt("id"),
                            name = obj.getString("name"),
                            costPrice = obj.getDouble("costPrice"),
                            sellingPrice = obj.getDouble("sellingPrice")
                        )
                        // Use a dao function or repository to insert with ID
                        repository.insertStock(s) 
                    }
                }

                // Actually transactions inserting with ID might fail if Dao doesn't handle it. But REPLACE will overwrite.
                val txArray = jsonObject.optJSONArray("transactions")
                if (txArray != null) {
                    for (i in 0 until txArray.length()) {
                        val obj = txArray.getJSONObject(i)
                        val t = TransactionEntity(
                            id = obj.getInt("id"),
                            timestamp = obj.getLong("timestamp"),
                            customerName = obj.getString("customerName"),
                            customerPhone = obj.getString("customerPhone"),
                            totalAmount = obj.getDouble("totalAmount"),
                            amountPaid = obj.getDouble("amountPaid"),
                            changeAmount = obj.getDouble("changeAmount"),
                            itemsJson = obj.getString("itemsJson"),
                            profit = obj.getDouble("profit")
                        )
                        repository.insertTransactionRaw(t)
                    }
                }

                val debtsArray = jsonObject.optJSONArray("debts")
                if (debtsArray != null) {
                    for (i in 0 until debtsArray.length()) {
                        val obj = debtsArray.getJSONObject(i)
                        val d = DebtEntity(
                            id = obj.getInt("id"),
                            transactionId = obj.optInt("transactionId", 0),
                            customerName = obj.getString("customerName"),
                            customerPhone = obj.getString("customerPhone"),
                            totalDebt = obj.getDouble("totalDebt"),
                            totalPaid = obj.getDouble("totalPaid"),
                            timestamp = obj.getLong("timestamp"),
                            notes = obj.getString("notes")
                        )
                        repository.insertDebt(d)
                    }
                }
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }
}

class StoreViewModelFactory(private val repository: StoreRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoreViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
