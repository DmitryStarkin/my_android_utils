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

package com.starsoft.myandroidutil.logutils

import android.os.Build
import com.starsoft.myandroidutil.providers.ContextProvider
import com.starsoft.simpleandroidasynclibrary.core.launch
import com.starsoft.simpleandroidasynclibrary.executors.newSingleThreadPool
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


// This File Created at 24.11.2020 18:24.

object LogWriter {
    val executor = newSingleThreadPool()
    private val lock = ReentrantLock()
    private val TIME_STAMP_PATTERN = "yyyy-MM_dd_HH-mm-ss-SSS"
    var blockLog = false

    init {
        writeToLog(
            "Start lodging \r\n" +
                    "SDK version" + Build.VERSION.SDK_INT.toString() + "\r\n" +
                    "Software version: " + Build.VERSION.RELEASE.toString() + "\r\n" +
                    "Build version: " + Build.VERSION.INCREMENTAL.toString() + "\r\n" +
                    "Device " + Build.DEVICE + "\r\n" +
                    "Model " + Build.MODEL + "\r\n" +
                    "Product " + Build.PRODUCT + "\r\n" +
                    "\r\n"
        )
    }

    private fun writeToLog(msg: String) {

        var writer: FileWriter? = null
        try {
            writer = FileWriter(ContextProvider.context.generateLogFile(), true)
            val time = SimpleDateFormat(TIME_STAMP_PATTERN, Locale.getDefault()).format(Date())
            writer.write("## $time $msg ##\r\n")
        } finally {
            writer?.close()
        }
    }

    fun writeLogMessage(
        msg: String,
        onSusses: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {

        lock.withLock {
            if (!blockLog) {
                executor.launch({ onSusses?.apply { this.invoke() } },
                    { e ->
                        e.printStackTrace()
                        onError?.apply { this.invoke(e) }
                    }) { writeToLog(msg) }
            }
        }
    }

    fun reset() {
        executor.purge()
    }
}