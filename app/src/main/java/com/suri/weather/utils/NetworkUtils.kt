package com.suri.weather.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class NetworkUtils constructor(private var context: Context) {

    fun isNetworkAvailable(): Boolean {
        return try {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR
            ) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_BLUETOOTH
            ))
        } catch (e: Exception) {
            false
        }
    }

}