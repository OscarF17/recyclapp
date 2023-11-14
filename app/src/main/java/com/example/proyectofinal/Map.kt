@file:Suppress("DEPRECATION")

package com.example.proyectofinal

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.proyectofinal.databinding.FragmentMapBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

// Fragmento para desplegar el mapa
class Map : Fragment(), OnMapReadyCallback, LocationListener,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private lateinit var binding: FragmentMapBinding
    private lateinit var mMap: GoogleMap
    private lateinit var mLastLocation: Location
    private var mCurrentLocationMarker: Marker? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Listas que contienen información sobre los puntos de reciclaje
    private val nameList: List<String> = listOf(
        "Reciclables San Pedro",
        "Planta de Reciclado Grupo AlEn",
        "Chatarra JRRR",
        "Centro de Reciclaje 1",
        "Centro de Reciclaje Servicios Públicos y Medio Ambiente"
    )
    private val latitudeList: List<Double> = listOf(
        25.67161164230018,
        25.67231021179866,
        25.6675139169062,
        25.671774618402647,
        25.66691687829407
    )
    private val longitudeList: List<Double> = listOf(
        -100.39913442329072,
        -100.43629443916835,
        -100.43234622756339,
        -100.39922539260367,
        -100.4103270005425
    )

    // Constante para solicitar permisos de ubicación
    companion object {
        private const val PERMISSIONS_REQUEST_LOCATION = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflar y devolver el diseño del fragmento
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener el fragmento del mapa
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        // Obtener la referencia al Spinner desde la vista
        val spinnerLocations: Spinner = binding.spinnerLocations

        // Configurar un adaptador para el Spinner
        val locationAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.location_names,
            android.R.layout.simple_spinner_item
        )
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLocations.adapter = locationAdapter

        // Inicializar el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Configuración de la solicitud de ubicación
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        // Configuración del evento de selección del Spinner
        spinnerLocations.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLocation = parent?.getItemAtPosition(position).toString()
                showLocationMarker(selectedLocation)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No se necesita implementación aquí
            }
        }
        // Iniciar el mapa asincrónicamente
        mapFragment.getMapAsync(this)
    }

    // Método llamado cuando el mapa está listo
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Verificar permisos de ubicación
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Construir el cliente de Google API para la ubicación
            buildGoogleApiClient()
            // Habilitar la capa de ubicación en el mapa
            mMap.isMyLocationEnabled = true

            // Obtener la última ubicación conocida y mover la cámara allí
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    onLocationChanged(location)
                }
            }
            // Mostrar marcadores de reciclaje en el mapa
            recyclerMarkers()
        } else {
            // Solicitar permiso de ubicación
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso concedido, inicializa los servicios de localización.
                    buildGoogleApiClient()
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    mMap.isMyLocationEnabled = true

                    // Obtén la última ubicación conocida y mueve la cámara allí
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            onLocationChanged(location)
                        }
                    }
                } else {
                    // Permiso denegado
                    Toast.makeText(
                        requireContext(),
                        "Permiso denegado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    // Método para construir el cliente de Google API para la ubicación
    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(requireContext())
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
        mGoogleApiClient!!.connect()
    }

    // Método llamado cuando cambia la ubicación del dispositivo
    override fun onLocationChanged(location: Location) {
        mLastLocation = location
        // Eliminar el marcador de ubicación actual
        if (mCurrentLocationMarker != null) {
            mCurrentLocationMarker!!.remove()
        }

        // Obtener la posición (latitud y longitud) de la ubicación
        val latLng = LatLng(location.latitude, location.longitude)

        // Mover la cámara al nuevo lugar
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.moveCamera(CameraUpdateFactory.zoomTo(18f))
    }

    // Método llamado cuando la conexión a la API de Google es exitosa
    override fun onConnected(p0: Bundle?) {
        // Configuración de la solicitud de ubicación
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        // Verificar permisos de ubicación
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Mostrar mensaje de carga del mapa
            // Toast.makeText(requireContext(), "Mapa cargado 🤠", Toast.LENGTH_SHORT).show()
        }
    }

    // Método llamado cuando la conexión a la API de Google se suspende
    override fun onConnectionSuspended(p0: Int) {
        // No se necesita implementación aquí
    }

    // Método llamado cuando la conexión a la API de Google falla
    override fun onConnectionFailed(p0: ConnectionResult) {
        // No se necesita implementación aquí
    }

    // Método para mostrar el marcador de una ubicación seleccionada en el Spinner
    private fun showLocationMarker(selectedLocation: String) {
        // Buscar la ubicación seleccionada en las listas y mostrar el marcador
        val index = nameList.indexOf(selectedLocation)
        if (index != -1) {
            val latitude = latitudeList[index]
            val longitude = longitudeList[index]
            val latLng = LatLng(latitude, longitude)

            mMap.clear() // Limpiar marcadores existentes
            mMap.addMarker(MarkerOptions().position(latLng).title(selectedLocation))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
        }
    }

    // Método para agregar marcadores al mapa basados en las listas de ubicaciones
    private fun recyclerMarkers() {
        for (index in nameList.indices) {
            val name = nameList[index]
            val latitude = latitudeList[index]
            val longitude = longitudeList[index]
            val latLng = LatLng(latitude, longitude)

            mMap.addMarker(MarkerOptions().position(latLng).title(name))
        }
    }
}