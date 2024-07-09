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

@file:JvmName("ClassHelper")

package com.starsoft.myandroidutil.refutils


// This File Created at 24.11.2020 14:33.

fun Class<*>.isExtendInterface(type: Class<*>): Boolean {
    this.interfaces.apply {
        for (typeOfApi in this) {
            if (typeOfApi == type) {
                return true
            } else if(typeOfApi.isExtendInterface(type)){
                return true
            }
        }
    }
    return false
}

fun Class<*>.isInstanceOrExtend(type: Class<*>): Boolean {
    this.getSuperClasses().apply {
        for (typeOfApi in this) {
            if (typeOfApi == type) {
                return true
            }
        }
    }
    return false
}


fun Class<*>.getSuperClasses() :List<Class<*>> {
    val classList = ArrayList<Class<*>>()
    classList.add(this)
    var superclass: Class<*>?  = this.superclass?.apply {this as Class<*> }
    superclass?.apply {classList.add(this)  }
    while (superclass != null) {

        superclass = superclass.superclass?.apply {this as Class<*> }
        superclass?.apply {classList.add(this)  }
    }
    return classList
}