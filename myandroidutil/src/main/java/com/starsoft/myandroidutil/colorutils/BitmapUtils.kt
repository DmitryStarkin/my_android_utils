/*
 * Copyright (c) 2024. Dmitry Starkin Contacts: t0506803080@gmail.com
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

package com.starsoft.myandroidutil.colorutils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.Px
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.component3
import androidx.core.graphics.component4
import androidx.core.graphics.drawable.toBitmap

/**
 * Created by Dmitry Starkin on 18.02.2024 11:49.
 */
fun Bitmap.toMutable(): Bitmap =
    if (this.isMutable) {
        this
    } else {
        val new = copy(this.config, true)
        recycle()
        new
    }

fun Bitmap.copyToMutable(config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap = copy(config, true)

fun Bitmap.copyToInMutable(config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap = copy(config, false)

fun Bitmap.clear(): Bitmap{
    Canvas(this).drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    return this
}

fun Bitmap?.drawOther(other: Bitmap): Bitmap {
    if(!isCanDrawOther(other)) throw Exception("Unable reset bitmap? check dimensions or mutable state")
    val canvas = Canvas(this!!)
    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    canvas.drawBitmap(other, 0f, 0f, null)
    return this
}

fun Bitmap?.isCanDrawOther(other: Bitmap): Boolean =
    this?.let{
        it.isMutable && it.width == other.width && it.height == other.height && !it.isRecycled
    } ?: false

fun Bitmap?.isCanReconfigureToNextDrawOther(other: Bitmap): Boolean =
    this?.let{
        it.isMutable && it.width >= other.width && it.height >= other.height && !it.isRecycled && it.config == other.config
    } ?: false

fun Bitmap?.reconfigureForOther(other: Bitmap): Bitmap? =
    this?.let{
        it.reconfigure(other.width, other.height, other.config)
        it
    }

fun Bitmap.cropRect(rect: Rect): Bitmap =
    Bitmap.createBitmap(this, rect.left, rect.top, rect.width(), rect.height()).also {
        this.recycle()
    }


fun Bitmap.cropCircle(rect: Rect): Bitmap {
    val path = Path()
    val targetBitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(targetBitmap)
    canvas.drawColor(TRANSPARENT_COLOR)
    path.moveTo(0f, 0f)
    path.addOval(RectF(0f, 0f, rect.width().toFloat(), rect.height().toFloat()), Path.Direction.CW)
    canvas.clipPath(path)
    val cropped = this.cropRect(rect)
    canvas.drawBitmap(this.cropRect(rect), 0f, 0f, null)
    cropped.recycle()
    return targetBitmap
}

fun Drawable.getCopyAsBitmap(hasAlpha: Boolean = true): Bitmap =

    if(this is BitmapDrawable){
        bitmap.run {
            copy(Bitmap.Config.ARGB_8888, true).also {
                it.setHasAlpha(hasAlpha)
            }
        }
    } else {
        val bitmap = Bitmap.createBitmap(intrinsicWidth,
            intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
        bitmap.also {
            it.setHasAlpha(hasAlpha)
        }
    }

fun Drawable.toBitmapWitchAlpha(
    @Px width: Int = intrinsicWidth,
    @Px height: Int = intrinsicHeight,
    config: Bitmap.Config? = null,
    hasAlpha: Boolean = true
): Bitmap = toBitmap(width, height, config).let {
    if(it.config != Bitmap.Config.ARGB_8888){
        getCopyAsBitmap(hasAlpha)
    } else {
        it.setHasAlpha(hasAlpha)
        it
    }
}

fun Drawable.getMyBitmapOrNull(): Bitmap? =
    if(this is BitmapDrawable){
        bitmap
    } else {
        null
    }

fun ImageView.resetBitmapWitchRecycle(new: Bitmap?){
    drawable?.getMyBitmapOrNull()?.let {
        if(new == it){
            setImageBitmap(new)
        } else {
            try {
                setImageBitmap(new)
            } catch (e: Throwable){
                e.printStackTrace()
            } finally {
                it.recycle()
            }
        }
    } ?: run{
        setImageBitmap(new)
    }
}

fun Drawable.toNewBitmap( @Px width: Int = intrinsicWidth,
                          @Px height: Int = intrinsicHeight,
                          config: Bitmap.Config? = null): Bitmap =
    toBitmap(width, height, config).let {
        if(this is BitmapDrawable && bitmap == it){
            val (oldLeft, oldTop, oldRight, oldBottom) = bounds
            val bitmap = Bitmap.createBitmap(width, height, config ?: Bitmap.Config.ARGB_8888)
            setBounds(0, 0, width, height)
            draw(Canvas(bitmap))
            setBounds(oldLeft, oldTop, oldRight, oldBottom)
            bitmap
        } else {
            it
        }
    }

