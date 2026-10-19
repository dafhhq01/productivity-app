package com.example.productivity.Activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.productivity.Data.AppDatabase
import com.example.productivity.Data.Investasi
import com.example.productivity.databinding.ActivityInvestasiFormBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class InvestasiFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInvestasiFormBinding
    private var investasiId = -1
    private var tanggalMulai = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvestasiFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        investasiId = intent.getIntExtra("id", -1)
        tanggalMulai = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

        binding.btnTanggalMulai.setOnClickListener { pilihTanggal() }
        binding.btnSimpan.setOnClickListener { simpan() }
        binding.btnHapus.setOnClickListener { hapus() }

        if (investasiId != -1) {
            binding.btnHapus.visibility = android.view.View.VISIBLE
            loadData()
        } else {
            updateViewTanggal()
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(this@InvestasiFormActivity)
            val investasi = db.investasiDao().getById(investasiId) ?: return@launch

            binding.etNama.setText(investasi.nama)
            binding.etModal.setText(investasi.modal.toString())
            binding.etBunga.setText(investasi.bungaTahunan.toString())
            tanggalMulai = investasi.tanggalMulai
            updateViewTanggal()
        }
    }

    private fun pilihTanggal() {
        val cal = Calendar.getInstance()
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            cal.time = sdf.parse(tanggalMulai) ?: Calendar.getInstance().time
        } catch (e: Exception) {}

        DatePickerDialog(
            this,
            { _, tahun, bulan, tanggal ->
                val bulanFix = bulan + 1
                tanggalMulai = String.format("%04d-%02d-%02d", tahun, bulanFix, tanggal)
                updateViewTanggal()
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateViewTanggal() {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(tanggalMulai)
            val sdOut = SimpleDateFormat("d MMM yyyy", Locale("id", "ID"))
            binding.tvTanggalMulai.text = sdOut.format(date ?: tanggalMulai)
        } catch (e: Exception) {
            binding.tvTanggalMulai.text = tanggalMulai
        }
    }

    private fun simpan() {
        val nama = binding.etNama.text.toString().trim()
        val modalStr = binding.etModal.text.toString().trim()
        val bungaStr = binding.etBunga.text.toString().trim()

        if (nama.isEmpty() || modalStr.isEmpty() || bungaStr.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val modal = try {
            modalStr.toDouble()
        } catch (e: Exception) {
            Toast.makeText(this, "Modal harus angka", Toast.LENGTH_SHORT).show()
            return
        }

        val bunga = try {
            bungaStr.toDouble()
        } catch (e: Exception) {
            Toast.makeText(this, "Bunga harus angka", Toast.LENGTH_SHORT).show()
            return
        }

        if (modal <= 0 || modal > 999999999999) {
            Toast.makeText(this, "Modal harus > 0 dan ≤ 999999999999", Toast.LENGTH_SHORT).show()
            return
        }

        if (bunga < 0 || bunga > 100) {
            Toast.makeText(this, "Bunga harus 0-100", Toast.LENGTH_SHORT).show()
            return
        }

        val hariIni = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
        if (tanggalMulai > hariIni) {
            Toast.makeText(this, "Tanggal mulai tidak boleh di masa depan", Toast.LENGTH_SHORT).show()
            return
        }

        val investasi = Investasi(
            id = investasiId,
            nama = nama,
            modal = modal,
            bungaTahunan = bunga,
            tanggalMulai = tanggalMulai
        )

        lifecycleScope.launch {
            val db = AppDatabase.getInstance(this@InvestasiFormActivity)
            try {
                if (investasiId == -1) {
                    db.investasiDao().insert(investasi)
                } else {
                    db.investasiDao().update(investasi)
                }
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@InvestasiFormActivity, "Gagal menyimpan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hapus() {
        AlertDialog.Builder(this)
            .setTitle("Hapus investasi")
            .setMessage("Hapus investasi ini?")
            .setPositiveButton("Ya") { _, _ ->
                lifecycleScope.launch {
                    val db = AppDatabase.getInstance(this@InvestasiFormActivity)
                    try {
                        val investasi = db.investasiDao().getById(investasiId) ?: return@launch
                        db.investasiDao().delete(investasi)
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(this@InvestasiFormActivity, "Gagal menghapus", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
}
