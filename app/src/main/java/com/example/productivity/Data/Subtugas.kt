package com.example.productivity.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subtugas")
data class Subtugas(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tugasId: Int,
    val judul: String,
    val selesai: Boolean = false
)
