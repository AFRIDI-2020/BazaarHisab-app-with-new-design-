package com.example.bazaar_hisabnew.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ItemDao {

    @Insert
    suspend fun addItem (items : Item)


    @Query("SELECT * FROM Item WHERE list_id LIKE :listId ORDER BY id ASC")
    suspend fun getItem(listId : String) : MutableList<Item>

    @Query("SELECT cost FROM ITEM WHERE list_id LIKE :listId ORDER BY id ASC")
    suspend fun getAllCost(listId: String) : List<String>

    @Delete
    suspend fun deleteItem(items: Item)
}