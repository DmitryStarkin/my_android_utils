/*
 * Copyright (c) 2023. Dmitry Starkin Contacts: t0506803080@gmail.com
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

package com.starsoft.myandroidutil.vibrator

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.IntRange
import com.starsoft.myandroidutil.providers.ContextProvider
import com.starsoft.myandroidutil.providers.mainContext
import com.starsoft.myandroidutil.timeutils.ScheduledJobTimer


/**
 * Created by Dmitry Starkin on 15.03.2023 15:15.
 */
const val VIBRATE_UNTIL_CANCEL = Int.MAX_VALUE
const val NO_REPEAT = -1
const val NO_AMPLITUDE = 0
const val MAX_AMPLITUDE = 250

@SuppressLint("WrongConstant")
@Suppress("DEPRECATION")
fun getVibrator():Vibrator = if (Build.VERSION.SDK_INT >= 31) {
    (mainContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
} else {
    // backward compatibility for Android API < 26
    // noinspection deprecation
    ContextProvider.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
}

@SuppressLint("ObsoleteSdkInt")
@Suppress("DEPRECATION")
fun Vibration.launch(): Boolean{
    if(!getVibrator().hasVibrator()) {
        return false
    }
    getVibrator().cancel()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        if(amplitude == VibrationEffect.DEFAULT_AMPLITUDE || !getVibrator().hasAmplitudeControl()){
            getVibrator().vibrate(VibrationEffect.createWaveform(pattern,  if(repeatCount == VIBRATE_UNTIL_CANCEL){1}else{NO_REPEAT}))
        } else {
            getVibrator().vibrate(VibrationEffect.createWaveform(pattern, amplitudePattern, if(repeatCount == VIBRATE_UNTIL_CANCEL){1}else{NO_REPEAT}))
        }

    } else {
        // backward compatibility for Android API < 26
        // noinspection deprecation
        getVibrator().vibrate(pattern, if(repeatCount == VIBRATE_UNTIL_CANCEL){1}else{NO_REPEAT})
    }
    return true
}

fun stopVibrate() {
    getVibrator().cancel()
}

interface Vibration{
    val startDelay: Long
    val vibrateDuration: Long
    val sleepDuration: Long
    val repeatCount: Int
    @get:IntRange(from = -1, to = 250)
    val amplitude: Int
        get() = -1

    val pattern: LongArray
        get() = (if(repeatCount <= 1 || repeatCount == VIBRATE_UNTIL_CANCEL){
            longArrayOf(startDelay, vibrateDuration, sleepDuration)
        } else {
            val draftPattern = ArrayList<Long>()
            draftPattern.add(startDelay)
            for(i in 0 until repeatCount){
                draftPattern.add(vibrateDuration)
                draftPattern.add(sleepDuration)
            }
            draftPattern.toLongArray()
        })

    val amplitudePattern: IntArray
        get() = (if(repeatCount <= 1 || repeatCount == VIBRATE_UNTIL_CANCEL){
            intArrayOf(NO_AMPLITUDE, amplitude, NO_AMPLITUDE)
        } else {
            val draftPattern = ArrayList<Int>()
            draftPattern.add(NO_AMPLITUDE)
            for(i in 0 until repeatCount){
                draftPattern.add(amplitude)
                draftPattern.add(NO_AMPLITUDE)
            }
            draftPattern.toIntArray()
        })
}

data class VibrationContainer(override val startDelay: Long,
                              override val vibrateDuration: Long,
                              override val sleepDuration: Long,
                              override val repeatCount: Int,
                              override val amplitude: Int = -1
) :Vibration

enum class VibrateNotifications(override val startDelay: Long,
                                override val vibrateDuration: Long,
                                override val sleepDuration: Long,
                                override val repeatCount: Int
                                ): Vibration{
    LONG_VIBRATION(0L, 1000L, 0L, NO_REPEAT),
    SHORT_VIBRATION(0L, 100L, 0L, NO_REPEAT)
}