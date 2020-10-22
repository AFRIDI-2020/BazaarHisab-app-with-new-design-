package com.example.bazaar_hisabnew.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ListsDao {

    @Insert
    suspend fun addList(lists: Lists)

    @Query("SELECT * FROM LISTS ORDER BY id DESC")
    suspend fun getLists() : List<Lists>

    @Query("SELECT id FROM LISTS WHERE list_name LIKE :listName")
    suspend fun getListId(listName : String) : Int

    @Query("SELECT budget FROM LISTS WHERE id LIKE :id")
    suspend fun getBudget(id : Int) : String

}