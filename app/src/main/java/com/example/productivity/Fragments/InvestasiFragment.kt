package com.example.productivity.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.productivity.Activities.InvestasiFormActivity
import com.example.productivity.Adapters.InvestasiAdapter
import com.example.productivity.Data.AppDatabase
import com.example.productivity.Util.Hitung
import com.example.productivity.databinding.FragmentInvestasiBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class InvestasiFragment : Fragment() {
    private var _binding: FragmentInvestasiBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInvestasiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.fabTambah.setOnClickListener { tambah() }
        binding.rvInvestasi.layoutManager = LinearLayoutManager(context)
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            val investasi = db.investasiDao().getAll()
            
            val hariIni = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
            var totalModal = 0.0
            var totalNilaiSekarang = 0.0
            
            for (inv in investasi) {
                val hari = Hitung.hariAntara(inv.tanggalMulai, hariIni)
                val nilaiSekarang = Hitung.nilaiSekarang(inv.modal, inv.bungaTahunan, hari)
                totalModal += inv.modal
                totalNilaiSekarang += nilaiSekarang
            }
            
            binding.tvTotalModal.text = "Total Modal: ${Hitung.formatRupiah(totalModal)}"
            binding.tvTotalNilaiSekarang.text = "Total Nilai: ${Hitung.formatRupiah(totalNilaiSekarang)}"
            
            if (investasi.isEmpty()) {
                binding.tvKosong.visibility = View.VISIBLE
                binding.rvInvestasi.visibility = View.GONE
            } else {
                binding.tvKosong.visibility = View.GONE
                binding.rvInvestasi.visibility = View.VISIBLE
                binding.rvInvestasi.adapter = InvestasiAdapter(investasi) { inv ->
                    val intent = Intent(context, InvestasiFormActivity::class.java)
                    intent.putExtra("id", inv.id)
                    startActivity(intent)
                }
            }
        }
    }

    private fun tambah() {
        val intent = Intent(context, InvestasiFormActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
