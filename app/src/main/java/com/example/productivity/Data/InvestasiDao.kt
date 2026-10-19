package com.example.productivity.Data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface InvestasiDao {
    @Query("SELECT * FROM investasi ORDER BY id ASC")
    suspend fun getAll(): List<Investasi>

    @Query("SELECT * FROM investasi WHERE id = :id")
    suspend fun getById(id: Int): Investasi?

    @Insert
    fun insert(investasi: Investasi): Long

    @Update
    fun update(investasi: Investasi)

    @Delete
    fun delete(investasi: Investasi)
}
