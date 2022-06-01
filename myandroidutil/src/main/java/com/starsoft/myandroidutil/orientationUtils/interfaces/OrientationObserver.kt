/*
 * Copyright (c) 2022. Dmitry Starkin Contacts: t0506803080@gmail.com
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

package com.starsoft.myandroidutil.orientationUtils.interfaces

import android.content.res.Configuration
import android.view.OrientationEventListener
import android.view.Surface
import kotlin.math.abs


/**
 * Created by Dmitry Starkin on 18.05.2022 14:33.
 */
interface OrientationObserver {

    interface OrientationCallback {
        fun onOrientationChanged(observer: OrientationObserver)
    }

    val currentDeviceOrientation: Orientation

    val currentSystemScreenOrientation: Int

    val deviceScreenOrientation: Int

    val currentSurfaceRotation: SurfaceRotation

    val currentOriginPosition: OriginPosition

    val largestAxis: LargestAxis

    val largestAxisDirection:  LargestAxisDirection

    /** Whether the device's natural orientation is portrait.  */
    val isDeviceDefaultPortrait: Boolean

    val isOrientationReady: Boolean

    val isEnabled: Boolean

    fun rereadOrientation()

    fun enable()

    fun disable()

    fun addOrientationCallback(callback: OrientationCallback)

    fun removeOrientationCallback(callback: OrientationCallback)


    /** Origin Positions  */
    enum class OriginPosition(val degreeDiff: Int) {
        TOP_LEFT(0),
        TOP_RIGHT(270),
        BOTTOM_RIGHT(180),
        BOTTOM_LEFT(90);

        companion object{
            fun getByDegreeDiff(diff: Int):  OriginPosition =
                values().find {
                    it.degreeDiff == diff
                } ?: TOP_LEFT
        }
    }

    /** device orientations.  */
    enum class Orientation(val orientationDegree: Int, val orientationDegreeForLand: Int,  val orientationDegreeForPor: Int, val sisScreenOrientation: Int) {
        UNKNOWN(-1, -1,  -1, Configuration.ORIENTATION_UNDEFINED),
        PORTRAIT(0, 90, 0, Configuration.ORIENTATION_PORTRAIT),
        LANDSCAPE(270, 0, 90, Configuration.ORIENTATION_LANDSCAPE),
        REVERSE_PORTRAIT(180, 270, 180, Configuration.ORIENTATION_PORTRAIT),
        REVERSE_LANDSCAPE(90, 180, 270, Configuration.ORIENTATION_LANDSCAPE);

        companion object{
            fun getByDegree(degree: Int):  Orientation =
                if(degree == OrientationEventListener.ORIENTATION_UNKNOWN){
                    UNKNOWN
                } else {
                    values().find {
                        it != UNKNOWN && abs(degree - it.orientationDegree) <= 45
                    } ?: if (abs(degree - 360) <= 45) {
                        PORTRAIT
                    } else UNKNOWN
                }


            fun getBySisOrientation(sisScreenOrientation: Int):  Orientation =
                values().find {
                    it.sisScreenOrientation == sisScreenOrientation
                } ?:  UNKNOWN
        }
    }

    /** screen orientations.  */
    enum class SurfaceRotation(val rotationDegree: Int, val rotation: Int){
        SurfaceRotation0(0, Surface.ROTATION_0),
        SurfaceRotation90(90, Surface.ROTATION_90),
        SurfaceRotation180(180, Surface.ROTATION_180),
        SurfaceRotation270(270, Surface.ROTATION_270);

        companion object{
            fun getByRotation(intRotation: Int): SurfaceRotation =
                values().find {
                    it.rotation == intRotation
                } ?: SurfaceRotation0}
    }

    enum class  LargestAxis{
        X,
        Y
    }

    enum class  LargestAxisDirection{
        Forward,
        Backward
    }
}