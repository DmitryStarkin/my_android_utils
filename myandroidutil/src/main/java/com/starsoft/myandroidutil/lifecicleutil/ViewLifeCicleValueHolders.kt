/*
 * Copyright (c) 2021. Dmitry Starkin Contacts: t0506803080@gmail.com
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

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.starsoft.simpleandroidasynclibrary.core.lifeciclesupport.interfaces.LifecycleSupport
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * Created by Dmitry Starkin on 20.05.2021 10:34.
 */

/**
 * Used with Kotlin "by"
 * tracks the life cycle of a view in a fragment
 * holds the value until onDestroy is called
 */

private const val EMPTY_VALUE_ERROR_MESSAGE = "You must set the value before getting it"

fun <T> Fragment.viewLifeCycleValueHolder(initialiseValue: () -> T): ReadOnlyProperty<Fragment, T> =
        object : ReadOnlyProperty<Fragment, T>, LifecycleSupport {

            private var value: T? = null

            init {
                this@viewLifeCycleValueHolder
                        .viewLifecycleOwnerLiveData.observe(this@viewLifeCycleValueHolder,  Observer{
                            it?.let { owner ->
                                connectToLifecycle(owner)
                            }
                        })

            }

            override fun finalizeTask() {
                value = null
            }

            override fun getValue(thisRef: Fragment, property: KProperty<*>): T {

                return this.value ?: initialiseValue().also {
                    this.value = it
                }
            }
        }

fun <T> Fragment.viewLifeCycleValueHolder(): ReadWriteProperty<Fragment, T> =
        object : ReadWriteProperty<Fragment, T>, LifecycleSupport {

            private var value: T? = null

            init {
                this@viewLifeCycleValueHolder
                        .viewLifecycleOwnerLiveData.observe(this@viewLifeCycleValueHolder,  Observer{
                            it?.let { owner ->
                                connectToLifecycle(owner)
                            }
                        })
            }

            override fun finalizeTask() {
                value = null
            }

            override fun getValue(thisRef: Fragment, property: KProperty<*>): T {

                return this.value ?: throw Exception(EMPTY_VALUE_ERROR_MESSAGE)
            }

            override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
                this.value = value
            }
        }