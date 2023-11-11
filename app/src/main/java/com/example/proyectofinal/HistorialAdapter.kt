package com.example.proyectofinal

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.databinding.ItemHistorialBinding

class HistorialAdapter(private val historial: List<Historial>):
    RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>(){

    inner class HistorialViewHolder(val binding: ItemHistorialBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var db: AppDatabase

        fun bind(hist: Historial) {
                binding.historialProducto.text = hist.producto
                binding.historialTipo.text = hist.tipo
                binding.btnInfo.setOnClickListener {
                    // TODO: Llamar al fragmento usando el id
                    Log.i("LOGOSCAR", "CLICK AL BOTON: ${hist.producto}")
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewtType: Int):  HistorialViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemHistorialBinding.inflate(layoutInflater, parent, false)
        return HistorialViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return historial.size
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val item = historial[position]
        holder.bind(item)
    }
}