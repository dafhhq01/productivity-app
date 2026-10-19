package com.example.productivity.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.productivity.Data.BudgetRingkasan
import com.example.productivity.Util.Hitung
import com.example.productivity.databinding.ItemBudgetBinding

class BudgetAdapter(
    private val daftarBudget: List<BudgetRingkasan>,
    private val onItemClick: (BudgetRingkasan) -> Unit
) : RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    inner class BudgetViewHolder(val binding: ItemBudgetBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(budget: BudgetRingkasan) {
            binding.tvKategori.text = budget.kategori
            binding.tvJumlah.text = Hitung.formatRupiah(budget.budget)
            binding.tvTerpakai.text = "Terpakai: ${Hitung.formatRupiah(budget.terpakai)}"
            
            binding.tvSisa.text = "Sisa: ${Hitung.formatRupiah(budget.sisa)}"
            
            if (budget.sisa < 0) {
                binding.tvSisa.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
            } else {
                binding.tvSisa.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
            }

            binding.root.setOnClickListener {
                onItemClick(budget)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val binding = ItemBudgetBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BudgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        holder.bind(daftarBudget[position])
    }

    override fun getItemCount(): Int = daftarBudget.size
}
