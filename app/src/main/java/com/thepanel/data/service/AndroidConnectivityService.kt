package com.thepanel.data.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.telephony.PhoneStateListener
import android.telephony.SignalStrength
import android.telephony.TelephonyManager
import com.thepanel.data.model.ConnectivityState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class AndroidConnectivityService(
    private val context: Context
) : ConnectivityService {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    override fun connectivityState(): Flow<ConnectivityState> = callbackFlow {
        var currentSignalDbm = -120

        val signalListener = object : PhoneStateListener() {
            override fun onSignalStrengthsChanged(signalStrength: SignalStrength?) {
                signalStrength?.let {
                    // Try to get dbm, but level is easier to get consistently
                    // level is 0..4
                    val level = it.level 
                    // Convert level to a rough dbm for display if needed, 
                    // but we'll just use dbm field for level if that's easier or use it for label
                    currentSignalDbm = -120 + (level * 20)
                    emitCurrent()
                }
            }
        }

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
            
            val signalStrengthLabel = when {
                currentSignalDbm > -70 -> "Excellent"
                currentSignalDbm > -85 -> "Good"
                currentSignalDbm > -100 -> "Fair"
                else -> "Poor"
            }

            val signal = if (online) "Internet access ($signalStrengthLabel)" else "No connection"
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
                    signalStrengthDbm = currentSignalDbm,
                    lastSeenOnline = lastSeen
                )
            )
        }

        telephonyManager.listen(signalListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
        
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) = emitCurrent()
            override fun onLost(network: Network) = emitCurrent()
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) = emitCurrent()
        }

        emitCurrent()
        connectivityManager.registerDefaultNetworkCallback(callback)
        awaitClose { 
            connectivityManager.unregisterNetworkCallback(callback)
            telephonyManager.listen(signalListener, PhoneStateListener.LISTEN_NONE)
        }
    }
}
