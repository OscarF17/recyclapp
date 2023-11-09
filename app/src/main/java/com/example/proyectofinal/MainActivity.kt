package com.example.proyectofinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.example.proyectofinal.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Camera())

        db = Room.databaseBuilder(applicationContext,
            AppDatabase::class.java, "Recyclapp.db").build()

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.bottom_camera -> replaceFragment(Camera())
                R.id.bottom_history -> replaceFragment(History())
                R.id.bottom_map -> replaceFragment(Map())
                R.id.bottom_settings -> replaceFragment(Settings())

                else -> {

                }
            }
            true
        }


    }

    private fun replaceFragment(fragment : Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}