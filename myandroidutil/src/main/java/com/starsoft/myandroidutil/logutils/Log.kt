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


import com.starsoft.myandroidutil.providers.mainContext
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

// This File Created at 28.11.2020 13:41.

val mainLogger: Logger by lazy {
    Logger("CommonLog")
}

private val mName = "com.starsoft.myandroidutil.logutils.Log"

private val lock = ReentrantLock()

@JvmOverloads
fun d(toFile: Boolean = false, t: Throwable? = null, msg: String = " ") {
    lock.withLock {
        mainLogger.tag =  getTag()
        val tf = mainLogger.toFile
        mainLogger.toFile = toFile
        mainLogger.d(t = t) { msg }
        mainLogger.toFile = tf
    }
}

fun log_d(toFile: Boolean = false, t: Throwable? = null, msg: () -> String = {" "}) {
    lock.withLock {
        mainLogger.tag =  getTag()
        val tf = mainLogger.toFile
        mainLogger.toFile = toFile
        mainLogger.d(t = t) { msg.invoke() }
        mainLogger.toFile = tf
    }
}

@JvmOverloads
fun i(toFile: Boolean = false, t: Throwable? = null, msg: String = " ") {
    lock.withLock {
        mainLogger.tag =  getTag()
        val tf = mainLogger.toFile
        mainLogger.toFile = toFile
        mainLogger.i(t = t) { msg }
        mainLogger.toFile = tf
    }
}

fun log_i(toFile: Boolean = false, t: Throwable? = null, msg: () -> String = {" "}) {
    lock.withLock {
        mainLogger.tag =  getTag()
        val tf = mainLogger.toFile
        mainLogger.toFile = toFile
        mainLogger.i(t = t) { msg.invoke() }
        mainLogger.toFile = tf
    }
}

@JvmOverloads
fun w(toFile: Boolean = false, t: Throwable? = null, msg: String = " ") {
    lock.withLock {
        mainLogger.tag =  getTag()
        val tf = mainLogger.toFile
        mainLogger.toFile = toFile
        mainLogger.w(t = t) { msg }
        mainLogger.toFile = tf
    }
}

fun log_w(toFile: Boolean = false, t: Throwable? = null, msg: () -> String = {" "}) {
    lock.withLock {
        mainLogger.tag =  getTag()
        val tf = mainLogger.toFile
        mainLogger.toFile = toFile
        mainLogger.w(t = t) { msg.invoke() }
        mainLogger.toFile = tf
    }

}

@JvmOverloads
fun e(toFile: Boolean = false, t: Throwable? = null, msg: String = " ") {
    lock.withLock {
        mainLogger.tag =  getTag()
        val tf = mainLogger.toFile
        mainLogger.toFile = toFile
        mainLogger.e(t = t) { msg }
        mainLogger.toFile = tf
    }
}

fun log_e(toFile: Boolean = false, t: Throwable? = null, msg: () -> String = {" "}) {
    lock.withLock {
        mainLogger.tag =  getTag()
        val tf = mainLogger.toFile
        mainLogger.toFile = toFile
        mainLogger.e(t = t) { msg.invoke() }
        mainLogger.toFile = tf
    }
}

@JvmOverloads
fun v(toFile: Boolean = false, t: Throwable? = null, msg: String = " ") {
    lock.withLock {
        mainLogger.tag =  getTag()
        val tf = mainLogger.toFile
        mainLogger.toFile = toFile
        mainLogger.v(t = t) { msg }
        mainLogger.toFile = tf
    }
}

fun log_v(toFile: Boolean = false, t: Throwable? = null, msg: () -> String = {" "}) {
    lock.withLock {
        mainLogger.tag =  getTag()
        val tf = mainLogger.toFile
        mainLogger.toFile = toFile
        mainLogger.v(t = t) { msg.invoke() }
        mainLogger.toFile = tf
    }
}

//TODO requires thorough testing
private fun getTag(): String{

        val stack = Throwable().stackTrace
        for(frame in stack){
            if(frame.className != mName){
                return frame.className.substringAfterLast(".") + "_" + frame.methodName
            }
        }
        return "CommonLog"
}