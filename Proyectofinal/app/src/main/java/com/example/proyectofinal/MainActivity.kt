package com.example.proyectofinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.selectedItemId = R.id.bottom_scanner

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_scanner -> {
                    return@setOnItemSelectedListener true
                }
                R.id.bottom_history -> {
                    startNewActivity(HistoryActivity::class.java)
                    return@setOnItemSelectedListener true
                }
                R.id.bottom_map -> {
                    startNewActivity(MapActivity::class.java)
                    return@setOnItemSelectedListener true
                }
                R.id.bottom_settings -> {
                    startNewActivity(SettingsActivity::class.java)
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }
    }

    private fun startNewActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
}