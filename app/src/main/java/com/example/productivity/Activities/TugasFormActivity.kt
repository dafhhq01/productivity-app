package com.example.productivity.Activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.withTransaction
import com.example.productivity.Data.AppDatabase
import com.example.productivity.Data.Subtugas
import com.example.productivity.Data.Tugas
import com.example.productivity.databinding.ActivityTugasFormBinding
import com.example.productivity.databinding.ItemSubtugasFormBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TugasFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTugasFormBinding
    private var tugasId = -1
    private var tanggalTenggat = ""
    private val daftarSubtugas = mutableListOf<Subtugas>()
    private val daftarViewsSubtugas = mutableListOf<ItemSubtugasFormBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTugasFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tugasId = intent.getIntExtra("id", -1)

        binding.btnTenggat.setOnClickListener { pilihTanggal() }
        binding.btnHapusTanggal.setOnClickListener { hapusTanggal() }
        binding.btnTambahSubtugas.setOnClickListener { tambahBariSubtugas() }
        binding.btnSimpan.setOnClickListener { simpan() }
        binding.btnHapus.setOnClickListener { hapus() }

        if (tugasId != -1) {
            binding.btnHapus.visibility = android.view.View.VISIBLE
            loadData()
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(this@TugasFormActivity)
            val tugas = db.tugasDao().getById(tugasId) ?: return@launch
            
            binding.etJudul.setText(tugas.judul)
            binding.etDeskripsi.setText(tugas.deskripsi)
            tanggalTenggat = tugas.tenggat
            updateViewTenggat()
            binding.cbMendesak.isChecked = tugas.mendesak
            binding.cbPenting.isChecked = tugas.penting
            binding.etTujuanHarada.setText(tugas.tujuanHarada)
            binding.cbSelesai.isChecked = tugas.selesai

            val subtugas = db.subtugasDao().getByTugasId(tugasId)
            daftarSubtugas.addAll(subtugas)
            
            for (st in subtugas) {
                tambahBariSubtugas(st.judul, st.selesai)
            }
        }
    }

    private fun pilihTanggal() {
        val cal = Calendar.getInstance()
        if (tanggalTenggat.isNotEmpty()) {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                cal.time = sdf.parse(tanggalTenggat) ?: Calendar.getInstance().time
            } catch (e: Exception) {}
        }

        DatePickerDialog(
            this,
            { _, tahun, bulan, tanggal ->
                val bulanFix = bulan + 1
                tanggalTenggat = String.format("%04d-%02d-%02d", tahun, bulanFix, tanggal)
                updateViewTenggat()
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun hapusTanggal() {
        tanggalTenggat = ""
        updateViewTenggat()
    }

    private fun updateViewTenggat() {
        if (tanggalTenggat.isEmpty()) {
            binding.tvTenggat.text = "Tidak ada tenggat"
        } else {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = sdf.parse(tanggalTenggat)
                val sdOut = SimpleDateFormat("d MMM yyyy", Locale("id", "ID"))
                binding.tvTenggat.text = sdOut.format(date ?: tanggalTenggat)
            } catch (e: Exception) {
                binding.tvTenggat.text = tanggalTenggat
            }
        }
    }

    private fun tambahBariSubtugas(judul: String = "", selesai: Boolean = false) {
        val inflater = LayoutInflater.from(this)
        val itemBinding = ItemSubtugasFormBinding.inflate(inflater)
        
        itemBinding.etJudul.setText(judul)
        itemBinding.cbSelesai.isChecked = selesai
        
        itemBinding.btnHapus.setOnClickListener {
            binding.llSubtugas.removeView(itemBinding.root)
            daftarViewsSubtugas.remove(itemBinding)
        }

        binding.llSubtugas.addView(itemBinding.root)
        daftarViewsSubtugas.add(itemBinding)
    }

    private fun simpan() {
        val judul = binding.etJudul.text.toString().trim()
        if (judul.isEmpty()) {
            Toast.makeText(this, "Judul tugas wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val deskripsi = binding.etDeskripsi.text.toString()
        val mendesak = binding.cbMendesak.isChecked
        val penting = binding.cbPenting.isChecked
        val tujuanHarada = binding.etTujuanHarada.text.toString()
        val selesai = binding.cbSelesai.isChecked

        val tugas = Tugas(
            id = tugasId,
            judul = judul,
            deskripsi = deskripsi,
            tenggat = tanggalTenggat,
            mendesak = mendesak,
            penting = penting,
            selesai = selesai,
            tujuanHarada = tujuanHarada
        )

        val subtugasArray = daftarViewsSubtugas.mapNotNull { itemBinding ->
            val j = itemBinding.etJudul.text.toString().trim()
            if (j.isNotEmpty()) {
                Subtugas(tugasId = 0, judul = j, selesai = itemBinding.cbSelesai.isChecked)
            } else null
        }

        lifecycleScope.launch {
            val db = AppDatabase.getInstance(this@TugasFormActivity)
            try {
                db.withTransaction {
                    val idTugas = if (tugasId == -1) {
                        db.tugasDao().insert(tugas).toInt()
                    } else {
                        db.tugasDao().update(tugas)
                        tugasId
                    }

                    if (tugasId != -1) {
                        db.subtugasDao().deleteByTugasId(idTugas)
                    }

                    for (st in subtugasArray) {
                        db.subtugasDao().insert(st.copy(tugasId = idTugas))
                    }
                }
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@TugasFormActivity, "Gagal menyimpan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hapus() {
        AlertDialog.Builder(this)
            .setTitle("Hapus tugas")
            .setMessage("Hapus tugas ini?")
            .setPositiveButton("Ya") { _, _ ->
                lifecycleScope.launch {
                    val db = AppDatabase.getInstance(this@TugasFormActivity)
                    try {
                        db.withTransaction {
                            val tugas = db.tugasDao().getById(tugasId) ?: return@withTransaction
                            db.subtugasDao().deleteByTugasId(tugasId)
                            db.tugasDao().delete(tugas)
                        }
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(this@TugasFormActivity, "Gagal menghapus", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
