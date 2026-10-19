package com.example.productivity.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaksi")
data class Transaksi(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val jenis: String, // "pemasukan" atau "pengeluaran"
    val kategori: String,
    val jumlah: Double,
    val tanggal: String, // yyyy-MM-dd
    val catatan: String = ""
)
