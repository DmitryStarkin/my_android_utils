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

@file:JvmName("MatchHelper")

package com.starsoft.myandroidutil.stringext


// This File Created at 25.11.2020 13:26.

fun String.extractDomain(): String{
    return Regex("[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,9}").find(this)?.value ?: ""
}

fun String.extractRootUrl(): String{
    return (Regex("^[^/]*//").find(this)?.value ?: "") + (Regex("[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,9}").find(this)?.value ?: "")
}

fun String?.isURL(): Boolean{
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex("^((ftp|http|https)://)?(www\\.)?([A-Za-zА-Яа-я0-9]{1}[A-Za-zА-Яа-я0-9\\-]*\\.?)*\\.{1}[A-Za-zА-Яа-я0-9-]{2,8}(/([\\w#!:.?+=&%@!\\-/])*)?"))
}

fun String?.isEmail(): Boolean {
    if (this == null || this.isEmpty()) return false
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String?.isDomain(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex(android.util.Patterns.DOMAIN_NAME.pattern()))
}

fun String?.isIP(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex(android.util.Patterns.IP_ADDRESS.pattern()))
}

fun String?.isRFC_3987URL(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex(android.util.Patterns.WEB_URL.pattern()))
}


//TODO requires thorough testing

fun String?.isValidFileName(ext: String): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex("[a-zA-Z0-9_]+\\.$ext"))
}

fun String?.isValidUserName(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex("^[A-Za-z0-9_-]{3,16}\$"))
}

fun String?.isValidCreditCardNumber(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex("^(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|6(?:011|5[0-9][0-9])[0-9]{12}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|(?:2131|1800|35\\d{3})\\d{11})\$"))
}

fun String?.isData(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex("^(?:(?:31([/\\-.])(?:0?[13578]|1[02]))\\1|(?:(?:29|30)([/\\-.])(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$|^(?:29([/\\-.])0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))\$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$"))
}

fun String?.isIP4(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex("\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b"))
}

fun String?.isIP6(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex("(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))"))
}

fun String?.isISBN(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex("\\b(?:ISBN(?:: ?| ))?((?:97[89])?\\d{9}[\\dx])\\b/i"))
}

fun String?.isBase64(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?\$"))
}

fun String?.isDigit(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex("^\\d{1,}\$"))
}

fun String.extractProtocol(): String{
    return Regex("^([a-zA-Z]+)://").find(this)?.value ?: ""
}