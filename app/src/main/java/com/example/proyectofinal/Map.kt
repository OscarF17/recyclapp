package com.example.proyectofinal

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
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

class Map : Fragment(), OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private lateinit var view: View
    private lateinit var mMap: GoogleMap
    internal lateinit var mLastLocation: Location
    internal var mCurrentLocationMarker: Marker? = null
    internal var mGoogleApiClient: GoogleApiClient? = null
    internal lateinit var  mLocationRequest: LocationRequest

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
        val searchEditText: EditText = view.findViewById(R.id.mapSearch)
        val searchButton: Button = view.findViewById(R.id.searchButton)

        searchButton.setOnClickListener {
            searchLocation(searchEditText)
        }

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                buildGoogleApiClient()
                mMap!!.isMyLocationEnabled = true
            }
        } else {
            buildGoogleApiClient()
            mMap!!.isMyLocationEnabled = true
        }

        recyclerMarkers()
    }

    protected fun buildGoogleApiClient() {
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
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("Current Position")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        mCurrentLocationMarker = mMap!!.addMarker(markerOptions)

        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap!!.moveCamera(CameraUpdateFactory.zoomTo(11f))

        if (mGoogleApiClient != null) {
           LocationServices.getFusedLocationProviderClient(requireContext())
        }
    }

    override fun onConnected(p0: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(requireContext())
        }
    }


    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    fun searchLocation(view: View) {
        val locationSearch: EditText = view.findViewById(R.id.mapSearch)
        val location: String = locationSearch.text.toString().trim()
        var addressList: List<Address>? = null

        if (location.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Please provide a location", Toast.LENGTH_SHORT).show()
        } else {
            val geocoder = Geocoder(requireContext())
            try {
                addressList = geocoder.getFromLocationName(location, 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (addressList != null && addressList.isNotEmpty()) {
                val address = addressList[0]
                val latlng = LatLng(address.latitude, address.longitude)
                mMap?.addMarker(MarkerOptions().position(latlng).title(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
                mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 18f))

            } else {
                Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun recyclerMarkers() {
        val nameList: List<String> = listOf("Reciclables San Pedro", "Planta de Reciclado Grupo AlEn", "Chatarra JRRR", "Centro de Reciclaje 1", "Centro de Reciclaje Servicios Públicos y Medio Ambiente")
        val latitudeList: List<Double> = listOf(25.67161164230018, 25.67231021179866, 25.6675139169062, 25.671774618402647, 25.66691687829407)
        val longitudeList: List<Double> = listOf(-100.39913442329072, -100.43629443916835, -100.43234622756339, -100.39922539260367, -100.4103270005425)

        for (index in nameList.indices) {
            val name = nameList[index]
            val latitude = latitudeList[index]
            val longitude = longitudeList[index]
            val latLng = LatLng(latitude, longitude)

            mMap?.addMarker(MarkerOptions().position(latLng).title(name))
        }
    }
}