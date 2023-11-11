package com.example.proyectofinal

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.zxing.integration.android.IntentIntegrator
import com.example.proyectofinal.databinding.FragmentCameraBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class Camera : Fragment() {

    private lateinit var binding: FragmentCameraBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* Si le pica escanear */
        binding.btnScan.setOnClickListener {
            startScanner()
        }

        /* Si le pica regresar */
        /* Quitar boton de regreso y información y poner boton de escaner */
        binding.btnBack.setOnClickListener {
            binding.btnScan.visibility = View.VISIBLE
            binding.btnBack.visibility = View.GONE
            binding.textView1.visibility = View.GONE
            binding.textView2.visibility = View.GONE
            binding.textView3.visibility = View.GONE
        }
    }

    /* Iniciar escnaeo */
    private fun startScanner() {
        val scanner = IntentIntegrator.forSupportFragment(this)
        scanner.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

            if (result.contents == null) {

                Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()

            } else {

                /* Conseguir Vistas */
                var btnScan2 = binding.btnScan
                var btnBack = binding.btnBack
                var view1 = binding.textView1
                var view2 = binding.textView2
                var view3 = binding.textView3

                /* Quitar boton de Scannear y poner boton de regreso y de textview de info */
                btnScan2.visibility = View.GONE
                btnBack.visibility = View.VISIBLE
                view1.visibility = View.VISIBLE
                view2.visibility = View.VISIBLE
                view3.visibility = View.VISIBLE

                getData(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    private fun displayData(post: Post) {
        val btnScan = binding.btnScan
        val btnBack = binding.btnBack
        val view1 = binding.textView1
        val view2 = binding.textView2
        val view3 = binding.textView3

        if (post.id == "NONE") {
            btnScan.visibility = View.VISIBLE
            btnBack.visibility = View.GONE
            view1.visibility = View.GONE
            view2.visibility = View.GONE
            view3.visibility = View.GONE
            Toast.makeText(requireContext(), "No se encontró en la base de datos 😣", Toast.LENGTH_SHORT).show()
            Log.i("LOG_ROBBY", "${post.id}, ${post.name}, ${post.tips} ")
        } else {
            Log.i("LOG_ROBBY", "${post.id}, ${post.name}, ${post.tips} ")
            view1.text = "ID: ${post.id}"
            view2.text = "nombre: ${post.name}"
            view3.text = "tip: ${post.tips}"
        }
    }

    private fun getData(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.i("LOG_ROBBY", "try")
                val call = getRetrofit().create(JsonPlaceholderApi::class.java)
                    .getPosts("http://35.208.119.80:5000/get_product/${id}")
                val post = call.body()
                requireActivity().runOnUiThread {
                    if (call.isSuccessful) {
                        if (post != null) {
                            displayData(post)
                        }
                    }
                    Log.i("LOG_ROBBY", "${call}")
                    Log.i("LOG_ROBBY", "${call.body()}")
                }

            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    Log.e("LOG_ROBBY", "Error: ${e.message}")
                }
            }
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://35.208.119.80:5000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}