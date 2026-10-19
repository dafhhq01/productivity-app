package com.example.productivity

import com.example.productivity.Util.Hitung
import org.junit.Assert.assertEquals
import org.junit.Test

class HitungTest {
    @Test
    fun testProgressTanpaSubtugas_Selesai() {
        val progress = Hitung.hitungProgress(true, emptyList())
        assertEquals(100, progress)
    }

    @Test
    fun testProgressTanpaSubtugas_BelumSelesai() {
        val progress = Hitung.hitungProgress(false, emptyList())
        assertEquals(0, progress)
    }

    @Test
    fun testProgressDuaFromDuaSubtugas() {
        val subtugas = listOf(true, true)
        val progress = Hitung.hitungProgress(false, subtugas)
        assertEquals(100, progress)
    }

    @Test
    fun testProgressDuaFromEmpatSubtugas() {
        val subtugas = listOf(true, false, true, false)
        val progress = Hitung.hitungProgress(false, subtugas)
        assertEquals(50, progress)
    }

    @Test
    fun testLabelKuadran_PentingMendesak() {
        val label = Hitung.labelKuadran(true, true)
        assertEquals("Penting & Mendesak", label)
    }

    @Test
    fun testLabelKuadran_PentingTidakMendesak() {
        val label = Hitung.labelKuadran(false, true)
        assertEquals("Penting & Tidak Mendesak", label)
    }

    @Test
    fun testLabelKuadran_TidakPentingMendesak() {
        val label = Hitung.labelKuadran(true, false)
        assertEquals("Mendesak & Tidak Penting", label)
    }

    @Test
    fun testLabelKuadran_TidakPentingTidakMendesak() {
        val label = Hitung.labelKuadran(false, false)
        assertEquals("Tidak Penting & Tidak Mendesak", label)
    }

    @Test
    fun testHariAntara_DuaTahunSama() {
        val hari = Hitung.hariAntara("2024-10-15", "2026-10-15")
        assertEquals(730, hari)
    }

    @Test
    fun testNilaiSekarang_Modal10jModals8Persen730Hari() {
        val nilai = Hitung.nilaiSekarang(10000000.0, 8.0, 730)
        assertEquals(11664000.0, nilai, 0.01)
    }

    @Test
    fun testNilaiSekarang_HariNegativeNotBelowModal() {
        val nilai = Hitung.nilaiSekarang(10000000.0, 8.0, -10)
        assert(nilai >= 10000000.0)
    }

    @Test
    fun testHariAntara_HariNegativeNotBelowZero() {
        val hari = Hitung.hariAntara("2026-10-15", "2024-10-15")
        assert(hari < 0)
    }
}
