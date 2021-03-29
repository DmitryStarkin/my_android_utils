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

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.starsoft.myandroidutil.providers.mainContext


// This File Created at 10.12.2020 19:09.

abstract class BasePreferences {

    companion object {
        @JvmStatic
        val DEFAULT_PREF_FILE = mainContext.packageName.toString() + "_preferences"
    }

    val mPreferences
    get() = getPreferences()

    @JvmOverloads
    fun clearPref(name: String, isCommit: Boolean = false) {
        getPreferences().clearPref(name, isCommit)
    }

    @JvmOverloads
    fun clearAllPref(isCommit: Boolean = false) {
        getPreferences().clearAllPref(isCommit)
    }

    @JvmOverloads
    fun setIntPref(key: String, value: Int, isCommit: Boolean = false) {
        getPreferences().setIntPref(key, value, isCommit)
    }

    @JvmOverloads
    fun setLongPref(key: String, value: Long, isCommit: Boolean = false) {
        getPreferences().setLongPref(key, value, isCommit)
    }

    @JvmOverloads
    fun setBooleanPref(key: String, value: Boolean, isCommit: Boolean = false) {
        getPreferences().setBooleanPref(key, value, isCommit)
    }

    @JvmOverloads
    fun setFloatPref(key: String, value: Float, isCommit: Boolean = false) {
        getPreferences().setFloatPref(key, value, isCommit)
    }

    @JvmOverloads
    fun setStringPref(key: String, value: String, isCommit: Boolean = false) {
        getPreferences().setStringPref(key, value, isCommit)
    }

    @JvmOverloads
    fun setStringSetPref(key: String, value: MutableSet<String>, isCommit: Boolean = false) {
        getPreferences().setStringSetPref(key, value, isCommit)
    }

    abstract fun getPreferences(): SharedPreferences
}