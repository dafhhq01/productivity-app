package com.example.productivity.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.productivity.Activities.BudgetFormActivity
import com.example.productivity.Adapters.BudgetAdapter
import com.example.productivity.Data.AppDatabase
import com.example.productivity.Data.BudgetRingkasan
import com.example.productivity.Util.Hitung
import com.example.productivity.Util.Konstanta
import com.example.productivity.databinding.FragmentBudgetBinding
import kotlinx.coroutines.launch
import java.util.Calendar

class BudgetFragment : Fragment() {
    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!
    private var tahun = Calendar.getInstance().get(Calendar.YEAR)
    private var bulan = Calendar.getInstance().get(Calendar.MONTH) + 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.btnPrevBulan.setOnClickListener { bulanSebelumnya() }
        binding.btnNextBulan.setOnClickListener { bulanBerikutnya() }
        binding.fabTambah.setOnClickListener { tambah() }
        
        binding.rvBudget.layoutManager = LinearLayoutManager(context)
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            val awalanBulan = Hitung.awalanBulan(tahun, bulan)
            
            val budgets = db.budgetDao().getByBulan(tahun, bulan)
            val daftarRingkasan = mutableListOf<BudgetRingkasan>()
            
            for (budget in budgets) {
                val terpakai = db.transaksiDao().sumPengeluaranKategori(budget.kategori, awalanBulan)
                val sisa = budget.jumlah - terpakai
                daftarRingkasan.add(BudgetRingkasan(budget.kategori, budget.jumlah, terpakai, sisa))
            }
            
            val judul = "${Konstanta.BULAN_NAMA[bulan - 1]} $tahun"
            binding.tvBulan.text = judul
            
            if (daftarRingkasan.isEmpty()) {
                binding.tvKosong.visibility = View.VISIBLE
                binding.rvBudget.visibility = View.GONE
            } else {
                binding.tvKosong.visibility = View.GONE
                binding.rvBudget.visibility = View.VISIBLE
                binding.rvBudget.adapter = BudgetAdapter(daftarRingkasan) { ringkasan ->
                    val budget = budgets.find { it.kategori == ringkasan.kategori }
                    if (budget != null) {
                        val intent = Intent(context, BudgetFormActivity::class.java)
                        intent.putExtra("id", budget.id)
                        intent.putExtra("tahun", tahun)
                        intent.putExtra("bulan", bulan)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun bulanSebelumnya() {
        bulan--
        if (bulan < 1) {
            bulan = 12
            tahun--
        }
        loadData()
    }

    private fun bulanBerikutnya() {
        bulan++
        if (bulan > 12) {
            bulan = 1
            tahun++
        }
        loadData()
    }

    private fun tambah() {
        val intent = Intent(context, BudgetFormActivity::class.java)
        intent.putExtra("tahun", tahun)
        intent.putExtra("bulan", bulan)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
