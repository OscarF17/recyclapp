package com.example.proyectofinal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.databinding.ItemHistorialBinding
import com.squareup.picasso.Picasso

// Adaptador de la clase historial
class HistorialAdapter(private val historial: List<Historial>):
    RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>(){

    /* Cada elemento dentro del recyclerview tiene un botón que debe poder cambiar la interfaz del
     * fragmento para mostrar la información del producto que contiene. Definimos una interfaz que
     * será implementada por el fragmento que permita a los elementos del recyclerview comunicarse
     * con este.
     */
    interface OnButtonClickListener {
        fun onButtonClick(hist: Historial)
    }

    // Creamos una variable del tipo de la interfaz para poder instanciarla y acceder a su método
    // Inicializada en null, el fragmento cambia el valor por un objeto con el método implementado
    var onButtonClickListener: OnButtonClickListener? = null

    // ViewHolder para el recyclerview
    inner class HistorialViewHolder(private val binding: ItemHistorialBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // Ligar cada elemento del RV con su informaciónn
        fun bind(hist: Historial) {
                // Obtener imagen del producto y desplegar
                val url = "http://35.208.119.80:5000/image/${hist.img}"
                Picasso.get().load(url).into(binding.historialImagen)
            binding.historialProducto.text = hist.producto
                binding.historialTipo.text = hist.tipo
                binding.btnInfo.setOnClickListener {
                    // Ligar cada botón con el método de la interfaz para cambiar el fragmento
                    onButtonClickListener?.onButtonClick(hist)
                }
            }
        }

    // Crear ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewtType: Int):  HistorialViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemHistorialBinding.inflate(layoutInflater, parent, false)
        return HistorialViewHolder(binding)
    }

    // Devolver tamaño del historial
    override fun getItemCount(): Int {
        return historial.size
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val item = historial[position]
        holder.bind(item)
    }
}