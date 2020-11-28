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

@file:JvmName("BuildConfigHelper")

package com.starsoft.myandroidutil.refutils

import android.content.Context
import android.widget.AutoCompleteTextView


// This File Created at 25.11.2020 12:22.

fun Context.getBuildConfigValue(fieldName: String): Any? {
    try {
        val clazz = Class.forName(this.packageName.toString() + ".BuildConfig")
        val field = clazz.getField(fieldName)
        return field.get(null)
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    } catch (e: NoSuchFieldException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }
    return null
}