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
 * Created by Dmitry Starkin on 07.02.2022 18:44.
 */
class CheckerBoardFillingBitmapFactory @JvmOverloads constructor(
    private val size: Int = 40,
    private val colorOdd: Int = Color.WHITE,
    private val colorEven: Int = Color.GRAY
) : FillingBitmapFactory {

    override fun createFillingBitmap(fillingArea: Rect, fitting: Boolean): Pair<Bitmap, Scale> {

        val scale = if (fitting) {
            val widthMultiple = (fillingArea.width() / size) * size
            val heightMultiple = (fillingArea.height() / size) * size
            Scale(fillingArea.width().toFloat()/widthMultiple.toFloat(),
                fillingArea.height().toFloat()/heightMultiple.toFloat())
        } else {
            Scale()
        }

        val bitmap = Bitmap.createBitmap(size * 2, size * 2, Bitmap.Config.ARGB_8888)

        val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bitmapPaint.style = Paint.Style.FILL

        val canvas = Canvas(bitmap)

        val rect = Rect(0, 0, size, size)
        bitmapPaint.color = colorOdd
        canvas.drawRect(rect, bitmapPaint)

        rect.offset(size, size)
        canvas.drawRect(rect, bitmapPaint)

        bitmapPaint.color = colorEven
        rect.offset(-size, 0)
        canvas.drawRect(rect, bitmapPaint)

        rect.offset(size, -size)
        canvas.drawRect(rect, bitmapPaint)
        return Pair(bitmap, scale)
    }
}