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

package com.starsoft.myandroidutil.orientationUtils

import android.content.Context
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.view.OrientationEventListener
import android.view.WindowManager
import com.starsoft.myandroidutil.logutils.log_d
import com.starsoft.myandroidutil.orientationUtils.interfaces.OrientationObserver
import com.starsoft.myandroidutil.orientationUtils.interfaces.OrientationObserver.Orientation.UNKNOWN
import com.starsoft.myandroidutil.providers.mainContext
import com.starsoft.myandroidutil.timeutils.ScheduledJobTimer
import kotlin.math.abs

/**
 * Created by Dmitry Starkin on 18.05.2022 14:34.
 */
class OrientationObserverImpl : OrientationObserver {

    companion object{
        const val NOTIFY_DELAY = 500L
        const val NOTIFY_TAG = 1
        const val TEST_TAG = 2
    }

    private val timer = ScheduledJobTimer()

    private val orientationMonitorCallbacks = ArrayList<OrientationObserver.OrientationCallback>()

    override val isOrientationReady: Boolean
        get() = deviceOrientation != UNKNOWN

    override val isEnabled: Boolean
        get() = isEnabledInternal

    private var isEnabledInternal = false

    private val orientationEventListener: OrientationEventListener =
        object : OrientationEventListener(mainContext) {
            override fun onOrientationChanged(degree: Int) {
                val newOrientation: OrientationObserver.Orientation =
                    OrientationObserver.Orientation.getByDegree(if (isDeviceDefaultPortrait) degree else degree - 90)
                if (newOrientation != deviceOrientation && newOrientation != UNKNOWN) {
                    deviceOrientation = newOrientation
                    if(deviceOrientation == previousDeviceOrientation && timer.hasMessages(NOTIFY_TAG)){
                        timer.removeMessages(NOTIFY_TAG)
                    } else {
                        previousDeviceOrientation = deviceOrientation
                        timer.schedule(NOTIFY_TAG, NOTIFY_DELAY) {
                            notifyCallbacks()
                        }
                    }
                }
            }
        }

    private var previousDeviceOrientation: OrientationObserver.Orientation =
        UNKNOWN

    private var deviceOrientation: OrientationObserver.Orientation =
        UNKNOWN

    override val currentDeviceOrientation get() = if (orientationEventListener.canDetectOrientation()){
        if(deviceOrientation != UNKNOWN){
            deviceOrientation
        } else {
            OrientationObserver.Orientation.getBySisOrientation(currentSystemScreenOrientation)
        }
    } else {
        OrientationObserver.Orientation.getBySisOrientation(currentSystemScreenOrientation)
    }

    override val currentSystemScreenOrientation get() = mainContext.resources.configuration.orientation

    override val deviceScreenOrientation: Int
        get() = if (currentDeviceOrientation == UNKNOWN) {
            if (isDeviceDefaultPortrait) {
                Configuration.ORIENTATION_PORTRAIT
            } else {
                Configuration.ORIENTATION_LANDSCAPE
            }
        } else {
            currentDeviceOrientation.sisScreenOrientation
        }

    override val currentSurfaceRotation: OrientationObserver.SurfaceRotation
        get() =
            OrientationObserver.SurfaceRotation.getByRotation(
                (mainContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                    .defaultDisplay
                    .rotation
            )

    override val currentOriginPosition: OrientationObserver.OriginPosition
        get() = if (currentDeviceOrientation == UNKNOWN) {
            OrientationObserver.OriginPosition.TOP_LEFT
        } else {
            val deviceOrientationDeg = if (isDeviceDefaultPortrait) {
                if(currentDeviceOrientation == OrientationObserver.Orientation.REVERSE_PORTRAIT && currentSurfaceRotation == OrientationObserver.SurfaceRotation.SurfaceRotation270){
                    currentDeviceOrientation.orientationDegreeForPor + 360
                } else {
                    currentDeviceOrientation.orientationDegreeForPor
                }
            } else {
                currentDeviceOrientation.orientationDegreeForLand
            }
            OrientationObserver.OriginPosition.getByDegreeDiff(abs(deviceOrientationDeg - currentSurfaceRotation.rotationDegree))
        }

    override val largestAxis: OrientationObserver.LargestAxis
        get() {
            val dm = DisplayMetrics()
            (mainContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(
                dm
            )
            val width = dm.widthPixels
            val height = dm.heightPixels
            return if (height > width) {
                OrientationObserver.LargestAxis.Y
            } else {
                OrientationObserver.LargestAxis.X
            }
        }

    override val largestAxisDirection: OrientationObserver.LargestAxisDirection
        get() = when (largestAxis) {
            OrientationObserver.LargestAxis.X -> {
                when (currentOriginPosition) {
                    OrientationObserver.OriginPosition.TOP_LEFT, OrientationObserver.OriginPosition.BOTTOM_LEFT -> {
                        if (isDeviceDefaultPortrait) {
                            if (deviceScreenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                                OrientationObserver.LargestAxisDirection.Forward
                            } else {
                                OrientationObserver.LargestAxisDirection.Backward
                            }
                        } else {
                            OrientationObserver.LargestAxisDirection.Forward
                        }

                    }
                    OrientationObserver.OriginPosition.BOTTOM_RIGHT, OrientationObserver.OriginPosition.TOP_RIGHT -> {
                        if (isDeviceDefaultPortrait) {
                            if (deviceScreenOrientation == Configuration.ORIENTATION_PORTRAIT) {
                                OrientationObserver.LargestAxisDirection.Forward
                            } else {
                                OrientationObserver.LargestAxisDirection.Backward
                            }
                        } else {
                            OrientationObserver.LargestAxisDirection.Backward
                        }
                    }
                }
            }
            OrientationObserver.LargestAxis.Y -> {
                when (currentOriginPosition) {

                    OrientationObserver.OriginPosition.TOP_LEFT, OrientationObserver.OriginPosition.BOTTOM_LEFT -> {
                        if (!isDeviceDefaultPortrait) {
                            if (deviceScreenOrientation == Configuration.ORIENTATION_PORTRAIT) {
                                OrientationObserver.LargestAxisDirection.Backward
                            } else {
                                OrientationObserver.LargestAxisDirection.Forward
                            }
                        } else {
                            OrientationObserver.LargestAxisDirection.Forward
                        }
                    }
                    OrientationObserver.OriginPosition.BOTTOM_RIGHT, OrientationObserver.OriginPosition.TOP_RIGHT -> {
                        if (!isDeviceDefaultPortrait) {
                            if (deviceScreenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                                OrientationObserver.LargestAxisDirection.Forward
                            } else {
                                OrientationObserver.LargestAxisDirection.Backward
                            }
                        } else {
                            OrientationObserver.LargestAxisDirection.Backward
                        }
                    }
                }
            }
        }

    override val isDeviceDefaultPortrait: Boolean
        get() =
            ((currentSurfaceRotation == OrientationObserver.SurfaceRotation.SurfaceRotation0 || currentSurfaceRotation == OrientationObserver.SurfaceRotation.SurfaceRotation180) && (largestAxis == OrientationObserver.LargestAxis.Y)
                    || ((currentSurfaceRotation == OrientationObserver.SurfaceRotation.SurfaceRotation90 || currentSurfaceRotation == OrientationObserver.SurfaceRotation.SurfaceRotation270)
                    && (largestAxis == OrientationObserver.LargestAxis.X)))



    override fun rereadOrientation() {
        if(isEnabledInternal){
            disable()
            enable()
        }
    }

    private fun notifyCallbacks() {
        orientationMonitorCallbacks.forEach {
            it.onOrientationChanged(this@OrientationObserverImpl)
        }
    }

    override fun enable() {
        log_d { "enable OrientationMonitor${this.hashCode()}" }
        if (!orientationEventListener.canDetectOrientation()) return
        orientationEventListener.enable()
        isEnabledInternal = true
    }

    override fun disable() {
        orientationEventListener.disable()
        deviceOrientation = UNKNOWN
        isEnabledInternal = false
    }

    override fun addOrientationCallback(callback: OrientationObserver.OrientationCallback) {
        if (callback !in orientationMonitorCallbacks) {
            orientationMonitorCallbacks.add(callback)
        }
    }

    override fun removeOrientationCallback(callback: OrientationObserver.OrientationCallback) {
        orientationMonitorCallbacks.remove(callback)
    }
}