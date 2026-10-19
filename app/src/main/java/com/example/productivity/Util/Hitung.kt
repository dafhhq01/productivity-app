package com.example.productivity.Util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.max
import kotlin.math.round

object Hitung {
    fun hitungProgress(tugasSelesai: Boolean, daftarSubtugasSelesai: List<Boolean>): Int {
        if (daftarSubtugasSelesai.isEmpty()) {
            return if (tugasSelesai) 100 else 0
        }
        val jumlahSelesai = daftarSubtugasSelesai.count { it }
        val total = daftarSubtugasSelesai.size
        return round((jumlahSelesai.toDouble() / total) * 100).toInt()
    }

    fun labelKuadran(mendesak: Boolean, penting: Boolean): String {
        return when {
            mendesak && penting -> "Penting & Mendesak"
            penting && !mendesak -> "Penting & Tidak Mendesak"
            mendesak && !penting -> "Mendesak & Tidak Penting"
            else -> "Tidak Penting & Tidak Mendesak"
        }
    }

    fun hariAntara(tanggalAwal: String, tanggalAkhir: String): Long {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        formatter.timeZone = java.util.TimeZone.getTimeZone("UTC")
        
        val calAwal = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
        val calAkhir = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
        
        try {
            calAwal.time = formatter.parse(tanggalAwal) ?: return 0L
            calAkhir.time = formatter.parse(tanggalAkhir) ?: return 0L
        } catch (e: Exception) {
            return 0L
        }
        
        val diffMs = calAkhir.timeInMillis - calAwal.timeInMillis
        return diffMs / (1000 * 60 * 60 * 24)
    }

    fun nilaiSekarang(modal: Double, bungaPersen: Double, hari: Long): Double {
        val hariValid = max(hari, 0L)
        return modal * Math.pow(1.0 + bungaPersen / 100.0, hariValid.toDouble() / 365.0)
    }

    fun formatRupiah(angka: Double): String {
        val nf = NumberFormat.getNumberInstance(Locale("id", "ID"))
        return "Rp" + nf.format(angka.toLong())
    }

    fun awalanBulan(tahun: Int, bulan: Int): String {
        return String.format("%04d-%02d", tahun, bulan)
    }
}
