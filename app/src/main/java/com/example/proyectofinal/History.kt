package com.example.proyectofinal

import android.annotation.SuppressLint
import android.app.appsearch.GlobalSearchSession
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import androidx.room.Room
import com.example.proyectofinal.databinding.FragmentCameraBinding
import com.example.proyectofinal.databinding.FragmentHistoryBinding
import com.squareup.picasso.Picasso
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Fragmento para ver el historial
class History() : Fragment(), HistorialAdapter.OnButtonClickListener {
    private lateinit var db: AppDatabase
    lateinit var adapter: HistorialAdapter
    private val historial = mutableListOf<Historial>() // Datos para desplegar
    private lateinit var binding: FragmentHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        adapter = HistorialAdapter(historial)

        // Asociar el onButtonClickListener del adaptador con esta clase (y su método ya implementado)
        adapter.onButtonClickListener = this

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // Botón para regresar
        // Esconder información del producto, mostrar recyclerview y título
        binding.btnBack.setOnClickListener {
            binding.historyTitle.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.VISIBLE

            binding.btnBack.visibility = View.GONE
            binding.textView1.visibility = View.GONE
            binding.textView2.visibility = View.GONE
            binding.textView3.visibility = View.GONE
            binding.imageView.visibility = View.GONE
        }

        db = Room.databaseBuilder(requireContext(),
            AppDatabase::class.java, "Recyclapp.db").build()

        // Obtener historial de la base de datos y desplegar
        GlobalScope.launch(Dispatchers.IO) {
            val hist = db.historialDao().getAllHistorial()

            launch(Dispatchers.Main) {
                historial.addAll(hist)
                adapter.notifyDataSetChanged()
            }
        }
        return binding.root
    }

    // Implementar el método de la interfaz del adaptador
    // Cambia el fragmento para esconder el RV y mostrar la información del producto seleccionado
    override fun onButtonClick(hist: Historial) {
        // Conseguir vistas
        val btnBack = binding.btnBack
        val view1 = binding.textView1
        val view2 = binding.textView2
        val view3 = binding.textView3
        val imageView = binding.imageView
        val historyTitle = binding.historyTitle

        // Esconder título y recyclerview
        historyTitle.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE

        // Mostrar botón para regresar, texto e imagen
        btnBack.visibility = View.VISIBLE
        view1.visibility = View.VISIBLE
        view2.visibility = View.VISIBLE
        view3.visibility = View.VISIBLE
        imageView.visibility = View.VISIBLE

        // Obtener datos del servidor y desplegarlos
        getData(hist.id)
    }

    // Obtener información del producto seleccionado
    private fun getData(id: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val query = db.historialDao().getHistorialById(id)
            // Obtener primer y único resultado del query
            val info = query[0]
            launch(Dispatchers.Main) {
                displayData(info)
            }
        }
    }

    // Desplegar datos obtenidos
    @SuppressLint("SetTextI18n")
    private fun displayData(post: Historial) {
        // Obtener vistas
        val view1 = binding.textView1
        val view2 = binding.textView2
        val view3 = binding.textView3
        val imageView = binding.imageView

        // Cargar datos
        val url = "http://35.208.119.80:5000/image/${post.img}"
        Picasso.get().load(url).into(imageView)
        view1.text = "ID: ${post.id}"
        view2.text = post.producto
        view3.text = post.tips
    }
}