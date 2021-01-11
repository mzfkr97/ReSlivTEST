package com.example.reslivtest.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.reslivtest.MainActivity
import com.example.reslivtest.R
import com.example.reslivtest.databinding.FragmentMainBinding
import com.example.reslivtest.util.Constants.REQUEST_LOCATION_PERMISSION
import com.example.reslivtest.util.LocationUtility
import com.example.reslivtest.util.WorkerManager
import com.example.reslivtest.util.database.CityDatabase
import com.example.reslivtest.util.database.LocationData
import com.example.reslivtest.util.extensions.checkViewVisibleOrGone
import com.example.reslivtest.util.extensions.showToastyError
import com.example.reslivtest.util.extensions.showToastyInfo
import com.example.reslivtest.util.repo.MainModelFactory
import com.example.reslivtest.util.repo.WeatherResponse
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


class MainFragment :
    Fragment(R.layout.fragment_main),
    EasyPermissions.PermissionCallbacks,
    OnMapReadyCallback {

    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModelCity: MainViewModel
    private lateinit var locationData: LocationData
    private var mMap: GoogleMap? = null
    private lateinit var latLng: LatLng

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)

        val mainRepository = MainRepository(CityDatabase(activity as MainActivity))
        val mainViewModelFactory = MainModelFactory(mainRepository)
        viewModelCity = ViewModelProvider(this, mainViewModelFactory).get(MainViewModel::class.java)
        requestPermissions()
        checkLocation()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }


    private fun checkLocation() {
        val manager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertLocation()
        }

        val workRequest = OneTimeWorkRequest.Builder(WorkerManager::class.java).build()
        val mWorkManager = WorkManager.getInstance(activity as MainActivity)
        mWorkManager.getWorkInfoByIdLiveData(workRequest.id).observe(viewLifecycleOwner,
            Observer { workStatus ->
                if (workStatus != null && workStatus.state == WorkInfo.State.SUCCEEDED) {
                    loadLocationWeatherFromDatabase()
                }
            })
        mWorkManager.beginWith(workRequest).enqueue()
    }

    private fun loadLocationWeatherFromDatabase() {
        viewModelCity.lastLocationLiveData.observe(
            viewLifecycleOwner, Observer { location ->
                location?.let {
                    this.locationData = location
                    loadWeather(locationData)
                    viewModelCity.saveCurrentLocation(locationData)
                    val latLng = LatLng(location.latitude, location.longitude)
                    viewModelCity.loadLatLngFromMap(latLng)
                    viewModelCity.lastLocationLiveData.removeObservers(viewLifecycleOwner)
                }
            })
        viewModelCity.loadLocationFromId(1)
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
        dialog.setCancelable(true)
        dialog.show()
    }


    @SuppressLint("SetTextI18n")
    private fun loadWeather(locationData: LocationData) {
        viewModelCity.loadWeather(locationData)
            ?.observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is WeatherResponse.Success -> {
                        response.data?.let { weatherResponse ->
                            showProgress(false, binding.weatherItem.progressBarWeather)
                            binding.weatherItem.temperature = weatherResponse
                            Log.d("TAG", "WeatherResponse")
                            activity?.showToastyInfo(getString(R.string.data_is_loading))
                        }
                    }
                    is WeatherResponse.Error -> {
                        response.message?.let { message ->
                            showProgress(false, binding.weatherItem.progressBarWeather)
                            activity?.showToastyError(getString(R.string.error_from_download_data))
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
            uiSettings.setAllGesturesEnabled(false)
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
                    moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))

                }
            }
        })
    }

    override fun onPause() {
        map?.onPause()
        super.onPause()
        // stopLocationUpdates()
    }


    override fun onResume() {
        map?.onResume()
        super.onResume()
        //checkLocation()
    }

    override fun onLowMemory() {
        map?.onLowMemory()
        super.onLowMemory()
    }
}