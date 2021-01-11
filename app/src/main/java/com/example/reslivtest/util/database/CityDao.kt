package com.example.reslivtest.util.database

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*


@Dao
interface CityDao {

    @Query("SELECT * FROM city_table")
    fun getAllCity(): LiveData<List<CityData>>

    @Query("SELECT * FROM city_table WHERE id=(:id)")
    fun getCity(id: UUID): LiveData<CityData?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCity(cityData: CityData)

    @Delete
    fun delete(cityData: CityData)

    @Update
    fun updateCity(cityData: CityData)

    @Query("DELETE FROM city_table")
    fun nukeTable()

    //Location data, coordinate
//
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun saveLocation(locationData: LocationData)
//
//    @Query("SELECT * FROM location_table WHERE id=:id")
//    fun getLastLocation(id: Long): LiveData<LocationData?>
//
//    @Query("SELECT * FROM location_table")
//    fun getAllLocation(): LiveData<List<LocationData>>

    //LocationResponce Data "location_response"

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveLocationResponse(locationResponse: LocationResponse)

    @Query("SELECT * FROM location_response WHERE id=:id")
    fun getLocationResponseFromId(id: Long): LiveData<LocationResponse?>





}