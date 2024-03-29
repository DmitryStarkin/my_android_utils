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

package com.starsoft.myandroidutil.enumutils

import kotlin.reflect.KClass


// This File Created at 14.12.2020 13:53.

fun Array<out Enum<*>>.getNames(): List<String> {
    var names = emptyArray<String>()
    for (enum in this) {
        names += enum.name
    }
    return names.toList()
}

fun Array<out Enum<*>>.getOrdinals(): List<Int> {
    var names = emptyArray<Int>()
    for (enum in this) {
        names += enum.ordinal
    }
    return names.toList()
}


