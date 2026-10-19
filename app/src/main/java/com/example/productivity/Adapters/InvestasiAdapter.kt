package com.example.productivity.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.productivity.Data.Investasi
import com.example.productivity.Util.Hitung
import com.example.productivity.databinding.ItemInvestasiBinding
import java.text.SimpleDateFormat
import java.util.Locale

class InvestasiAdapter(
    private val daftarInvestasi: List<Investasi>,
    private val onItemClick: (Investasi) -> Unit
) : RecyclerView.Adapter<InvestasiAdapter.InvestasiViewHolder>() {

    inner class InvestasiViewHolder(val binding: ItemInvestasiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(investasi: Investasi) {
            binding.tvNama.text = investasi.nama
            binding.tvModal.text = "Modal: ${Hitung.formatRupiah(investasi.modal)}"
            binding.tvBunga.text = "Bunga: ${investasi.bungaTahunan}%"
            
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = sdf.parse(investasi.tanggalMulai)
                val sdOut = SimpleDateFormat("d MMM yyyy", Locale("id", "ID"))
                binding.tvTanggalMulai.text = "Mulai: ${sdOut.format(date ?: investasi.tanggalMulai)}"
            } catch (e: Exception) {
                binding.tvTanggalMulai.text = "Mulai: ${investasi.tanggalMulai}"
            }

            val hariIni = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(java.util.Calendar.getInstance().time)
            val hari = Hitung.hariAntara(investasi.tanggalMulai, hariIni)
            val nilaiSekarang = Hitung.nilaiSekarang(investasi.modal, investasi.bungaTahunan, hari)
            val keuntungan = nilaiSekarang - investasi.modal

            binding.tvNilaiSekarang.text = "Nilai: ${Hitung.formatRupiah(nilaiSekarang)}"
            binding.tvKeuntungan.text = "Keuntungan: ${Hitung.formatRupiah(keuntungan)}"

            if (keuntungan >= 0) {
                binding.tvKeuntungan.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
            } else {
                binding.tvKeuntungan.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
            }

            binding.root.setOnClickListener {
                onItemClick(investasi)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvestasiViewHolder {
        val binding = ItemInvestasiBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return InvestasiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InvestasiViewHolder, position: Int) {
        holder.bind(daftarInvestasi[position])
    }

    override fun getItemCount(): Int = daftarInvestasi.size
}
