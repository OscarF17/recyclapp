package com.example.proyectofinal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.proyectofinal.databinding.ActivityMainBinding

// Actividad principal
// Despliega la barra de navegación y el fragmento seleccionado
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Iniciar con el fragmento de la cámara abierto
        replaceFragment(Camera())

        // Ligar cada botón con un fragmento a desplegar
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

    // Desplegar fragmento
    private fun replaceFragment(fragment : Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}