package com.example.reslivtest.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.reslivtest.MainActivity
import com.example.reslivtest.R
import com.example.reslivtest.databinding.FragmentMainBinding
import com.example.reslivtest.util.Constants.REQUEST_LOCATION_PERMISSION
import com.example.reslivtest.util.LocationUtility
import com.example.reslivtest.util.database.LocationData
import com.example.reslivtest.util.extensions.checkViewVisibleOrGone
import com.example.reslivtest.util.extensions.showToastyInfo
import com.example.reslivtest.util.repo.WeatherResponse
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_main.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*


class MainFragment :
        Fragment(R.layout.fragment_main),
        EasyPermissions.PermissionCallbacks,
OnMapReadyCallback{

    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModelCity: MainViewModel
    private lateinit var locationManager: LocationManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationData: LocationData
    private var mMap: GoogleMap? = null
    private lateinit var latLng: LatLng

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)
        requestPermissions()
        viewModelCity = (activity as MainActivity).viewModelCity
        locationManager =
                activity?.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context as Activity)
        checkLocation()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun loadLocationWeatherFromDatabase() {
        viewModelCity.loadLocationFromId(1)
        viewModelCity.lastLocationLiveData.observe(
            viewLifecycleOwner, Observer { location ->
                location?.let {
                    this.locationData = location
                    loadWeather(location.latitude, location.longitude)

                }
            })
    }


    private fun checkLocation() {
        val manager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertLocation()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context as Activity)
        updateLocation()
    }

    private fun showAlertLocation() {
        val dialog = AlertDialog.Builder(context as Activity)
        dialog.setMessage("В настройках вашего местоположения установлено значение Выкл., Пожалуйста, включите местоположение, чтобы использовать это приложение.")
        dialog.setPositiveButton("Настройки") { _, _ ->
            val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(myIntent)
        }
        dialog.setNegativeButton("Отмена") { _, _ ->
            activity?.finish()
        }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }



    @SuppressLint("MissingPermission")
    fun updateLocation() {
        if (LocationUtility.hasLocationPermission(requireContext())) {
            val request = LocationRequest().apply {
                interval = 50000
                fastestInterval = 50000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            fusedLocationClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    private val locationCallback = object : LocationCallback() {
        @SuppressLint("SetTextI18n")
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            if (locationResult.locations.isNotEmpty()) {
                val addresses: List<Address>?
                val geoCoder = Geocoder(activity?.applicationContext, Locale.getDefault())
                addresses = geoCoder.getFromLocation(
                    locationResult.lastLocation.latitude,
                    locationResult.lastLocation.longitude,
                    1
                )
                if (addresses != null && addresses.isNotEmpty()) {
                    val latitude: Double = addresses[0].latitude
                    val longitude: Double = addresses[0].longitude
                    val data = LocationData(1, latitude, longitude)
                    val loc = LatLng(latitude, longitude)
                    viewModelCity.loadLocationFromMap(loc)
                    viewModelCity.saveCurrentLocation(data)
                    loadWeather(latitude, longitude)


                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadWeather(latitude: Double, longitude: Double) {
        viewModelCity.loadWeather(latitude, longitude)
                ?.observe(viewLifecycleOwner, Observer { response ->
                    when (response) {
                        is WeatherResponse.Success -> {
                            response.data?.let { weatherResponse ->
                                showProgress(false, binding.weatherItem.progressBarWeather)
                                binding.weatherItem.temperature = weatherResponse
                            }
                            Log.d("TAG", "WeatherResponse")
                            activity?.showToastyInfo("Данные загружены!")
                        }
                        is WeatherResponse.Error -> {
                            response.message?.let { message ->
                                showProgress(false, binding.weatherItem.progressBarWeather)
//                        showErrorMessage()
                            }
                        }
                        is WeatherResponse.Loading -> {
                            showProgress(show = true, binding.weatherItem.progressBarWeather)
                        }
                    }
                })
    }

    private fun showProgress(show: Boolean, view: View) {
        checkViewVisibleOrGone(show, view)
    }

    private fun requestPermissions() {
        if (LocationUtility.hasLocationPermission(requireContext())) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "Чтобы использовать это приложение, вам необходимо принять разрешения на местоположение",
                REQUEST_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Чтобы использовать это приложение, вам необходимо принять разрешения на местоположение",
                REQUEST_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            //AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()

        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        AppSettingsDialog.Builder(this).build().show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.mMap = googleMap
        googleMap?.apply {
            setPadding(0, 0, 0, 200)
            uiSettings.isZoomControlsEnabled = false
            uiSettings.isMapToolbarEnabled = false //Панель инструментов карты
            uiSettings.isCompassEnabled = false
            setMapStyle(MapStyleOptions.loadRawResourceStyle(activity, R.raw.map_style))
            uiSettings.isRotateGesturesEnabled = false
        }
        viewModelCity.locationLiveData.observe(viewLifecycleOwner, Observer { ltLng ->
            ltLng?.let {
                this.latLng = ltLng
                googleMap?.apply {
                    addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title("Marker in Sydney")
                    )
                    moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                }
            }
        })
    }

    override fun onPause() {
        map?.onPause()
        super.onPause()
        stopLocationUpdates()
    }


    override fun onResume() {
        map?.onResume()
        super.onResume()
        checkLocation()
    }

    override fun onLowMemory() {
        map?.onLowMemory()
        super.onLowMemory()
    }
}