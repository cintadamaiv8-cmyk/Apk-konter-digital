package com.example.data

import androidx.room.TypeConverter
import org.json.JSONArray
import org.json.JSONObject

class Converters {
    @TypeConverter
    fun fromCartItemList(value: List<CartItem>): String {
        val jsonArray = JSONArray()
        for (item in value) {
            val jsonObject = JSONObject()
            jsonObject.put("stockId", item.stockId)
            jsonObject.put("name", item.name)
            jsonObject.put("price", item.price)
            jsonObject.put("costPrice", item.costPrice)
            jsonObject.put("quantity", item.quantity)
            jsonObject.put("subtotal", item.subtotal)
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }

    @TypeConverter
    fun toCartItemList(value: String): List<CartItem> {
        val list = mutableListOf<CartItem>()
        if (value.isNotEmpty()) {
            val jsonArray = JSONArray(value)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                list.add(
                    CartItem(
                        stockId = jsonObject.getInt("stockId"),
                        name = jsonObject.getString("name"),
                        price = jsonObject.getDouble("price"),
                        costPrice = jsonObject.optDouble("costPrice", 0.0),
                        quantity = jsonObject.getInt("quantity"),
                        subtotal = jsonObject.getDouble("subtotal")
                    )
                )
            }
        }
        return list
    }
}
