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

package com.starsoft.myandroidutil.timeutils

import android.os.Handler
import android.os.Looper
import android.os.Message


/**
 * Created by Dmitry Starkin on 08.05.2021 12:57.
 */
class ScheduledJobTimer : Handler(Looper.getMainLooper()) {

    override fun handleMessage(msg: Message) {
        if (msg.obj is ScheduledJob) {
            (msg.obj as ScheduledJob).execute()
        }
    }

    private fun schedule(tag: Int, delay: Long, job: ScheduledJob) {
        removeMessages(tag)
        val message: Message = obtainMessage(tag, job)
        sendMessageDelayed(message, delay)
    }

    fun schedule(tag: Int, delay: Long, job: () -> Unit){
        schedule(tag, delay, object :ScheduledJob{
            override fun execute() {
                job.invoke()
            }
        })
    }

    fun cancelScheduledJob(tag: Int) {
        removeMessages(tag)
    }

    private interface ScheduledJob {
        fun execute()
    }
}