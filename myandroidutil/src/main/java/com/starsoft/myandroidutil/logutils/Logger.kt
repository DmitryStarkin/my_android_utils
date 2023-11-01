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

import android.util.Log
import com.starsoft.myandroidutil.providers.ContextProvider
import com.starsoft.myandroidutil.refutils.getBuildConfigValue


// This File Created at 25.11.2020 11:34.

class Logger @JvmOverloads constructor(var tag: String, var toFile: Boolean = false) {

    private companion object {

        val isDebug = ContextProvider.context.getBuildConfigValue("DEBUG") as Boolean? ?: false

        init {
            val file = ContextProvider.context.getLogFile()
            if (file.exists()) {
                file.delete()
            }
        }
    }


    internal var appCommonTag: String? = null

    private val visibleTag: String get() = appCommonTag ?: tag

    fun d(perform: Boolean = true, t: Throwable? = null, msg: () -> String) {
        if (isDebug && perform) {
            var _msg = msg.invoke()
            t?.apply { Log.d(
                visibleTag, _msg, this
                )
            } ?: Log.d(
                visibleTag, _msg
            )
            if (toFile) {
                t?.apply { _msg = _msg + " " + "\r\n" + Log.getStackTraceString(this) }
                LogWriter.writeLogMessage("d - $visibleTag $_msg")
            }
        }
    }

    @JvmOverloads
    fun d(perform: Boolean = true, t: Throwable? = null, msg: String) {
       d(perform,t){msg}
    }

    fun i(perform: Boolean = true, t: Throwable? = null, msg: () -> String) {
        if (perform) {
            var _msg = msg.invoke()
            t?.apply { Log.i(
                visibleTag, _msg, this
            )
            } ?: Log.i(
                visibleTag, _msg
            )
            if (toFile) {
                t?.apply { _msg = _msg + " " + "\r\n" + Log.getStackTraceString(this) }
                LogWriter.writeLogMessage("i - $visibleTag $_msg")
            }
        }
    }

    @JvmOverloads
    fun i(perform: Boolean = true, t: Throwable? = null, msg: String) {
        i(perform,t){msg}
    }


    fun w(perform: Boolean = true, t: Throwable? = null, msg: () -> String) {
        if (perform) {
            var _msg = msg.invoke()
            t?.apply { Log.w(
                visibleTag, _msg, this
            )
            } ?: Log.w(
                visibleTag, _msg
            )
            if (toFile) {
                t?.apply { _msg = _msg + " " +  "\r\n" + Log.getStackTraceString(this) }
                LogWriter.writeLogMessage("w - $visibleTag $_msg")
            }
        }
    }

    @JvmOverloads
    fun w(perform: Boolean = true, t: Throwable? = null, msg: String) {
        w(perform,t){msg}
    }

    fun e(perform: Boolean = true, t: Throwable? = null, msg: () -> String) {
        if (perform) {
            var _msg = msg.invoke()
            t?.apply { Log.e(
                visibleTag, _msg, this
            )
            } ?: Log.e(
                visibleTag, _msg
            )
            if (toFile) {
                t?.apply { _msg = _msg + " " +  "\r\n" + Log.getStackTraceString(this) }
                LogWriter.writeLogMessage("e - $visibleTag $_msg")
            }
        }
    }

    @JvmOverloads
    fun e(perform: Boolean = true, t: Throwable? = null, msg: String) {
        e(perform,t){msg}
    }

    fun v(perform: Boolean = true, t: Throwable? = null, msg: () -> String) {
        if (perform) {
            var _msg = msg.invoke()
            t?.apply { Log.v(
                visibleTag, _msg, this
            )
            } ?: Log.v(
                visibleTag, _msg
            )
            if (toFile) {
                t?.apply { _msg = _msg + " " +  "\r\n" + Log.getStackTraceString(this) }
                LogWriter.writeLogMessage("v - $visibleTag $_msg")
            }
        }
    }

    @JvmOverloads
    fun v(perform: Boolean = true, t: Throwable? = null, msg: String) {
        v(perform,t){msg}
    }
}