package com.example.bazaar_hisabnew.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Item(
    @ColumnInfo(name = "list_id") val listId: String,
    @ColumnInfo(name = "item_name") val itemName: String,
    @ColumnInfo(name = "quantity") val quantity: String,
    @ColumnInfo(name = "unit") val unit: String,
    @ColumnInfo(name = "cost_per_unit") val costPerUnit: String,
    @ColumnInfo(name = "cost") val cost: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}