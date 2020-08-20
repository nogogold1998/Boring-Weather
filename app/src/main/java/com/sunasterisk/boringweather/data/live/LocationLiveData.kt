package com.sunasterisk.boringweather.data.live

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.annotation.IntDef
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.sunasterisk.boringweather.util.isLazyInitialized

@IntDef(
    value = [LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
        LocationRequest.PRIORITY_HIGH_ACCURACY,
        LocationRequest.PRIORITY_LOW_POWER,
        LocationRequest.PRIORITY_NO_POWER],
    open = true
)
annotation class LocationPriority

class LocationLiveData(
    context: Context,
    private val interval: Long = DEFAULT_INTERVAL_TIME_MILLIS,
    private val fastestInterval: Long = DEFAULT_FASTEST_INTERVAL_TIME_MILLIS,
    @LocationPriority private val priority: Int = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
) : LiveData<Location>() {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context.applicationContext)
    }

    private val locationRequest: LocationRequest by lazy {
        LocationRequest.create().also {
            it.interval = interval
            it.fastestInterval = fastestInterval
            it.priority = priority
        }
    }

    private val locationCallback: LocationCallback by lazy { DefaultLocationCallBack() }

    // required permission is granted guarantee when this is observed
    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    override fun onInactive() {
        super.onInactive()
        if (this::locationCallback.isLazyInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    @RequiresPermission(
        anyOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    override fun observe(owner: LifecycleOwner, observer: Observer<in Location>) {
        super.observe(owner, observer)
    }

    @RequiresPermission(
        anyOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    override fun observeForever(observer: Observer<in Location>) {
        super.observeForever(observer)
    }

    companion object {
        const val DEFAULT_INTERVAL_TIME_MILLIS = 60_000L
        const val DEFAULT_FASTEST_INTERVAL_TIME_MILLIS = 5_000L

        private const val TAG = "LocationLive"
    }

    private inner class DefaultLocationCallBack : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                postValue(location)
            }
        }

        override fun onLocationAvailability(availability: LocationAvailability?) {
            super.onLocationAvailability(availability)
            Log.d(
                TAG,
                "onLocationAvailability: isLocationAvailable=${availability?.isLocationAvailable}"
            )
        }
    }
}
