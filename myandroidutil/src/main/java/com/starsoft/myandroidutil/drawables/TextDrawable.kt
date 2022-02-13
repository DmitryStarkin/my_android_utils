/*
 * Copyright (c) 2021. Oryna Starkina Contacts: oryna.stark@gmail.com
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

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.TypedValue


/**
 * Created by Dmitry Starkin on 08.05.2021 13:41.
 * @hiden
 */
class TextDrawable (res: Resources, text: CharSequence, textColor: Int? = null, size: Int? = null) : Drawable() {

    companion object {
        private const val DEFAULT_COLOR = Color.WHITE
        private const val DEFAULT_TEXT_SIZE = 5
    }

    private var mPaint: Paint = Paint()
    private var mText: CharSequence = text
    private var mIntrinsicWidth: Int = 0
    private var mIntrinsicHeight: Int = 0

    init {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.color = textColor ?: DEFAULT_COLOR
        mPaint.textAlign = Paint.Align.LEFT
        val textSize = TypedValue.applyDimension(
            TypedValue.TYPE_DIMENSION,
            DEFAULT_TEXT_SIZE.toFloat(), res.displayMetrics
        )
        mPaint.textSize = textSize
        @Suppress("MagicNumber")
        mIntrinsicWidth = size ?: (mPaint.measureText(mText, 0, mText.length) + .2).toInt()
        mIntrinsicHeight = size ?: mPaint.getFontMetricsInt(null)
    }

    override fun draw(canvas: Canvas) {
        val bounds = bounds

        val xPos = bounds.centerX() - mPaint.measureText(mText.toString()) / 2
        val yPos = bounds.centerY() - (mPaint.descent() + mPaint.ascent()) / 2

        canvas.drawColor(Color.TRANSPARENT)
        canvas.drawText(mText.toString(), xPos, yPos, mPaint)
    }

    override fun getOpacity(): Int = mPaint.alpha

    override fun getIntrinsicWidth(): Int = mIntrinsicWidth

    override fun getIntrinsicHeight(): Int = mIntrinsicHeight

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(filter: ColorFilter?) {
        mPaint.colorFilter = filter
    }
}