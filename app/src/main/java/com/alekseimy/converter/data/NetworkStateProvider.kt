package com.alekseimy.converter.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.ConnectivityManager.NetworkCallback
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class NetworkStateProvider(
    context: Context
) {

    private val networkStateSubject = BehaviorSubject.create<NetworkState>()

    private val lock = ReentrantLock()
    private var refCount = 0

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val networkStateCallback: NetworkCallback = object : NetworkCallback() {

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            notifyNetworkState(networkCapabilities.hasCapability(NET_CAPABILITY_INTERNET))
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            notifyNetworkState(false)
        }
    }

    init {
        notifyNetworkState(isConnected = false)
    }

    fun observeNetworkState(): Flowable<NetworkState> {
        return networkStateSubject
            .doOnSubscribe {
                lock.withLock {
                    if (refCount++ == 0) {
                        connectivityManager.registerDefaultNetworkCallback(networkStateCallback)
                    }
                }
            }
            .doOnDispose {
                lock.withLock {
                    if (--refCount == 0) {
                        connectivityManager.unregisterNetworkCallback(networkStateCallback)
                    }
                }
            }
            .toFlowable(BackpressureStrategy.LATEST)
    }

    private fun notifyNetworkState(isConnected: Boolean) {
        networkStateSubject.onNext(NetworkState(isConnected))
    }

    data class NetworkState(val isConnected: Boolean)
}