/*
 * Copyright (c) 2022. Dmitry Starkin Contacts: t0506803080@gmail.com
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

package com.starsoft.myandroidutil.errorUtil


/**
 * Created by Dmitry Starkin on 15.04.2022 11:27.
 */

fun runSafe(lambda: () -> Unit){
    try {
        lambda.invoke()
    } catch (e: Throwable){
        e.printStackTrace()
    }
}

fun <T> runSafe(ifError:(Throwable) -> T, lambda: () -> T): T =
    try {
        lambda.invoke()
    } catch (e: Throwable){
        e.printStackTrace()
        ifError(e)
    }

fun <T, V> T.runSafe(ifError:T.(Throwable) -> V, lambda: T.() -> V): V =
    try {
        this.lambda()
    } catch (e: Throwable){
        e.printStackTrace()
        this.ifError(e)
    }

fun <T> getSafeOrNull(lambda: () -> T): T? =
    try {
        lambda.invoke()
    } catch (e: Throwable){
        e.printStackTrace()
        null
    }

fun <T, V> T.runSafeOrNull(lambda: T.() -> V): V? =
    try {
        this.lambda()
    } catch (e: Throwable){
        e.printStackTrace()
        null
    }
