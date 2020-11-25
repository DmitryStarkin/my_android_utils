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

class Logger (var tag: String, val toFile: Boolean = false) {

    companion object{

        val isDebug = ContextProvider.context.getBuildConfigValue("DEBUG") as Boolean? ?: false

        init {
            val file = ContextProvider.context.getLogFile()
            if (file.exists()) {
                file.delete()
            }

        }
    }
    fun d(perform: Boolean = true, msg: () -> String) {
        if (isDebug && perform) {
            val _msg = msg.invoke()
            Log.d(
                tag, _msg
            )
            if (toFile) {
                LogWriter.writeLogMessage("d - $tag $_msg")
            }
        }
    }
}