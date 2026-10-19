package com.example.productivity.Data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budget WHERE tahun = :tahun AND bulan = :bulan ORDER BY kategori ASC")
    suspend fun getByBulan(tahun: Int, bulan: Int): List<Budget>

    @Query("SELECT * FROM budget WHERE id = :id")
    suspend fun getById(id: Int): Budget?

    @Query("SELECT COUNT(*) FROM budget WHERE kategori = :kategori AND tahun = :tahun AND bulan = :bulan AND id != :idSelf")
    suspend fun countDuplikat(kategori: String, tahun: Int, bulan: Int, idSelf: Int): Int

    @Insert
    fun insert(budget: Budget): Long

    @Update
    fun update(budget: Budget)

    @Delete
    fun delete(budget: Budget)
}
