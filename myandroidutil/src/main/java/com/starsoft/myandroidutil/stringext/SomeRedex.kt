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
@file:JvmName("RegexStrings")

package com.starsoft.myandroidutil.stringext

import androidx.core.util.PatternsCompat


/**
 * Created by Dmitry Starkin on 08.05.2021 13:38.
 */
const val HASH_TAG_PREFIX = "#"

const val MENTIONS_TAG_PREFIX = "@"

const val LINK_PREFIX = "https://"

const val HASH_TAG_REGEX_STRING = "$HASH_TAG_PREFIX{1}[A-Za-z0-9\\W_][^$HASH_TAG_PREFIX\\s]*"

const val HASH_TAG_HIGHLIGHT_REGEX_STRING = "(?<=\\s|^)$HASH_TAG_REGEX_STRING(?=\\s|$)"

const val HEX_COLOR_REGEX_STRING = "$HASH_TAG_PREFIX[a-f0-9]{6}\\b"

const val HEX_COLOR_WITCH_ALPHA_REGEX_STRING = "$HASH_TAG_PREFIX[a-f0-9]{8}\\b"

const val MENTIONS_REGEX_STRING = "$MENTIONS_TAG_PREFIX{1}[A-Za-z0-9\\W_][^$MENTIONS_TAG_PREFIX\\s]*"

const val MENTIONS_HIGHLIGHT_REGEX_STRING = "(?<=\\s|^)$MENTIONS_REGEX_STRING(?=\\s|$)"

const val WEB_PROTOCOL_REGEX_STRING = "^([a-zA-Z]+)://"

const val URL_REGEX_STRING = "^((ftp|http|https)://)?(www\\.)?([A-Za-zА-Яа-я0-9]{1}[A-Za-zА-Яа-я0-9\\-]*\\.?)*\\.{1}[A-Za-zА-Яа-я0-9-]{2,8}(/([\\w#!:.?+=&%@!\\-/])*)?"

const val USER_NAME_REGEX_STRING = "^[A-Za-z0-9_-]{3,16}\$"

const val DIGIT_REGEX_STRING = "^\\d{1,}\$"

const val DOMAIN_REGEX_STRING = "[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,9}"

const val ISBN_REGEX_STRING = "\\b(?:ISBN(?:: ?| ))?((?:97[89])?\\d{9}[\\dx])\\b/i"

const val CREDIT_CARD_NUMBER_REGEX_STRING = "^(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|6(?:011|5[0-9][0-9])[0-9]{12}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|(?:2131|1800|35\\d{3})\\d{11})\$"

const val BASE64_REGEX_STRING = "^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?\$"

const val FILE_NAME_REGEX_STRING = "[a-zA-Z0-9_]+\\."

const val IP6_REGEX_STRING = "(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))"

const val IP4_REGEX_STRING = "\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b"

const val DATA_REGEX_STRING = "^(?:(?:31([/\\-.])(?:0?[13578]|1[02]))\\1|(?:(?:29|30)([/\\-.])(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$|^(?:29([/\\-.])0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))\$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$"

val EMAIL_REGEX_STRING = PatternsCompat.EMAIL_ADDRESS.toString()

val WEB_LINK_REGEX_STRING = PatternsCompat.WEB_URL.toString()

val RFC_3987URL_REGEX_STRING: String = android.util.Patterns.WEB_URL.pattern()

val IP_ADDRESS_REGEX_STRING: String = android.util.Patterns.IP_ADDRESS.pattern()

val EMAIL_ADDRESS_REGEX_STRING: String = android.util.Patterns.EMAIL_ADDRESS.pattern()

val ALT_DOMAIN_REGEX_STRING: String = android.util.Patterns.DOMAIN_NAME.pattern()