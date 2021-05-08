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

package com.starsoft.myandroidutil.dateTimeUtils

import android.content.Context
import com.starsoft.myandroidutil.R
import com.starsoft.myandroidutil.stringext.EMPTY_STRING
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Dmitry Starkin on 08.05.2021 13:53.
 */
const val DAYS_IN_MONTH = 30L
const val DAYS_IN_YEAR = 365L
const val ONE_UNIT = 1L
const val TWO_UNIT = 2L
const val ZERO_DAYS = 0

val timeIntervalsMills = listOf(
    TimeUnit.DAYS.toMillis(DAYS_IN_YEAR),
    TimeUnit.DAYS.toMillis(DAYS_IN_MONTH),
    TimeUnit.DAYS.toMillis(ONE_UNIT),
    TimeUnit.HOURS.toMillis(ONE_UNIT),
    TimeUnit.MINUTES.toMillis(ONE_UNIT),
    TimeUnit.SECONDS.toMillis(ONE_UNIT))

fun Context.getRelativeTimeString(timeIntervalMills: Long): String {
    val timeIntervalStrings = resources.getStringArray(R.array.time_intervals)
    val relativeString = StringBuffer()
    for (i in timeIntervalsMills.indices) {
        val interval = timeIntervalMills / timeIntervalsMills[i]
        if (interval > 0) {
            relativeString.append(interval)
                .append(timeIntervalStrings[i])
                .append(if (interval != ONE_UNIT) resources.getString(R.string.plural_suffix) else EMPTY_STRING)
                .append(resources.getString(R.string.ago))
            break
        }
    }
    return if (relativeString.toString().isEmpty()) resources.getString(R.string.zero_relative_string) else relativeString.toString()
}

fun String?.getDate(formatter: DateFormat): Date? =
    this?.let { timeString ->
        formatter.parse(timeString)
    }

fun Date?.getRestDays(anchor: Long): Int =
    this?.let {
        val interval = it.time - anchor
        if (interval <= 0) {
            ZERO_DAYS
        } else {
            (interval / timeIntervalsMills[2]).toInt()
        }
    } ?: ZERO_DAYS

fun Context.getDateStamp(date: Date, anchor: Long, formatter: DateFormat): String {
    val timeIntervalDays = (anchor - date.time) / timeIntervalsMills[2]
    return when {
        timeIntervalDays < ONE_UNIT -> {
            resources.getString(R.string.today)
        }
        timeIntervalDays < TWO_UNIT -> {
            resources.getString(R.string.yesterday)
        }
        else -> {
            formatter.format(date)
        }
    }
}