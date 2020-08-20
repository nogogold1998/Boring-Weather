package com.sunasterisk.boringweather.data.live

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.sunasterisk.boringweather.util.isLazyInitialized

enum class NetworkState {
    CELLULAR, WIFI, ETHERNET, NO_NETWORK
}

class NetworkStateLiveData(context: Context) : LiveData<NetworkState>() {

    private val connectivityManager by lazy {
        ContextCompat.getSystemService(
            context.applicationContext,
            ConnectivityManager::class.java
        ) as ConnectivityManager
    }

    private val networkCallback: ConnectivityManager.NetworkCallback by lazy { DefaultNetworkCallBack() }

    init {
        postValue(NetworkState.NO_NETWORK)
    }

    override fun onActive() {
        super.onActive()
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun onInactive() {
        super.onInactive()
        if (this::networkCallback.isLazyInitialized) {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

    private fun getNetworkState() =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork).run {
            when {
                this === null -> NetworkState.NO_NETWORK
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkState.CELLULAR
                hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE) -> NetworkState.WIFI
                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkState.ETHERNET
                else -> NetworkState.NO_NETWORK
            }
        }

    private inner class DefaultNetworkCallBack : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            postValue(getNetworkState())
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            postValue(getNetworkState())
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(getNetworkState())
        }

        override fun onUnavailable() {
            super.onUnavailable()
            postValue(getNetworkState())
        }
    }
}
