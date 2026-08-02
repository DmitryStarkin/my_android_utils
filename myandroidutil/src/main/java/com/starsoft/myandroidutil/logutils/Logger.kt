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

class Logger {

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

    private fun visibleTag(tag: String): String = appCommonTag ?: tag

    fun d(tag: String, toFile: Boolean = false, perform: Boolean = true, t: Throwable? = null,  msg: () -> String) {
        if (isDebug && perform) {
            val vTag = visibleTag(tag)
            var _msg = if(appCommonTag == null){msg.invoke()} else {"$tag  ${msg.invoke()}"}
            t?.apply { Log.d(
                vTag, _msg, this
                )
            } ?: Log.d(
                vTag, _msg
            )
            if (toFile) {
                t?.apply { _msg = _msg + " " + "\r\n" + Log.getStackTraceString(this) }
                LogWriter.writeLogMessage("d - $vTag $_msg")
            }
        }
    }

    @JvmOverloads
    fun d(tag: String, toFile: Boolean = false, perform: Boolean = true, t: Throwable? = null,  msg: String) {
       d(tag, toFile, perform, t){msg}
    }

    fun i(tag: String, toFile: Boolean = false, perform: Boolean = true, t: Throwable? = null, msg: () -> String) {
        if (perform) {
            val vTag = visibleTag(tag)
            var _msg = msg.invoke()
            t?.apply { Log.i(
                vTag, _msg, this
            )
            } ?: Log.i(
                vTag, _msg
            )
            if (toFile) {
                t?.apply { _msg = _msg + " " + "\r\n" + Log.getStackTraceString(this) }
                LogWriter.writeLogMessage("i - $vTag $_msg")
            }
        }
    }

    @JvmOverloads
    fun i(tag: String, toFile: Boolean = false, perform: Boolean = true, t: Throwable? = null, msg: String) {
        i(tag, toFile, perform,t){msg}
    }


    fun w(tag: String, toFile: Boolean = false, perform: Boolean = true, t: Throwable? = null, msg: () -> String) {
        if (perform) {
            val vTag = visibleTag(tag)
            var _msg = msg.invoke()
            t?.apply { Log.w(
                vTag, _msg, this
            )
            } ?: Log.w(
                vTag, _msg
            )
            if (toFile) {
                t?.apply { _msg = _msg + " " +  "\r\n" + Log.getStackTraceString(this) }
                LogWriter.writeLogMessage("w - $vTag $_msg")
            }
        }
    }

    @JvmOverloads
    fun w(tag: String, toFile: Boolean = false, perform: Boolean = true, t: Throwable? = null, msg: String) {
        w(tag, toFile, perform,t){msg}
    }

    fun e(tag: String, toFile: Boolean = false, perform: Boolean = true, t: Throwable? = null, msg: () -> String) {
        if (perform) {
            val vTag = visibleTag(tag)
            var _msg = msg.invoke()
            t?.apply { Log.e(
                vTag, _msg, this
            )
            } ?: Log.e(
                vTag, _msg
            )
            if (toFile) {
                t?.apply { _msg = _msg + " " +  "\r\n" + Log.getStackTraceString(this) }
                LogWriter.writeLogMessage("e - $vTag $_msg")
            }
        }
    }

    @JvmOverloads
    fun e(tag: String, toFile: Boolean = false, perform: Boolean = true, t: Throwable? = null, msg: String) {
        e(tag, toFile, perform,t){msg}
    }

    fun v(tag: String, toFile: Boolean = false, perform: Boolean = true, t: Throwable? = null, msg: () -> String) {
        if (perform) {
            val vTag = visibleTag(tag)
            var _msg = msg.invoke()
            t?.apply { Log.v(
                vTag, _msg, this
            )
            } ?: Log.v(
                vTag, _msg
            )
            if (toFile) {
                t?.apply { _msg = _msg + " " +  "\r\n" + Log.getStackTraceString(this) }
                LogWriter.writeLogMessage("v - $vTag $_msg")
            }
        }
    }

    @JvmOverloads
    fun v(tag: String, toFile: Boolean = false, perform: Boolean = true, t: Throwable? = null, msg: String) {
        v(tag, toFile, perform,t){msg}
    }
}