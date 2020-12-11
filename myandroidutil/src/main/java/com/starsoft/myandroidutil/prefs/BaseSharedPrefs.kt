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

package com.starsoft.myandroidutil.prefs

import android.content.Context
import android.content.SharedPreferences
import com.starsoft.myandroidutil.providers.mainContext


// This File Created at 11.12.2020 9:29.

open class BaseSharedPrefs @JvmOverloads constructor(private val fileName: String = DEFAULT_PREF_FILE,
                                                     private val mode: Mode = Mode.MODE_PRIVATE) : BasePreferences() {

    override fun getPreferences(): SharedPreferences {
        return mainContext.getSharedPreferences(fileName, mode.mode)
    }

    enum class Mode(val mode: Int) {
        MODE_PRIVATE(Context.MODE_PRIVATE),
        MODE_APPEND(Context.MODE_APPEND)
    }
}