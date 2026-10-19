package com.example.productivity.Data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TransaksiDao {
    @Query("SELECT * FROM transaksi WHERE tanggal LIKE :awalanBulan || '%' ORDER BY tanggal DESC")
    suspend fun getByBulan(awalanBulan: String): List<Transaksi>

    @Query("SELECT * FROM transaksi WHERE id = :id")
    suspend fun getById(id: Int): Transaksi?

    @Query("SELECT COALESCE(SUM(jumlah), 0) FROM transaksi WHERE jenis = 'pemasukan' AND tanggal LIKE :awalanBulan || '%'")
    suspend fun sumPemasukanBulan(awalanBulan: String): Double

    @Query("SELECT COALESCE(SUM(jumlah), 0) FROM transaksi WHERE jenis = 'pengeluaran' AND tanggal LIKE :awalanBulan || '%'")
    suspend fun sumPengeluaranBulan(awalanBulan: String): Double

    @Query("SELECT COALESCE(SUM(jumlah), 0) FROM transaksi WHERE jenis = 'pengeluaran' AND kategori = :kategori AND tanggal LIKE :awalanBulan || '%'")
    suspend fun sumPengeluaranKategori(kategori: String, awalanBulan: String): Double

    @Insert
    fun insert(transaksi: Transaksi): Long

    @Update
    fun update(transaksi: Transaksi)

    @Delete
    fun delete(transaksi: Transaksi)
}
