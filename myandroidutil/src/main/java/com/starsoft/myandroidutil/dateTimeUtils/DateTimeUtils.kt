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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Dmitry Starkin on 08.05.2021 13:53.
 */
const val DAYS_IN_MONTH = 30L
const val DAYS_IN_YEAR = 365L
const val HOURS_IN_DAY = 24L
const val HOURS_IN_WEEK = 168L
const val ONE_UNIT = 1L
const val TWO_UNIT = 2L
const val ZERO_DAYS = 0

private const val TODAY_DATE_FORMAT = "yyyy-MM-dd"
private val todayDataFormatter: DateFormat by lazy {
    SimpleDateFormat(TODAY_DATE_FORMAT, Locale.US)
}

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
        try{
            formatter.parse(timeString)
        } catch (e: ParseException){
            e.printStackTrace()
            null
        }
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

fun Context.getRelativeTimeStamp(timeIntervalMills: Long): String {
    val timeIntervalHour = timeIntervalMills / timeIntervalsMills[3]
    return when {
        timeIntervalHour < ONE_UNIT -> {
            resources.getString(R.string.minute_mark, (timeIntervalMills / timeIntervalsMills[4]).toString())
        }
        timeIntervalHour < HOURS_IN_DAY -> {
            resources.getString(R.string.hour_mark, (timeIntervalMills / timeIntervalsMills[3]).toString())
        }
        timeIntervalHour < HOURS_IN_WEEK -> {
            resources.getString(R.string.day_mark, (timeIntervalMills / timeIntervalsMills[2]).toString())
        }
        else -> {
            resources.getString(R.string.week_mark, ((timeIntervalMills / timeIntervalsMills[3])/HOURS_IN_WEEK).toString())
        }
    }
}

/**
 * returns today's date at 23: 59
 */

fun getTodayDate(): Date = Date((todayDataFormatter.format(Date()).getDate(todayDataFormatter)?.time ?: Date().time)
        + timeIntervalsMills[2] - timeIntervalsMills[5])