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

package com.starsoft.myandroidutil.linnkClickUtils

import androidx.core.util.PatternsCompat


/**
 * Created by Dmitry Starkin on 08.05.2021 13:38.
 */
const val HASH_TAG_PREFIX = "#"

const val MENTIONS_TAG_PREFIX = "@"

const val LINK_PREFIX = "https://"

const val HASH_TAG_REGEX_STRING = "$HASH_TAG_PREFIX{1}[A-Za-z0-9\\W_][^$HASH_TAG_PREFIX\\s]*"

const val HASH_TAG_HIGHLIGHT_REGEX_STRING = "(?<=\\s|^)$HASH_TAG_REGEX_STRING(?=\\s|$)"

const val MENTIONS_REGEX_STRING =
    "$MENTIONS_TAG_PREFIX{1}[A-Za-z0-9\\W_][^$MENTIONS_TAG_PREFIX\\s]*"

const val MENTIONS_HIGHLIGHT_REGEX_STRING = "(?<=\\s|^)$MENTIONS_REGEX_STRING(?=\\s|$)"

const val WEB_PROTOCOL_REGEX_STRING = "^([a-zA-Z]+)://"

val EMAIL_REGEX_STRING = PatternsCompat.EMAIL_ADDRESS.toString()

val WEB_LINK_REGEX_STRING = PatternsCompat.WEB_URL.toString()