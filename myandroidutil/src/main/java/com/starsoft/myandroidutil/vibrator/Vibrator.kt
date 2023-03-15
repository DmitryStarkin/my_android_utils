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
import com.starsoft.myandroidutil.providers.ContextProvider
import com.starsoft.myandroidutil.providers.mainContext
import com.starsoft.myandroidutil.timeutils.ScheduledJobTimer


/**
 * Created by Dmitry Starkin on 15.03.2023 15:15.
 */
private const val MESSAGE_REPEAT = 1

private val handler = ScheduledJobTimer()

@SuppressLint("WrongConstant")
@Suppress("DEPRECATION")
fun getVibrator(): Vibrator = if (Build.VERSION.SDK_INT >= 31) {
    (mainContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
} else {
    // backward compatibility for Android API < 26
    // noinspection deprecation
    ContextProvider.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
}

@Suppress("DEPRECATION")
fun Vibration.launch(){
    if(handler.hasMessages(MESSAGE_REPEAT)){
        handler.cancelScheduledJob(MESSAGE_REPEAT)
    }
    getVibrator().cancel()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        getVibrator().vibrate(VibrationEffect.createWaveform(pattern, repeat))
    } else {
        // backward compatibility for Android API < 26
        // noinspection deprecation

        getVibrator().vibrate(pattern, repeat)
    }
    if(repeat != -1 && duration > 0){
        handler.schedule(MESSAGE_REPEAT, duration){
            getVibrator().cancel()
        }
    }
}

fun stopVibrate() {
    handler.removeCallbacksAndMessages(null)
    getVibrator().cancel()
}

interface Vibration{
    val delay: Long
    val vibrate: Long
    val sleep: Long
    val repeat: Int
    val duration: Long
    val pattern: LongArray
        get() = longArrayOf(delay, vibrate, sleep)
}

enum class VibrateNotifications(override val delay: Long,
                                override val vibrate: Long,
                                override val sleep: Long,
                                override val repeat: Int,
                                override val duration: Long): Vibration{
    LONG_VIBRATION(0L, 1000L, 0L, -1, 0L),
    SHORT_VIBRATION(0L, 100L, 0L, -1, 0L)
}