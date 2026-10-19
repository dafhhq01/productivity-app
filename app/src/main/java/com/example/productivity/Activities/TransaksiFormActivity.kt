package com.example.productivity.Activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.productivity.Data.AppDatabase
import com.example.productivity.Data.Transaksi
import com.example.productivity.Util.Konstanta
import com.example.productivity.databinding.ActivityTransaksiFormBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TransaksiFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransaksiFormBinding
    private var transaksiId = -1
    private var tanggalTransaksi = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransaksiFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transaksiId = intent.getIntExtra("id", -1)
        tanggalTransaksi = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

        val adapterJenis = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, Konstanta.JENIS_TRANSAKSI)
        binding.spJenis.adapter = adapterJenis

        val adapterKategori = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, Konstanta.KATEGORI_LENGKAP)
        binding.spKategori.adapter = adapterKategori

        binding.btnTanggal.setOnClickListener { pilihTanggal() }
        binding.btnSimpan.setOnClickListener { simpan() }
        binding.btnHapus.setOnClickListener { hapus() }

        if (transaksiId != -1) {
            binding.btnHapus.visibility = android.view.View.VISIBLE
            loadData()
        } else {
            updateViewTanggal()
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(this@TransaksiFormActivity)
            val transaksi = db.transaksiDao().getById(transaksiId) ?: return@launch

            binding.spJenis.setSelection(Konstanta.JENIS_TRANSAKSI.indexOf(transaksi.jenis))
            binding.spKategori.setSelection(Konstanta.KATEGORI_LENGKAP.indexOf(transaksi.kategori))
            binding.etJumlah.setText(transaksi.jumlah.toString())
            tanggalTransaksi = transaksi.tanggal
            updateViewTanggal()
            binding.etCatatan.setText(transaksi.catatan)
        }
    }

    private fun pilihTanggal() {
        val cal = Calendar.getInstance()
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            cal.time = sdf.parse(tanggalTransaksi) ?: Calendar.getInstance().time
        } catch (e: Exception) {}

        DatePickerDialog(
            this,
            { _, tahun, bulan, tanggal ->
                val bulanFix = bulan + 1
                tanggalTransaksi = String.format("%04d-%02d-%02d", tahun, bulanFix, tanggal)
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
            val date = sdf.parse(tanggalTransaksi)
            val sdOut = SimpleDateFormat("d MMM yyyy", Locale("id", "ID"))
            binding.tvTanggal.text = sdOut.format(date ?: tanggalTransaksi)
        } catch (e: Exception) {
            binding.tvTanggal.text = tanggalTransaksi
        }
    }

    private fun simpan() {
        val jenis = binding.spJenis.selectedItem.toString()
        val kategori = binding.spKategori.selectedItem.toString()
        val jumlahStr = binding.etJumlah.text.toString().trim()
        val catatan = binding.etCatatan.text.toString()

        if (jumlahStr.isEmpty()) {
            Toast.makeText(this, "Jumlah wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val jumlah = try {
            jumlahStr.toDouble()
        } catch (e: Exception) {
            Toast.makeText(this, "Jumlah harus angka", Toast.LENGTH_SHORT).show()
            return
        }

        if (jumlah <= 0 || jumlah > 999999999999) {
            Toast.makeText(this, "Jumlah harus > 0 dan ≤ 999999999999", Toast.LENGTH_SHORT).show()
            return
        }

        val transaksi = Transaksi(
            id = transaksiId,
            jenis = jenis,
            kategori = kategori,
            jumlah = jumlah,
            tanggal = tanggalTransaksi,
            catatan = catatan
        )

        lifecycleScope.launch {
            val db = AppDatabase.getInstance(this@TransaksiFormActivity)
            try {
                if (transaksiId == -1) {
                    db.transaksiDao().insert(transaksi)
                } else {
                    db.transaksiDao().update(transaksi)
                }
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@TransaksiFormActivity, "Gagal menyimpan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hapus() {
        AlertDialog.Builder(this)
            .setTitle("Hapus transaksi")
            .setMessage("Hapus transaksi ini?")
            .setPositiveButton("Ya") { _, _ ->
                lifecycleScope.launch {
                    val db = AppDatabase.getInstance(this@TransaksiFormActivity)
                    try {
                        val transaksi = db.transaksiDao().getById(transaksiId) ?: return@launch
                        db.transaksiDao().delete(transaksi)
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(this@TransaksiFormActivity, "Gagal menghapus", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
}
