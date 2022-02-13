/*
 * Copyright (c) 2022. Dmitry Starkin Contacts: t0506803080@gmail.com
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

package com.starsoft.myandroidutil.LifeDataUtils

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.starsoft.myandroidutil.R
import com.starsoft.myandroidutil.providers.mainContext
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException


/**
 * Created by Dmitry Starkin on 07.02.2022 10:52.
 */
open class ErrorHandler(
    private val context: Context,
    error: MutableLiveData<Event<String>>
) {
    companion object{
        fun getInstance(): ErrorHandler = ErrorHandler(mainContext, MutableLiveData())
    }

    private val _error = error
    fun getError(): LiveData<Event<String>> = _error

    open fun handleThrowable(throwable: Throwable) {
        when (throwable) {
            is UnknownHostException -> {
                _error.postValue(Event(context.getString(R.string.no_internet_error)))
            }

            is SocketTimeoutException -> {
                _error.postValue(Event(context.getString(R.string.server_time_out_error)))
            }

            is CancellationException -> {
                // nothing to do
            }

            is InterruptedException -> {
                // nothing to do
            }

            else -> {
                throwable.message?.let { message ->
                    _error.postValue(Event(message))
                }
            }
        }
    }
}