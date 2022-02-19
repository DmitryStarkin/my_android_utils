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
import com.starsoft.myandroidutil.drawables.Scale

/**
 * Created by Dmitry Starkin on 07.02.2022 18:38.
 */
class DotFillingBitmapFactory @JvmOverloads constructor(
    private val squareSize: Int = 40,
    private val dotSize: Int = 5,
    private val bgColor: Int = Color.WHITE,
    private val dotColor: Int = Color.GRAY
) : FillingBitmapFactory {

    init {
        if (dotSize > squareSize) {
            throw Exception("Incorrect dot size")
        }
    }

    override fun createFillingBitmap(fillingArea: Rect, fitting: Boolean): Pair<Bitmap, Scale> {

        val scale = if (fitting) {
            val widthMultiple = (fillingArea.width() /  squareSize) * squareSize  + dotSize
            val heightMultiple = (fillingArea.height() /  squareSize) * squareSize + dotSize
            Scale(fillingArea.width().toFloat()/widthMultiple.toFloat(), fillingArea.height().toFloat()/heightMultiple.toFloat())
        } else {
            Scale()
        }

        val bitmap = Bitmap.createBitmap(squareSize, squareSize, Bitmap.Config.ARGB_8888)

        val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bitmapPaint.style = Paint.Style.FILL

        val canvas = Canvas(bitmap)

        val dotRect = RectF(0F, 0F, dotSize.toFloat(), dotSize.toFloat())

        canvas.drawColor(bgColor)
        bitmapPaint.color = dotColor
        canvas.drawOval(dotRect, bitmapPaint)
        return Pair(bitmap, scale)
    }
}