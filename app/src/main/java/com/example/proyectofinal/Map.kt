@file:Suppress("DEPRECATION")

package com.example.proyectofinal

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class Map : Fragment(), OnMapReadyCallback, LocationListener,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private lateinit var binding: FragmentMapBinding
    private lateinit var mMap: GoogleMap
    private lateinit var mLastLocation: Location
    private var mCurrentLocationMarker: Marker? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val PERMISSIONS_REQUEST_LOCATION = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        val searchEditText: SearchView = binding.mapSearch

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        searchEditText.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    searchLocation(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            buildGoogleApiClient()
            mMap.isMyLocationEnabled = true

            // Obtén la última ubicación conocida y mueve la cámara allí
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    onLocationChanged(location)
                }
            }

            recyclerMarkers()
        } else {
            // Request location permission
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
                    // Permission granted, initialize location services
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
                    // Permission denied, handle accordingly (e.g., show a message to the user)
                    Toast.makeText(
                        requireContext(),
                        "Location permission denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(requireContext())
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
        mGoogleApiClient!!.connect()
    }

    override fun onLocationChanged(location: Location) {
        mLastLocation = location
        if (mCurrentLocationMarker != null) {
            mCurrentLocationMarker!!.remove()
        }

        val latLng = LatLng(location.latitude, location.longitude)

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.moveCamera(CameraUpdateFactory.zoomTo(18f))
    }

    override fun onConnected(p0: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

        }
    }

    override fun onConnectionSuspended(p0: Int) {}

    override fun onConnectionFailed(p0: ConnectionResult) {}

    private fun searchLocation(query: String) {
        var addressList: List<Address>? = null

        if (query.isEmpty()) {
            Toast.makeText(requireContext(), "Introduzca un lugar", Toast.LENGTH_SHORT).show()
        } else {
            val geocoder = Geocoder(requireContext())
            try {
                addressList = geocoder.getFromLocationName(query, 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (!addressList.isNullOrEmpty()) {
                val address = addressList[0]
                val latlng = LatLng(address.latitude, address.longitude)
                mMap.addMarker(
                    MarkerOptions().position(latlng).title(query)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                )
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 18f))
            } else {
                Toast.makeText(requireContext(), "Localización no encontrada", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun recyclerMarkers() {
        val nameList: List<String> = listOf(
            "Reciclables San Pedro",
            "Planta de Reciclado Grupo AlEn",
            "Chatarra JRRR",
            "Centro de Reciclaje 1",
            "Centro de Reciclaje Servicios Públicos y Medio Ambiente"
        )
        val latitudeList: List<Double> = listOf(
            25.67161164230018,
            25.67231021179866,
            25.6675139169062,
            25.671774618402647,
            25.66691687829407
        )
        val longitudeList: List<Double> = listOf(
            -100.39913442329072,
            -100.43629443916835,
            -100.43234622756339,
            -100.39922539260367,
            -100.4103270005425
        )

        for (index in nameList.indices) {
            val name = nameList[index]
            val latitude = latitudeList[index]
            val longitude = longitudeList[index]
            val latLng = LatLng(latitude, longitude)

            mMap.addMarker(MarkerOptions().position(latLng).title(name))
        }
    }
}