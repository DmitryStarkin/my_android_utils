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

@file:JvmName("PreferencesHelper")

package com.starsoft.myandroidutil.prefs

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.starsoft.myandroidutil.providers.mainContext


// This File Created at 10.12.2020 19:26.

@SuppressLint("ApplySharedPref")
@JvmOverloads
fun SharedPreferences.clearPref(name: String, isCommit: Boolean = false) {
    this.edit()
        .remove(name)
        .apply {
            if (isCommit) {
                this.commit()
            } else {
                this.apply()
            }
        }
}

@SuppressLint("ApplySharedPref")
@JvmOverloads
fun SharedPreferences.clearAllPref(isCommit: Boolean = false) {
    this.edit()
            .clear()
            .apply {
                if (isCommit) {
                    this.commit()
                } else {
                    this.apply()
                }
            }
}

@SuppressLint("ApplySharedPref")
@JvmOverloads
fun SharedPreferences.setIntPref(key: String, value: Int, isCommit: Boolean = false) {
    this.edit()
        .putInt(key, value)
        .apply {
            if (isCommit) {
                this.commit()
            } else {
                this.apply()
            }
        }
}

@SuppressLint("ApplySharedPref")
@JvmOverloads
fun SharedPreferences.setLongPref(key: String, value: Long, isCommit: Boolean = false) {
    this.edit()
        .putLong(key, value)
        .apply {
            if (isCommit) {
                this.commit()
            } else {
                this.apply()
            }
        }
}

@SuppressLint("ApplySharedPref")
@JvmOverloads
fun SharedPreferences.setBooleanPref(key: String, value: Boolean, isCommit: Boolean = false) {
    this.edit()
        .putBoolean(key, value)
        .apply {
            if (isCommit) {
                this.commit()
            } else {
                this.apply()
            }
        }
}

@SuppressLint("ApplySharedPref")
@JvmOverloads
fun SharedPreferences.setStringPref(key: String, value: String, isCommit: Boolean = false) {
    this.edit()
        .putString(key, value)
        .apply {
            if (isCommit) {
                this.commit()
            } else {
                this.apply()
            }
        }
}

@SuppressLint("ApplySharedPref")
@JvmOverloads
fun SharedPreferences.setStringSetPref(key: String, value: MutableSet<String>, isCommit: Boolean = false) {

    clearPref(key)
    val newSet = HashSet<String>(value)
    this.edit()
        .putStringSet(key, newSet).apply {
            if (isCommit) {
                this.commit()
            } else {
                this.apply()
            }
        }
}

@SuppressLint("ApplySharedPref")
@JvmOverloads
fun SharedPreferences.setFloatPref(key: String, value: Float, isCommit: Boolean = false) {

    this.edit()
            .putFloat(key, value)
            .apply {
                if (isCommit) {
                    this.commit()
                } else {
                    this.apply()
                }
            }
}