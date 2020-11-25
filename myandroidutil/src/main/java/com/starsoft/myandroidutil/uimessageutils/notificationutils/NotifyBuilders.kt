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

package com.starsoft.myandroidutil.uimessageutils.notificationutils

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.starsoft.myandroidutil.providers.ContextProvider
import com.starsoft.myandroidutil.providers.mainContext


// This File Created at 25.11.2020 14:28.

const val IMPORTANCE_DEFAULT: Int = 3 //simular NotificationManager.IMPORTANCE_DEFAULT

@SuppressLint("WrongConstant")
fun createNotificationChannelIfNeeded(
    channelName: String,
    chanelDescription: String,
    channelId: String,
    importance: Int = NotificationManagerCompat.IMPORTANCE_DEFAULT
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val mChannel = NotificationChannel(channelId, channelName, importance)
        mChannel.description = chanelDescription
        val notificationManager =
            mainContext.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }
}

fun Context.createNotification(
    channelId: String,
    ongoing: Boolean,
    onNotifyClickPIntent: PendingIntent? = null,
    ticker: String,
    title: String,
    text: String,
    smallIcon: Int,
    color: Int,
    buttons: List<NotifyButton>? = null
): Notification {

    return NotificationCompat.Builder(this, channelId)
        .setOngoing(ongoing)
        .setSmallIcon(smallIcon)
        .setColor(ContextCompat.getColor(this, color))
        .setTicker(ticker)
        .setContentTitle(title)
        .setContentText(text)
        .setContentIntent(onNotifyClickPIntent)
        .setWhen(System.currentTimeMillis())
        .apply {
            if (buttons != null) {
                for (button in buttons) {
                    addAction(button.icon, button.title, button.pendingIntent)
                }
            }
        }
        .build()
}

fun isNotifyGlobalDisabled(channelId: String? = null): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val mNotificationManager =
            mainContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!mNotificationManager.areNotificationsEnabled()) {
            return true
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId?.apply {
                val channel = mNotificationManager.getNotificationChannel(this)
                channel?.apply { return this.getImportance() == NotificationManager.IMPORTANCE_NONE }
            }

        }
    }
    return false
}

data class NotifyButton(val icon: Int, val title: CharSequence, val pendingIntent: PendingIntent)