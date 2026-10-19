package com.example.productivity.Data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SubtugasDao {
    @Query("SELECT * FROM subtugas WHERE tugasId = :tugasId ORDER BY id ASC")
    suspend fun getByTugasId(tugasId: Int): List<Subtugas>

    @Insert
    fun insert(subtugas: Subtugas): Long

    @Update
    fun update(subtugas: Subtugas)

    @Delete
    fun delete(subtugas: Subtugas)

    @Query("DELETE FROM subtugas WHERE tugasId = :tugasId")
    fun deleteByTugasId(tugasId: Int)
}
