package com.example.productivity.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.productivity.Data.AppDatabase
import com.example.productivity.Util.Hitung
import com.example.productivity.databinding.FragmentDashboardBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            
            val tahun = Calendar.getInstance().get(Calendar.YEAR)
            val bulan = Calendar.getInstance().get(Calendar.MONTH) + 1
            val awalanBulan = Hitung.awalanBulan(tahun, bulan)
            
            val semuaTugas = db.tugasDao().getAll()
            var tugasBelumSelesai = 0
            for (tugas in semuaTugas) {
                val subtugas = db.subtugasDao().getByTugasId(tugas.id)
                val subtugasSelesai = subtugas.map { it.selesai }
                val progress = Hitung.hitungProgress(tugas.selesai, subtugasSelesai)
                if (progress < 100) {
                    tugasBelumSelesai++
                }
            }
            
            val pemasukan = db.transaksiDao().sumPemasukanBulan(awalanBulan)
            val pengeluaran = db.transaksiDao().sumPengeluaranBulan(awalanBulan)
            
            val semuaInvestasi = db.investasiDao().getAll()
            var totalModal = 0.0
            for (inv in semuaInvestasi) {
                totalModal += inv.modal
            }
            
            binding.tvTugasBelumSelesai.text = tugasBelumSelesai.toString()
            binding.tvPemasukanBulanIni.text = Hitung.formatRupiah(pemasukan)
            binding.tvPengeluaranBulanIni.text = Hitung.formatRupiah(pengeluaran)
            binding.tvTotalModalInvestasi.text = Hitung.formatRupiah(totalModal)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
