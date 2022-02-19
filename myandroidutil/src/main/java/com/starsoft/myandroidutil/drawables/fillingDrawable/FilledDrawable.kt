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

package com.starsoft.myandroidutil.drawables

import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.BitmapShader


/**
 * Created by Dmitry Starkin on 07.02.2022 18:24.
 */

class FilledDrawable @JvmOverloads constructor(
    private val bitmapFactory: FillingBitmapFactory,
    private val offsets: Offsets = Offsets(),
    private val background: Int = Color.TRANSPARENT,
    private val fitting: Boolean = true
) : Drawable() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun draw(canvas: Canvas) {
        val fillingArea = Rect(
            offsets.startOffset,
            offsets.topOffset,
            bounds.width() - offsets.endOffset,
            bounds.height() - offsets.bottomOffset
        )
        val bitmapData = bitmapFactory.createFillingBitmap(fillingArea, fitting)
        val shader = BitmapShader(bitmapData.first, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        if(bitmapData.second.hasScale()){
            val matrix = Matrix()
            matrix.postScale(bitmapData.second.xScale, bitmapData.second.yScale)
            shader.setLocalMatrix(matrix)
        }
        paint.shader = shader
        canvas.drawColor(background)
        if(!fillingArea.isEmpty) {
            canvas.drawRect(fillingArea, paint)
        }
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }
}

interface FillingBitmapFactory {

    fun createFillingBitmap(fillingArea: Rect, fitting: Boolean = true): Pair<Bitmap, Scale>
}

data class Scale(
    val xScale: Float = 1f,
    val yScale: Float = 1f
){
    fun hasScale(): Boolean =
        xScale > 0f && yScale > 0f && (xScale != 1f || yScale != 1f)
}

data class Offsets(
    val startOffset: Int = 0,
    val endOffset: Int = 0,
    val topOffset: Int = 0,
    val bottomOffset: Int = 0
)




