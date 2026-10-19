package com.example.productivity.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.productivity.Activities.TransaksiFormActivity
import com.example.productivity.Adapters.TransaksiAdapter
import com.example.productivity.Data.AppDatabase
import com.example.productivity.Util.Hitung
import com.example.productivity.Util.Konstanta
import com.example.productivity.databinding.FragmentTransaksiBinding
import kotlinx.coroutines.launch
import java.util.Calendar

class TransaksiFragment : Fragment() {
    private var _binding: FragmentTransaksiBinding? = null
    private val binding get() = _binding!!
    private var tahun = Calendar.getInstance().get(Calendar.YEAR)
    private var bulan = Calendar.getInstance().get(Calendar.MONTH) + 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransaksiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.btnPrevBulan.setOnClickListener { bulanSebelumnya() }
        binding.btnNextBulan.setOnClickListener { bulanBerikutnya() }
        binding.fabTambah.setOnClickListener { tambah() }
        
        binding.rvTransaksi.layoutManager = LinearLayoutManager(context)
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            val awalanBulan = Hitung.awalanBulan(tahun, bulan)
            
            val transaksi = db.transaksiDao().getByBulan(awalanBulan)
            val pemasukan = db.transaksiDao().sumPemasukanBulan(awalanBulan)
            val pengeluaran = db.transaksiDao().sumPengeluaranBulan(awalanBulan)
            val selisih = pemasukan - pengeluaran
            
            val judul = "${Konstanta.BULAN_NAMA[bulan - 1]} $tahun"
            binding.tvBulan.text = judul
            
            binding.tvPemasukan.text = "Pemasukan: ${Hitung.formatRupiah(pemasukan)}"
            binding.tvPengeluaran.text = "Pengeluaran: ${Hitung.formatRupiah(pengeluaran)}"
            binding.tvSelisih.text = "Selisih: ${Hitung.formatRupiah(selisih)}"
            
            if (transaksi.isEmpty()) {
                binding.tvKosong.visibility = View.VISIBLE
                binding.rvTransaksi.visibility = View.GONE
            } else {
                binding.tvKosong.visibility = View.GONE
                binding.rvTransaksi.visibility = View.VISIBLE
                binding.rvTransaksi.adapter = TransaksiAdapter(transaksi) { t ->
                    val intent = Intent(context, TransaksiFormActivity::class.java)
                    intent.putExtra("id", t.id)
                    startActivity(intent)
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
        val intent = Intent(context, TransaksiFormActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
