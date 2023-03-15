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

package com.starsoft.myandroidutil.soundNotificator

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import androidx.annotation.RawRes
import com.starsoft.myandroidutil.providers.mainContext
import com.starsoft.myandroidutil.soundNotificator.interfaces.NotifyPlayer


/**
 * Created by Dmitry Starkin on 15.03.2023 15:24.
 */
class NotifyPlayerImpl: SoundPool.OnLoadCompleteListener, NotifyPlayer {

companion object{
    private const val LOAD_PRIORITY = 1
    private const val CLOSE_INTERVAL = 3000L
    private const val LOAD_COMPLETED = 0
    private const val PLAY_PRIORITY = 0
    private const val LOOP_MODE = 0
    private const val PLAY_RATE = 1f
}

    override var volume = 0.8f

    private var soundPool: SoundPool? = null

    private fun initPool(): SoundPool =

        SoundPool.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .build().apply {
                setOnLoadCompleteListener(this@NotifyPlayerImpl)
            }

    @RawRes
    private var  myNotify:  List<Int> = emptyList()

    private val handler = Handler(Looper.getMainLooper())

    private val loading: HashMap<Int, Int> = HashMap()
    private val loaded: HashMap<Int, Int> = HashMap()

    override fun onLoadComplete(soundPool: SoundPool?, sampleId: Int, status: Int) {
        soundPool?.apply {
            if (status == LOAD_COMPLETED && !loaded.containsValue(sampleId)) {

                if(sampleId in loading){
                    loading[sampleId]?.apply{
                        loaded[this] = sampleId
                        loading.remove(sampleId)
                    }
                }
            }
        }
    }

    override fun loadNotifications(@RawRes ids: List<Int>): NotifyPlayer {
        myNotify = ids
        clearNotifySets()
        if(myNotify.isNotEmpty()){
            soundPool?.release()
            soundPool = initPool()
            soundPool?.apply {
                myNotify.forEach {
                    loading[load(mainContext, it, LOAD_PRIORITY)] = it
                }
            }
        }
        return this
    }

    override fun close() {
        soundPool?.release()
        soundPool = null
        myNotify = emptyList()
        clearNotifySets()
    }

    private fun clearNotifySets(){
        loading.clear()
        loaded.clear()
    }

    override fun isReady(): Boolean  = soundPool != null && loaded.isNotEmpty()

    override fun isInit(): Boolean = myNotify.isNotEmpty()

    override fun playNotify(@RawRes resourceId: Int): Boolean =
        soundPool?.let {
            if(resourceId !in myNotify) {
                return@let false
            }
            if (resourceId in loaded) {
                loaded[resourceId]?.apply {
                    it.play(this, volume, volume, PLAY_PRIORITY, LOOP_MODE, PLAY_RATE)
                } ?: run{return@let false}
                true
            } else {
                loading[it.load(mainContext, resourceId, LOAD_PRIORITY)] = resourceId
                false
            }
        } ?: false


    override fun playNotifyAndClose(@RawRes resourceId: Int) {
        if(playNotify(resourceId)){
            handler.postDelayed({
                close()
            }, CLOSE_INTERVAL)
        } else {
            close()
        }
    }
}