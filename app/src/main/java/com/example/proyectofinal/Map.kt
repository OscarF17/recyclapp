package com.example.proyectofinal

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class Map : Fragment(), OnMapReadyCallback {

    private lateinit var view: View
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_map, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val searchView: SearchView
        searchView = view.findViewById(R.id.mapSearch)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("MapFragment", "Query submitted: $query")
                query?.let { location ->
                    searchLocation(location)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("MapFragment", "Query text changed: $newText")
                return true
            }
        })

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Habilitar la capa de tráfico (opcional)
        map.isTrafficEnabled = true

        // Habilitar el botón de ubicación actual
        map.isMyLocationEnabled = true

        // Coordenadas de la UDEM Monterrey
        val udemCoordinates = LatLng(25.6610, -100.4202)

        // Agregar marcador en la UDEM
        val marker = MarkerOptions().position(udemCoordinates).title("UDEM Monterrey")
        map.addMarker(marker)

        // Centrar la cámara en la UDEM con un nivel de zoom de 18
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(udemCoordinates, 18f),
            4000,
            null
        )
    }

    private fun searchLocation(location: String) {
        val geocoder = Geocoder(requireContext())
        try {
            val addressList: List<Address>? = geocoder.getFromLocationName(location, 1)
            addressList?.let {
                if (it.isNotEmpty()) {
                    val address = it[0]
                    val latitude = address.latitude
                    val longitude = address.longitude
                    Log.d("MapFragment", "Latitude: $latitude, Longitude: $longitude")

                    // Ahora puedes usar latitude y longitude según tus necesidades.

                    val searchedLocation = LatLng(latitude, longitude)
                    map.addMarker(MarkerOptions().position(searchedLocation).title(location))
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(searchedLocation, 15f))
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}