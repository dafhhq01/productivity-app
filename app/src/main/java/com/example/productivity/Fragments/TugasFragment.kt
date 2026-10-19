package com.example.productivity.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.productivity.Activities.TugasFormActivity
import com.example.productivity.Adapters.TugasAdapter
import com.example.productivity.Data.AppDatabase
import com.example.productivity.databinding.FragmentTugasBinding
import kotlinx.coroutines.launch

class TugasFragment : Fragment() {
    private var _binding: FragmentTugasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTugasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.rvTugas.layoutManager = LinearLayoutManager(requireContext())
        
        binding.fabTambah.setOnClickListener {
            val intent = Intent(requireContext(), TugasFormActivity::class.java)
            intent.putExtra("id", -1)
            startActivity(intent)
        }
        
        loadTugas()
    }

    override fun onResume() {
        super.onResume()
        loadTugas()
    }

    private fun loadTugas() {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            val daftarTugas = db.tugasDao().getAll()
            
            if (daftarTugas.isEmpty()) {
                binding.rvTugas.visibility = View.GONE
                binding.tvKosong.visibility = View.VISIBLE
            } else {
                binding.rvTugas.visibility = View.VISIBLE
                binding.tvKosong.visibility = View.GONE
                
                val adapter = TugasAdapter(daftarTugas) { tugas ->
                    val intent = Intent(requireContext(), TugasFormActivity::class.java)
                    intent.putExtra("id", tugas.id)
                    startActivity(intent)
                }
                binding.rvTugas.adapter = adapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
