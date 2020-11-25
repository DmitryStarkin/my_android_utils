/*
 * Copyright (c) 2020. Dmitry Starkin Contacts: t0506803080@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the «License»);
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  //www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an «AS IS» BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.starsoft.myandroidutil.connectionutil

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities


// This File Created at 25.11.2020 14:33.

fun Context.isInternetAvailable(): Boolean {
    return getCapabilitiesNetworkWitchInternetConnected(this)?.let { true } ?: false
}

fun Context.isInternetAvailableByWiFi(): Boolean {
    return getCapabilitiesNetworkWitchInternetConnected(this)?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
}

fun Context.isInternetAvailableByMobile(): Boolean {
    return getCapabilitiesNetworkWitchInternetConnected(this)?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ?: false
}

private fun getCapabilitiesNetworkWitchInternetConnected(context: Context): NetworkCapabilities? {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networks = connectivityManager.allNetworks
    for (network in networks) {
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && capabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)) {
            return capabilities
        }
    }
    return null
}