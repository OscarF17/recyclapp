package com.example.proyectofinal

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import androidx.room.Room

/*
TODO: Revisar que los registros insertados no sean duplicados por la llave primaria
      NO se hace automáticamente por el Room ya que el ID no es autoincremental
 */

class History(dbInput: AppDatabase) : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var db: AppDatabase
    lateinit var adapter: HistorialAdapter
    private val historial = mutableListOf<Historial>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = HistorialAdapter(historial)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        Log.i("LOGOSCAR", "Reycler View inicializado")

        db = Room.databaseBuilder(requireContext(),
            AppDatabase::class.java, "Recyclapp.db").build()

        GlobalScope.launch(Dispatchers.IO) {
            val hist = db.historialDao().getAllHistorial()

            launch(Dispatchers.Main) {
                historial.addAll(hist)
                adapter.notifyDataSetChanged()
            }
        }

        return view
    }

    private fun getData() {
        Log.i("LOGOSCAR", "Obteniendo datos...")
        GlobalScope.launch(Dispatchers.IO) {
            Log.i("LOGOSCAR", "Leyendo base de datos...")
            val hist = db.historialDao().getAllHistorial()
            Log.i("LOGOSCAR", "Base de datos leida")
            launch(Dispatchers.Main) {
                Log.i("LOGOSCAR", "Guardando historial...")
                historial.addAll(hist)
                Log.i("LOGOSCAR", "Actualizando dataset...")
                adapter.notifyDataSetChanged()
                Log.i("LOGOSCAR", "Exito")
            }

        }
    }
}