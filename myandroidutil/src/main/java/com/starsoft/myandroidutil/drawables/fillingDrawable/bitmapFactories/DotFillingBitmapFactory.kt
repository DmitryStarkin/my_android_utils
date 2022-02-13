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

package com.starsoft.myandroidutil.drawables.fillingDrawable.bitmapFactories

import android.graphics.*
import com.starsoft.myandroidutil.drawables.FillingBitmapFactory

/**
 * Created by Dmitry Starkin on 07.02.2022 18:38.
 */
class DotFillingBitmapFactory(
    private val squareSize: Int = 10,
    private val dotSize: Int = 40,
    private val colorOdd: Int = -0x3d3d3e,
    private val colorEven: Int = -0xc0c0d
) : FillingBitmapFactory {

    init {
        if (dotSize > squareSize) {
            throw Exception("Incorrect dot size")
        }
    }

    override fun createFillingBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(squareSize, squareSize, Bitmap.Config.ARGB_8888)

        val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bitmapPaint.style = Paint.Style.FILL

        val canvas = Canvas(bitmap)

        val rect = Rect(0, 0, squareSize, squareSize)
        val dotRect = RectF(0F, 0F, dotSize.toFloat(), dotSize.toFloat())

        bitmapPaint.color = colorOdd
        canvas.drawRect(rect, bitmapPaint)

        bitmapPaint.color = colorEven
        canvas.drawOval(dotRect, bitmapPaint)
        return bitmap
    }
}