package com.example.productivity.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tugas")
data class Tugas(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val judul: String,
    val deskripsi: String = "",
    val tenggat: String = "",
    val mendesak: Boolean = false,
    val penting: Boolean = false,
    val selesai: Boolean = false,
    val tujuanHarada: String = ""
)
