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

package com.starsoft.myandroidutil.soundNotificator.interfaces

import androidx.annotation.RawRes


/**
 * Created by Dmitry Starkin on 15.03.2023 15:22.
 */
interface NotifyPlayer {
    /**
     * Plays a notification from the resource
     * if this notification has been loaded
     * @param resourceId id resource to play
     * @return true if  notification start playing false otherwise
     */
    fun playNotify(@RawRes resourceId: Int): Boolean


    /**
     * Plays a notification from the resource
     * if this notification has been loaded
     * and close [close] this player
     * @param resourceId id resource to play
     */
    fun playNotifyAndClose(@RawRes resourceId: Int)

    /**
     * loads notifications to the player
     * if the player is not initialized then it is initialized
     * @param ids ids resource to play
     * @return NotifyPlayer [NotifyPlayer]
     */
    fun loadNotifications(@RawRes ids: List<Int>): NotifyPlayer

    /**
     * close Player and Release  resources.
     * Release all memory and native resources used Player.
     * The Player can no longer be used until the loadNotifications [loadNotifications] method is called
     * @param resourceId id resource to play
     */
    fun close()

    /**
     * @return true if  Player ready to play  false otherwise
     */
    fun isReady(): Boolean

    /**
     * @return true if  player has notifications for play false otherwise
     * true means that the player has notifications for playback,
     * but it does not mean that it is ready for playback,
     * use the isReady [isReady] to determine this
     */
    fun isInit(): Boolean


    var volume: Float
}