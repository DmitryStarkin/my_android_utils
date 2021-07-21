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

@file:JvmName("StringUtils")

package com.starsoft.myandroidutil.stringext

import android.content.res.Configuration
import android.content.res.Resources.NotFoundException
import android.graphics.Typeface
import android.os.Build
import android.os.LocaleList
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.annotation.IntRange
import androidx.annotation.RequiresApi
import androidx.core.text.toSpannable
import com.starsoft.myandroidutil.providers.mainContext
import java.util.*
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

// This File Created at 25.11.2020 13:33.
const val EMPTY_STRING = ""
const val SPACE = " "
private val COMMON_LOCALE = Locale.US
val BOLD_SPAN = StyleSpan(Typeface.BOLD)

fun String.insertTo(position: Int, string: CharSequence): CharSequence{

    return StringBuilder(this).insert(position, string) as CharSequence
}

fun String.boldMatches(regexString: String): Spannable =
    this.applyStyleSpanToMatches(regexString, BOLD_SPAN)

fun String.applyStyleSpanToMatches(regexString: String, span: StyleSpan): Spannable {
    val result = SpannableString.valueOf(this)
    if(regexString.isEmpty()) return result
    val pattern = try{
        Pattern.compile(regexString)
    } catch (e: PatternSyntaxException){
        return result
    }
    val matcher = pattern.matcher(result)
    while (matcher.find()) {
        val start = matcher.start()
        val end = matcher.end()
        result.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return result
}

fun CharSequence.getFirstSafety(): Char =
    if(isNotBlank()){
        first()
    } else {
        Character.MIN_VALUE
    }

fun CharSequence.applyTypefaceStyle(@IntRange(from = 0, to = Typeface.BOLD_ITALIC.toLong()) style: Int
): Spannable = this.toSpannable().run {
    setSpan(
        StyleSpan(style),
        0, length,
        Spannable.SPAN_INCLUSIVE_INCLUSIVE
    )
    this
}

@RequiresApi(Build.VERSION_CODES.N)
private fun getStringByLocale(locale: Locale, id: Int): String? {
    var string: String? = null
    try {
        val commonString: String = getFromResources(COMMON_LOCALE, id)
        if (locale.language == COMMON_LOCALE.getLanguage()) {
            return commonString
        }
        string = getFromResources(locale, id)
        if (string == commonString) {
            string = null
        }
    } catch (e: NotFoundException) {
        e.printStackTrace()
    }
    return string
}

@RequiresApi(Build.VERSION_CODES.N)
private fun getFromResources(locale: Locale, id: Int): String {
    val baseResources = mainContext.resources
    val config = Configuration(baseResources.configuration)
    config.setLocales(LocaleList(locale))
    val tempContext = mainContext.createConfigurationContext(config)
    return tempContext.getString(id)
}