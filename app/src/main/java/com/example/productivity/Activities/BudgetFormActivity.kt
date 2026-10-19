package com.example.productivity.Activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.productivity.Data.AppDatabase
import com.example.productivity.Data.Budget
import com.example.productivity.Util.Konstanta
import com.example.productivity.databinding.ActivityBudgetFormBinding
import kotlinx.coroutines.launch

class BudgetFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBudgetFormBinding
    private var budgetId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        budgetId = intent.getIntExtra("id", -1)
        val tahunIntent = intent.getIntExtra("tahun", java.util.Calendar.getInstance().get(java.util.Calendar.YEAR))
        val bulanIntent = intent.getIntExtra("bulan", java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1)

        val adapterKategori = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, Konstanta.KATEGORI_BUDGET)
        binding.spKategori.adapter = adapterKategori

        binding.etTahun.setText(tahunIntent.toString())
        binding.etBulan.setText(bulanIntent.toString())

        binding.btnSimpan.setOnClickListener { simpan() }
        binding.btnHapus.setOnClickListener { hapus() }

        if (budgetId != -1) {
            binding.btnHapus.visibility = android.view.View.VISIBLE
            loadData()
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(this@BudgetFormActivity)
            val budget = db.budgetDao().getById(budgetId) ?: return@launch

            binding.spKategori.setSelection(Konstanta.KATEGORI_BUDGET.indexOf(budget.kategori))
            binding.etJumlah.setText(budget.jumlah.toString())
            binding.etTahun.setText(budget.tahun.toString())
            binding.etBulan.setText(budget.bulan.toString())
        }
    }

    private fun simpan() {
        val kategori = binding.spKategori.selectedItem.toString()
        val jumlahStr = binding.etJumlah.text.toString().trim()
        val tahunStr = binding.etTahun.text.toString().trim()
        val bulanStr = binding.etBulan.text.toString().trim()

        if (jumlahStr.isEmpty() || tahunStr.isEmpty() || bulanStr.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val jumlah = try {
            jumlahStr.toDouble()
        } catch (e: Exception) {
            Toast.makeText(this, "Jumlah harus angka", Toast.LENGTH_SHORT).show()
            return
        }

        val tahun = try {
            tahunStr.toInt()
        } catch (e: Exception) {
            Toast.makeText(this, "Tahun harus angka", Toast.LENGTH_SHORT).show()
            return
        }

        val bulan = try {
            bulanStr.toInt()
        } catch (e: Exception) {
            Toast.makeText(this, "Bulan harus angka", Toast.LENGTH_SHORT).show()
            return
        }

        if (jumlah <= 0 || jumlah > 999999999999) {
            Toast.makeText(this, "Jumlah harus > 0 dan ≤ 999999999999", Toast.LENGTH_SHORT).show()
            return
        }

        if (tahun < 2000 || tahun > 2100) {
            Toast.makeText(this, "Tahun harus 2000-2100", Toast.LENGTH_SHORT).show()
            return
        }

        if (bulan < 1 || bulan > 12) {
            Toast.makeText(this, "Bulan harus 1-12", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val db = AppDatabase.getInstance(this@BudgetFormActivity)
            try {
                val duplikat = db.budgetDao().countDuplikat(kategori, tahun, bulan, budgetId)
                if (duplikat > 0) {
                    Toast.makeText(this@BudgetFormActivity, "Budget kategori ini sudah ada di bulan tersebut", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val budget = Budget(
                    id = budgetId,
                    kategori = kategori,
                    jumlah = jumlah,
                    tahun = tahun,
                    bulan = bulan
                )

                if (budgetId == -1) {
                    db.budgetDao().insert(budget)
                } else {
                    db.budgetDao().update(budget)
                }
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@BudgetFormActivity, "Gagal menyimpan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hapus() {
        AlertDialog.Builder(this)
            .setTitle("Hapus budget")
            .setMessage("Hapus budget ini?")
            .setPositiveButton("Ya") { _, _ ->
                lifecycleScope.launch {
                    val db = AppDatabase.getInstance(this@BudgetFormActivity)
                    try {
                        val budget = db.budgetDao().getById(budgetId) ?: return@launch
                        db.budgetDao().delete(budget)
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(this@BudgetFormActivity, "Gagal menghapus", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
}
