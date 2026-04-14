package com.thepanel.data.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.thepanel.data.model.ConnectivityState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class AndroidConnectivityService(
    context: Context
) : ConnectivityService {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun connectivityState(): Flow<ConnectivityState> = callbackFlow {
        fun emitCurrent() {
            val network = connectivityManager.activeNetwork
            val caps = connectivityManager.getNetworkCapabilities(network)
            val online = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
            val transport = when {
                caps == null -> "Offline"
                caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Mobile data"
                caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
                else -> "Connected"
            }
            val signal = if (online) "Internet access" else "No connection"
            val lastSeen = if (online) {
                "Active"
            } else {
                "Last online ${LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH))}"
            }
            trySend(
                ConnectivityState(
                    online = online,
                    transport = transport,
                    signalLabel = signal,
                    lastSeenOnline = lastSeen
                )
            )
        }

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) = emitCurrent()
            override fun onLost(network: Network) = emitCurrent()
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) = emitCurrent()
        }

        emitCurrent()
        connectivityManager.registerDefaultNetworkCallback(callback)
        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }
}
