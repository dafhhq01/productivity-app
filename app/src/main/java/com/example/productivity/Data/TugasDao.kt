package com.example.productivity.Data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TugasDao {
    @Query("SELECT * FROM tugas ORDER BY mendesak DESC, penting DESC, tenggat IS NULL, tenggat ASC")
    suspend fun getAll(): List<Tugas>

    @Query("SELECT * FROM tugas WHERE id = :id")
    suspend fun getById(id: Int): Tugas?

    @Insert
    fun insert(tugas: Tugas): Long

    @Update
    fun update(tugas: Tugas)

    @Delete
    fun delete(tugas: Tugas)
}

