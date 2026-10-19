package com.example.productivity.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val kategori: String,
    val jumlah: Double,
    val tahun: Int,
    val bulan: Int
)
