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
    return Regex(DOMAIN_REGEX_STRING).find(this)?.value ?: EMPTY_STRING
}

fun String.extractRootUrl(): String{
    return (Regex("^[^/]*//").find(this)?.value ?: EMPTY_STRING) + (Regex("[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,9}").find(this)?.value ?: EMPTY_STRING)
}

fun String?.isURL(): Boolean{
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex(URL_REGEX_STRING))
}

fun String?.isEmail(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex(EMAIL_ADDRESS_REGEX_STRING))
}

fun String?.isDomain(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex(ALT_DOMAIN_REGEX_STRING))
}

fun String?.isIP(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex(IP_ADDRESS_REGEX_STRING))
}

fun String?.isRFC_3987URL(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex(RFC_3987URL_REGEX_STRING))
}


//TODO requires thorough testing

fun String?.isValidFileName(ext: String): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex("$FILE_NAME_REGEX_STRING$ext"))
}

fun String?.isValidUserName(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex(USER_NAME_REGEX_STRING))
}

fun String?.isValidCreditCardNumber(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex(CREDIT_CARD_NUMBER_REGEX_STRING))
}

fun String?.isData(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex(DATA_REGEX_STRING))
}

fun String?.isIP4(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex(IP4_REGEX_STRING))
}

fun String?.isIP6(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex(IP6_REGEX_STRING))
}

fun String?.isISBN(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex(ISBN_REGEX_STRING))
}

fun String?.isBase64(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex(BASE64_REGEX_STRING))
}

fun String?.isDigit(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex(DIGIT_REGEX_STRING))
}

fun String.extractProtocol(): String{
    return Regex(WEB_PROTOCOL_REGEX_STRING).find(this)?.value ?: EMPTY_STRING
}

fun String?.isHexColorString(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex(HEX_COLOR_REGEX_STRING)) || this.matches(Regex(HEX_COLOR_WITCH_ALPHA_REGEX_STRING))
}

fun String?.isHashTagLink(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex(HASH_TAG_REGEX_STRING))
}

fun String?.isMentionsLink(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex(MENTIONS_REGEX_STRING))
}

fun String?.isWebLink(): Boolean {
    if (this == null || this.isEmpty()) return false
    return this.matches(Regex(WEB_LINK_REGEX_STRING))
}

fun String.extractMentionsData(): String {
    return this.replaceFirst(MENTIONS_TAG_PREFIX, "")
}