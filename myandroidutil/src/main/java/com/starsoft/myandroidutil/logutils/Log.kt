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

@file:JvmName("Log")

package com.starsoft.myandroidutil.logutils

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

// This File Created at 28.11.2020 13:41.

private val mainLogger: Logger by lazy {
    Logger()
}

private const val M_NAME = "com.starsoft.myandroidutil.logutils.Log"

private val lock = ReentrantLock()

fun setCommonTag(tag: String){
    if(tag.isNotEmpty()){
        mainLogger.appCommonTag = tag
    } else {
        throw Exception("empty tag")
    }
}

fun resetTag(){
        mainLogger.appCommonTag = null
}

@JvmOverloads
fun d(msg: String = " ", t: Throwable? = null, toFile: Boolean = false ) {
    lock.withLock {
        val tg = getTag()
        mainLogger.d(tg, t = t) { msg }
    }
}

fun log_d(toFile: Boolean = false, t: Throwable? = null, msg: () -> String = {" "}) {
    lock.withLock {
        val tg = getTag()
        mainLogger.d(tg, toFile, t = t) { msg.invoke() }
    }
}

fun log_dT(tag: String? = null, t: Throwable? = null, msg: () -> String = {" "}) {
    lock.withLock {
        val tg = getTag()
        mainLogger.d(tag ?: tg, t = t) { msg.invoke() }
    }
}

@JvmOverloads
fun i(msg: String = " ", t: Throwable? = null, toFile: Boolean = false ) {
    lock.withLock {
        val tg = getTag()
        mainLogger.i(tg, t = t) { msg }
    }
}

fun log_i(toFile: Boolean = false, t: Throwable? = null, msg: () -> String = {" "}) {
    lock.withLock {
        val tg = getTag()
        mainLogger.i(tg, toFile, t = t) { msg.invoke() }
    }
}

fun log_iT(tag: String? = null, t: Throwable? = null, msg: () -> String = {" "}) {
    lock.withLock {
        val tg = getTag()
        mainLogger.i(tag ?: tg, t = t) { msg.invoke() }
    }
}

@JvmOverloads
fun w(msg: String = " ", t: Throwable? = null, toFile: Boolean = false ) {
    lock.withLock {
        val tg = getTag()
        mainLogger.w(tg, t = t) { msg }
    }
}

fun log_w(toFile: Boolean = false, t: Throwable? = null, msg: () -> String = {" "}) {
    lock.withLock {
        val tg = getTag()
        mainLogger.w(tg, toFile, t = t) { msg.invoke() }
    }

}

fun log_wT(tag: String? = null, t: Throwable? = null, msg: () -> String = {" "}) {
    lock.withLock {
        val tg = getTag()
        mainLogger.w(tag ?: tg, t = t) { msg.invoke() }
    }

}

@JvmOverloads
fun e(msg: String = " ", t: Throwable? = null, toFile: Boolean = false ) {
    lock.withLock {
        val tg = getTag()
        mainLogger.e(tg, t = t) { msg }
    }
}

fun log_e(toFile: Boolean = false, t: Throwable? = null, msg: () -> String = {" "}) {
    lock.withLock {
        val tg = getTag()
        mainLogger.e(tg, toFile, t = t) { msg.invoke() }
    }
}

fun log_eT(tag: String? = null, t: Throwable? = null, msg: () -> String = {" "}) {
    lock.withLock {
        val tg = getTag()
        mainLogger.e(tag ?: tg, t = t) { msg.invoke() }
    }
}

@JvmOverloads
fun v(msg: String = " ", t: Throwable? = null, toFile: Boolean = false ) {
    lock.withLock {
        val tg = getTag()
        mainLogger.v(tg, t = t) { msg }
    }
}

fun log_v(toFile: Boolean = false, t: Throwable? = null, msg: () -> String = {" "}) {
    lock.withLock {
        val tg = getTag()
        mainLogger.v(tg, toFile, t = t) { msg.invoke() }
    }
}

fun log_vT(tag: String? = null, t: Throwable? = null, msg: () -> String = {" "}) {
    lock.withLock {
        val tg = getTag()
        mainLogger.v(tag ?: tg, t = t) { msg.invoke() }
    }
}

//TODO requires thorough testing
private fun getTag(): String{

        val stack = Throwable().stackTrace
        for(frame in stack){
            if(frame.className != M_NAME){
                return frame.className.substringAfterLast(".") + "_" + frame.methodName
            }
        }
        return "CommonLog"
}