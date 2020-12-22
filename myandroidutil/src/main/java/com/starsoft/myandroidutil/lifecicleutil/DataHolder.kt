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

package com.starsoft.myandroidutil.lifecicleutil

import androidx.lifecycle.LifecycleOwner
import com.starsoft.simpleandroidasynclibrary.core.lifeciclesupport.interfaces.LifecycleSupport
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


// This File Created at 22.12.2020 10:09.

/**
 * Used with Kotlin "by"
 * holds the value until onDestroy is called
 */

fun <T> LifecycleOwner.valueHolder(initialiseValue: () -> T): ReadOnlyProperty<LifecycleOwner, T> =
        object : ReadOnlyProperty<LifecycleOwner, T>, LifecycleSupport {

            private var value: T? = null

            init {
                connectToLifecycle(this@valueHolder)
            }

            override fun finalize() {
                value = null
            }

            override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): T {

                return this.value ?: initialiseValue().also {
                    this.value = it
                }
            }
        }