package com.example.productivity.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.productivity.Data.Transaksi
import com.example.productivity.Util.Hitung
import com.example.productivity.databinding.ItemTransaksiBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TransaksiAdapter(
    private val daftarTransaksi: List<Transaksi>,
    private val onItemClick: (Transaksi) -> Unit
) : RecyclerView.Adapter<TransaksiAdapter.TransaksiViewHolder>() {

    inner class TransaksiViewHolder(val binding: ItemTransaksiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(transaksi: Transaksi) {
            binding.tvKategori.text = transaksi.kategori
            binding.tvJumlah.text = Hitung.formatRupiah(transaksi.jumlah)
            
            if (transaksi.jenis == "pemasukan") {
                binding.tvJumlah.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
            } else {
                binding.tvJumlah.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
            }

            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = sdf.parse(transaksi.tanggal)
                val sdOut = SimpleDateFormat("d MMM yyyy", Locale("id", "ID"))
                binding.tvTanggal.text = sdOut.format(date ?: transaksi.tanggal)
            } catch (e: Exception) {
                binding.tvTanggal.text = transaksi.tanggal
            }

            if (transaksi.catatan.isNotEmpty()) {
                binding.tvCatatan.text = transaksi.catatan
                binding.tvCatatan.visibility = android.view.View.VISIBLE
            } else {
                binding.tvCatatan.visibility = android.view.View.GONE
            }

            binding.root.setOnClickListener {
                onItemClick(transaksi)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaksiViewHolder {
        val binding = ItemTransaksiBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TransaksiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransaksiViewHolder, position: Int) {
        holder.bind(daftarTransaksi[position])
    }

    override fun getItemCount(): Int = daftarTransaksi.size
}
