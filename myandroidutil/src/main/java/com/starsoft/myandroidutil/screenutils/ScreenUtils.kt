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

@file:JvmName("ScreenHelper")

package com.starsoft.myandroidutil.screenutils

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.Display
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.starsoft.myandroidutil.providers.mainContext


/**
 * Created by Dmitry Starkin at 25.11.2020 12:56.
 */

fun View.isOver(view: View?): Boolean {
    view ?: return false
    val tLoc = IntArray(2)
    val vLoc = IntArray(2)
    this.getLocationOnScreen(tLoc)
    view.getLocationOnScreen(vLoc)
    val xCenter = tLoc[0] + this.measuredWidth / 2
    val yCenter = tLoc[1] + this.measuredHeight / 2
    return (xCenter > vLoc[0] && xCenter < vLoc[0] + view.measuredWidth) && (yCenter > vLoc[1] && yCenter < vLoc[1] + view.measuredHeight)
}

fun MotionEvent?.isTouchInside(view: View?): Boolean {
    view ?: return false
    this ?: return false
    val vLoc = IntArray(2)
    view.getLocationOnScreen(vLoc)
    val touchX = this.rawX.toInt()
    val touchY = this.rawY.toInt()
    val xBound = vLoc[0] + view.measuredWidth
    val yBound = vLoc[1] +  view.measuredHeight
    return (touchX > vLoc[0] && touchX < xBound) && (touchY > vLoc[1] && touchY < yBound)
}

fun MotionEvent.castTo(view: View): MotionEvent {
    if(!isTouchInside(view)) throw Exception("touch must be inside this view")
    val vLoc = IntArray(2)
    view.getLocationOnScreen(vLoc)
    return MotionEvent.obtain(downTime, eventTime, action, rawX - vLoc[0].toFloat(), rawY - vLoc[1].toFloat(), metaState)
}

fun View.pointPositionOnScreen(): Point {
    val location = IntArray(2)
    this.getLocationOnScreen(location)
    return Point(location[0], location[1])
}

fun View.topOffsetOf(other: View): Int =
    pointPositionOnScreen().y - other.pointPositionOnScreen().y

fun View.leftOffsetOf(other: View): Int =
    pointPositionOnScreen().x - other.pointPositionOnScreen().x

/**
 * get display size
 * @return display size
 */
private fun View.getDisplaySizePoint(): Point {
    val displaySize = Point()
    this.display?.getRealSize(displaySize)
    return displaySize
}

/**
 * Returns the size of the display in pixels by delegating to [Display.getRealSize].
 */
@Suppress("DEPRECATION")
fun Context.getDisplaySizeInPixels(): Point {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val bounds = windowManager.currentWindowMetrics.bounds
        Point(bounds.width(),  bounds.height() )
    } else {
        val displaySize = Point()
        windowManager.defaultDisplay.getRealSize(displaySize)
        return displaySize
    }
}

const val BASE_DPI = 160f

const val MM_IN_INCH = 25.4f

fun Context.convertDpToPixel(dp: Float): Float = (dp / BASE_DPI) * getAverageDisplayPxPerInch()

fun Context.getHorizontalDisplayPxPerInch(): Float = resources.displayMetrics.xdpi

fun Context.getVerticalDisplayPxPerInch(): Float = resources.displayMetrics.ydpi

fun Context.getAverageDisplayPxPerInch(): Float = (getHorizontalDisplayPxPerInch() + getVerticalDisplayPxPerInch()) / 2f

fun convertDpToPixel(dp: Float): Float = (dp / BASE_DPI) * averageDisplayPxPerInch

val displayHorizontalPxPerInch: Float get() = mainContext.getHorizontalDisplayPxPerInch()

val displayVerticalPxPerInch: Float get() = mainContext.getVerticalDisplayPxPerInch()

val displayVerticalPxPerMM: Float get() = displayVerticalPxPerInch/MM_IN_INCH

val displayHorizontalPxPerMM: Float get() =  displayHorizontalPxPerInch/MM_IN_INCH

val averageDisplayPxPerInch: Float get() = (displayVerticalPxPerInch + displayHorizontalPxPerInch) / 2f

val averageDisplayPxPerMM: Float get() = averageDisplayPxPerInch/MM_IN_INCH

fun Context.getDisplayDpiDensity(): Int = resources.displayMetrics.densityDpi
