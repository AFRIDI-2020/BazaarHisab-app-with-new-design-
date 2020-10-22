package com.example.bazaar_hisabnew.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Lists(
    @ColumnInfo(name = "list_name") val listName : String,
    @ColumnInfo(name = "budget") val budget : String
) {
    @PrimaryKey(autoGenerate = true) var id : Int = 0
}