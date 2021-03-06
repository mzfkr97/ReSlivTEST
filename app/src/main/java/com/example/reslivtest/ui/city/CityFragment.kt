package com.example.reslivtest.ui.city

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reslivtest.MainActivity
import com.example.reslivtest.R
import com.example.reslivtest.databinding.FragmentCityBinding
import com.example.reslivtest.ui.city.adapter.CityAdapter
import com.example.reslivtest.util.database.CityData
import com.example.reslivtest.util.database.CityDatabase
import com.example.reslivtest.util.extensions.checkViewVisibleOrGone
import com.example.reslivtest.util.extensions.showToastyError
import com.example.reslivtest.util.extensions.showToastyInfo
import com.example.reslivtest.util.network.WeatherResponse
import com.google.android.material.snackbar.Snackbar
import java.util.*

class CityFragment :
    Fragment(R.layout.fragment_city) {

    private lateinit var cityAdapter: CityAdapter
    private var _binding: FragmentCityBinding? = null
    private lateinit var viewModel: CityViewModel
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCityBinding.bind(view)
        val mainRepository = CityRepository(CityDatabase(activity as MainActivity))
        val mainViewModelFactory = CityModelFactory(mainRepository)
        viewModel =  ViewModelProvider(this, mainViewModelFactory).get(CityViewModel::class.java)

        updateUI()

        binding.buttonAddCity.setOnClickListener {
            val number = binding.addCityEditText.text.toString()
            if (number.isEmpty()) {
                context?.showToastyError(getString(R.string.add_city))
                return@setOnClickListener
            }
            val city = CityData(id = UUID.randomUUID(), number)
            viewModel.addCity(city)
            loadWeather(city.cityName)
            binding.addCityEditText.clear()
        }
        itemTouchHelperSetup(view)

    }


    private fun updateUI(){
        viewModel.weatherListLiveData.observe(
            viewLifecycleOwner, { weatherList ->
                weatherList?.let {
                    cityAdapter = CityAdapter(CityAdapter.CityItemClickListener { item ->
                        item?.let { onItemClick(it) }
                    })
                    cityAdapter.submitList(weatherList)
                    binding.cityRecycler.adapter = cityAdapter
                    cityAdapter.notifyDataSetChanged()
                }
            })
        binding.cityRecycler.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
        }
    }


    private fun itemTouchHelperSetup(view: View) {
        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(

            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = cityAdapter.currentList[position]
                viewModel.deleteCity(item)
                Snackbar.make(view, getString(R.string.city_will_be_delted), Snackbar.LENGTH_LONG).apply {
                    setAction("Вернуть") {
                        viewModel.addCity(item)
                    }
                }.show()
            }

        }
        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(binding.cityRecycler)
        }

    }


    private fun EditText.clear() { text.clear() }


    private fun onItemClick(item: CityData) {
        loadWeather(item.cityName)
    }


    @SuppressLint("SetTextI18n")
    private fun loadWeather(cityName: String) {
        viewModel.loadWeatherFromId(cityName)
            ?.observe(viewLifecycleOwner, { response ->
                when (response) {
                    is WeatherResponse.Success -> {
                        response.data?.let { weatherResponse ->
                            showProgress(false, binding.weatherLayout.progressBarWeather)
                            binding.weatherLayout.temperature = weatherResponse
                            activity?.showToastyInfo("Данные загружены!")
                        }
                    }
                    is WeatherResponse.Error -> {
                        response.message?.let { message ->
                            showProgress(false, binding.weatherLayout.progressBarWeather)
                            activity?.showToastyError(message)
                        }
                    }
                    is WeatherResponse.Loading -> {
                        showProgress(show = true, binding.weatherLayout.progressBarWeather)
                    }
                }
            })
    }


    private fun showProgress(show: Boolean, view: View) {
        checkViewVisibleOrGone(show, view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}