package com.example.productivity.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.productivity.Data.Tugas
import com.example.productivity.Util.Hitung
import com.example.productivity.databinding.ItemTugasBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TugasAdapter(
    private val daftarTugas: List<Tugas>,
    private val onItemClick: (Tugas) -> Unit
) : RecyclerView.Adapter<TugasAdapter.TugasViewHolder>() {

    inner class TugasViewHolder(val binding: ItemTugasBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tugas: Tugas) {
            binding.tvJudul.text = tugas.judul
            binding.tvLabel.text = Hitung.labelKuadran(tugas.mendesak, tugas.penting)
            
            if (tugas.tenggat.isNotEmpty()) {
                try {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))
                    val date = sdf.parse(tugas.tenggat)
                    val sdOut = SimpleDateFormat("d MMM yyyy", Locale("id", "ID"))
                    binding.tvTenggat.text = sdOut.format(date ?: tugas.tenggat)
                } catch (e: Exception) {
                    binding.tvTenggat.text = tugas.tenggat
                }
            } else {
                binding.tvTenggat.text = "-"
            }
            
            val progress = if (daftarTugas.isNotEmpty()) 100 else 0
            binding.progressBar.progress = progress
            binding.tvPersen.text = "$progress%"
            
            if (tugas.tujuanHarada.isNotEmpty()) {
                binding.tvTujuanHarada.text = tugas.tujuanHarada
                binding.tvTujuanHarada.visibility = android.view.View.VISIBLE
            } else {
                binding.tvTujuanHarada.visibility = android.view.View.GONE
            }
            
            binding.root.setOnClickListener {
                onItemClick(tugas)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TugasViewHolder {
        val binding = ItemTugasBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TugasViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TugasViewHolder, position: Int) {
        holder.bind(daftarTugas[position])
    }

    override fun getItemCount(): Int = daftarTugas.size
}
