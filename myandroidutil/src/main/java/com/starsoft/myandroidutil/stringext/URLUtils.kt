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

@file:JvmName("URLHelper")

package com.starsoft.myandroidutil.stringext


// This File Created at 25.11.2020 13:26.

fun String?.isURL(): Boolean{
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex("^((ftp|http|https)://)?(www\\.)?([A-Za-zА-Яа-я0-9]{1}[A-Za-zА-Яа-я0-9\\-]*\\.?)*\\.{1}[A-Za-zА-Яа-я0-9-]{2,8}(/([\\w#!:.?+=&%@!\\-/])*)?"))
}

fun String.extractDomain(): String{

    return Regex("[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,9}").find(this)?.value ?: ""
}

fun String.extractRootUrl(): String{

    val rootUrl = (Regex("^[^/]*//").find(this)?.value ?: "") + (Regex("[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,9}").find(this)?.value ?: "")
    return rootUrl
}