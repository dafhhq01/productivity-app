package com.example.productivity.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.productivity.Fragments.DashboardFragment
import com.example.productivity.Fragments.TugasFragment
import com.example.productivity.Fragments.TransaksiFragment
import com.example.productivity.Fragments.BudgetFragment
import com.example.productivity.Fragments.InvestasiFragment
import com.example.productivity.R
import com.example.productivity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            loadFragment(DashboardFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> loadFragment(DashboardFragment())
                R.id.nav_tugas -> loadFragment(TugasFragment())
                R.id.nav_transaksi -> loadFragment(TransaksiFragment())
                R.id.nav_budget -> loadFragment(BudgetFragment())
                R.id.nav_investasi -> loadFragment(InvestasiFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
