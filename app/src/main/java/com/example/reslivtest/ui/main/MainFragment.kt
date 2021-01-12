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
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.reslivtest.MainActivity
import com.example.reslivtest.R
import com.example.reslivtest.databinding.FragmentMainBinding
import com.example.reslivtest.util.*
import com.example.reslivtest.util.Constants.REQUEST_LOCATION_PERMISSION
import com.example.reslivtest.util.database.CityDatabase
import com.example.reslivtest.util.database.DataBaseLocationConverter
import com.example.reslivtest.util.database.LocationResponse
import com.example.reslivtest.util.extensions.checkViewVisibleOrGone
import com.example.reslivtest.util.extensions.showToastyError
import com.example.reslivtest.util.extensions.showToastyInfo
import com.example.reslivtest.util.network.WeatherResponse
import com.example.reslivtest.util.response.MainModelFactory
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
import java.util.concurrent.TimeUnit


class MainFragment :
    Fragment(R.layout.fragment_main),
    EasyPermissions.PermissionCallbacks,
    OnMapReadyCallback {

    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModelCity: MainViewModel
    private lateinit var locationResponse: LocationResponse
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

        val isWorkerEnable = PreferencesHelper.workerIsEnable(requireActivity())
        when(isWorkerEnable == "yes"){
            true -> {
                createPeriodicWorker()
            } else -> {
            WorkManager.getInstance(activity as MainActivity).cancelUniqueWork(Constants.WORKER_TAG)
            }
        }
    }


    private fun checkLocation() {
        val manager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertLocation()
        }
        LocationHelper(context as MainActivity).updateLocation()
        loadLocationWeatherTODatabase()

    }

    private fun createPeriodicWorker(){
        val periodicRequest = PeriodicWorkRequest
            .Builder(WorkerManager::class.java, 1, TimeUnit.HOURS).build()
        val workManager = WorkManager.getInstance(activity as MainActivity)
        workManager
            .getWorkInfoByIdLiveData(periodicRequest.id)
            .observe(viewLifecycleOwner, Observer { workStatus ->
                if (workStatus != null && workStatus.state == WorkInfo.State.SUCCEEDED) {
                    loadLocationWeatherTODatabase()
                }
            })
        workManager.enqueueUniquePeriodicWork(
            Constants.WORKER_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
    }

    private fun loadLocationWeatherTODatabase() {
        viewModelCity.lastDBLocationLiveData.observe(
            viewLifecycleOwner, Observer { location ->
                location?.let {
                    this.locationResponse = location
                    binding.weatherItem.locationResponse = locationResponse
                    loadWeather(locationResponse)
                    viewModelCity.saveCurrentLocationInDB(locationResponse)
                    val latLng = LatLng(location.latitude, location.longitude)
                    viewModelCity.setPositionOnMapView(latLng)
                    viewModelCity.lastDBLocationLiveData.removeObservers(viewLifecycleOwner)
                }
            })
        viewModelCity.loadLocationFromId(1)
    }

    private fun showAlertLocation() {
        val dialog = AlertDialog.Builder(context as Activity)
        dialog.setMessage(getString(R.string.location_is_disable))
        dialog.setPositiveButton(getString(R.string.title_settings)) { _, _ ->
            val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(myIntent)
        }
        dialog.setNegativeButton(getString(R.string.cancell)) { _, _ ->
            activity?.finish()
        }
        dialog.setCancelable(true)
        dialog.show()
    }


    @SuppressLint("SetTextI18n")
    private fun loadWeather(locationResponse: LocationResponse) {
        viewModelCity.loadWeather(locationResponse.latitude, locationResponse.longitude)
            ?.observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is WeatherResponse.Success -> {
                        response.data?.let { weatherResponse ->
                            showProgress(false, binding.weatherItem.progressBarWeather)
                            activity?.let {
                                DataBaseLocationConverter(it)
                                    .convertDataToDataBase(
                                        weatherResponse
                                    )
                            }
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
                getString(R.string.need_access_to_location),
                REQUEST_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.need_access_to_location),
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
        val style = MapStyleOptions.loadRawResourceStyle(this.context, R.raw.map_style_dark)
        googleMap?.apply {
            setPadding(0, 0, 0, 200)
            uiSettings.isZoomControlsEnabled = false
            uiSettings.setAllGesturesEnabled(false)
            setMapStyle(MapStyleOptions.loadRawResourceStyle(activity, R.raw.map_style))
            uiSettings.isRotateGesturesEnabled = false
            when (PreferencesHelper.nightModeEnable(requireActivity())){
                true ->
                    setMapStyle(style)
            }

        }
        viewModelCity.locationLiveData.observe(viewLifecycleOwner, Observer { ltLng ->
            ltLng?.let {
                this.latLng = ltLng
                googleMap?.apply {
                    addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(getString(R.string.you_is_here))
                    )
                    moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))

                }
            }
        })
    }

    override fun onPause() {
        map?.onPause()
        super.onPause()

    }


    override fun onResume() {
        map?.onResume()
        super.onResume()
        LocationHelper(context as MainActivity).updateLocation()
    }

    override fun onLowMemory() {
        map?.onLowMemory()
        super.onLowMemory()
    }
}