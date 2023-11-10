package com.example.proyectofinal

import android.app.appsearch.GlobalSearchSession
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.StringBuilder
/*
TODO: Revisar que los registros insertados no sean duplicados por la llave primaria
      NO se hace automáticamente por el Room ya que el ID no es autoincremental
 */

class History(dbInput: AppDatabase) : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var recyclerView: RecyclerView
    private val db = dbInput

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        edtId = view.findViewById(R.id.edtId)
        edtProduct = view.findViewById(R.id.edtProduct)
        btnInsert = view.findViewById(R.id.btnInsert)
        btnShow = view.findViewById(R.id.btnShow)
        txtResults = view.findViewById(R.id.txtResults)

        btnInsert.setOnClickListener {
            val id = edtId.text.toString()
            val product = edtProduct.text.toString()
            if (id.isNotBlank()) {
                GlobalScope.launch(Dispatchers.IO) {
                    val historial = Historial(id = id, producto = product)
                    val result = db.historialDao().insert(historial)
                    launch(Dispatchers.Main) {

                    }
                }
            }
        }

        btnShow.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val historial = db.historialDao().getAllHistorial()
                launch(Dispatchers.Main) {
                    val output = StringBuilder()

                    for (hist in historial) {
                        output.append("ID: ${hist.id}, Nombre: ${hist.producto}")
                    }
                    txtResults.text = output.toString()
                }
            }
        }

        return view
    }
}