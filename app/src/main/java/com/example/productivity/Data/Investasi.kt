package com.example.productivity.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "investasi")
data class Investasi(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nama: String,
    val modal: Double,
    val bungaTahunan: Double,
    val tanggalMulai: String // yyyy-MM-dd
)
