package com.example.bazaar_hisabnew.db

import androidx.room.*

@Dao
interface ListsDao {

    @Insert
    suspend fun addList(lists: Lists)

    @Query("SELECT * FROM LISTS ORDER BY id DESC")
    suspend fun getLists() : MutableList<Lists>

    @Query("SELECT id FROM LISTS WHERE list_name LIKE :listName")
    suspend fun getListId(listName : String) : Int

    @Query("SELECT budget FROM LISTS WHERE id LIKE :id")
    suspend fun getBudget(id : Int) : String

    @Query("SELECT list_name FROM LISTS WHERE id LiKE :id")
    suspend fun getListName(id : Int) : String

    @Delete
    suspend fun deleteList(lists: Lists)

    @Update
    suspend fun updateList(lists: Lists)

}