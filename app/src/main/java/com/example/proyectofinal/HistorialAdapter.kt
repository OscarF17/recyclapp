package com.example.proyectofinal

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.databinding.ItemHistorialBinding
import com.squareup.picasso.Picasso

class HistorialAdapter(private val historial: List<Historial>):
    RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>(){

    /* Cada elemento dentro del recyclerview tiene un botón que debe poder cambiar la interfaz del fragmento
     * Definimos una interfaz que será implementada por el fragmento
     * Esto permite comunicar cada botón dentro del recyclerview con el fragmento
     */
    interface OnButtonClickListener {
        fun onButtonClick(hist: Historial)
    }

    // Creamos un objeto que tendrá una instancia de la base de la interfaz
    var onButtonClickListener: OnButtonClickListener? = null

    inner class HistorialViewHolder(val binding: ItemHistorialBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var db: AppDatabase

        fun bind(hist: Historial) {
                var url = "http://35.208.119.80:5000/image/${hist.img}"
                Picasso.get().load(url).into(binding.historialImagen);
                binding.historialProducto.text = hist.producto
                binding.historialTipo.text = hist.tipo
                binding.btnInfo.setOnClickListener {
                    Log.i("LOGOSCAR", "CLICK AL BOTON: ${hist.id}, IMAGEN: ${hist.img}")
                    /* Cada item dentro del recyclerview tiene un botón que despliega los datos del elemento
                     * Bindear el botón de cada elemento con el el método de la interfaz
                     */
                    onButtonClickListener?.onButtonClick(hist)
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