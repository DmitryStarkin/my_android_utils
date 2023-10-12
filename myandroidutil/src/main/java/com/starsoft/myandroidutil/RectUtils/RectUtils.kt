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

package com.starsoft.myandroidutil.RectUtils

import android.graphics.Matrix
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import androidx.core.graphics.toRect
import androidx.core.graphics.toRectF

/**
 * Created by Dmitry Starkin on 12.10.2023 13:59.
 */

private const val DEFAULT_MATCHES_ALLOWANCE_PX = 1

fun Rect.isMatches(other: Rect, allowance: Int = DEFAULT_MATCHES_ALLOWANCE_PX): Boolean =
    this.left in other.left - allowance..other.left + allowance &&
            this.right in other.right - allowance..other.right + allowance &&
            this.top in other.top - allowance..other.top + allowance &&
            this.bottom in other.bottom - allowance..other.bottom + allowance

fun Rect.castToOuterCoordinates(outerRect: Rect): Rect =
    Rect(
        this.left - outerRect.left,
        this.top - outerRect.top,
        this.right - outerRect.left,
        this.bottom - outerRect.top
    )

fun Rect.castToOuterCoordinateSystem(outerRect: Rect): Rect =
    Rect(
        this.left + outerRect.left,
        this.top + outerRect.top,
        this.right + outerRect.left,
        this.bottom + outerRect.top
    )

fun Rect.scaleToNewOuter(oldOuterSize: Pair<Int, Int>, newOuter: Rect): Rect {
    val scaleX = newOuter.width().toFloat() / oldOuterSize.first.toFloat()
    val scaleY = newOuter.height().toFloat() / oldOuterSize.second.toFloat()
    return this.scale(scaleX, scaleY)
}

private fun Rect.scale(scaleX: Float, scaleY: Float): Rect =
    if (scaleX == 1f && scaleY == 1f) {
        this
    } else {
        Rect(
            (left.toFloat() * scaleX).toInt(),
            (top.toFloat() * scaleY).toInt(),
            (right.toFloat() * scaleX).toInt(),
            (bottom.toFloat() * scaleY).toInt()
        )
    }

val Rect.minDimension : Float get() = java.lang.Float.min(width().toFloat(), height().toFloat())

val RectF.minDimension : Float get() = java.lang.Float.min(width(), height())

val Rect.maxDimension : Float get() = java.lang.Float.max(width().toFloat(), height().toFloat())

val RectF.maxDimension : Float get() = java.lang.Float.max(width(), height())

fun Rect.scaleInPlace(scaleX: Float, scaleY: Float): Rect =
    if (scaleX == 1f && scaleY == 1f) {
        this
    } else {
        Rect(
            left,
            top,
            left + (width().toFloat() * scaleX).toInt(),
            top + (height().toFloat() * scaleY).toInt()
        )
    }

fun Rect.setNewSize(newWidth: Int, newHeight: Int): Rect =
    Rect(
        left,
        top,
        if (newWidth <= 0) {
            right
        } else {
            left + newWidth
        },
        if (newHeight <= 0) {
            bottom
        } else {
            top + newHeight
        }
    )


fun Rect.scale(scaleX: Float, scaleY: Float, scalePoint: Point): Rect =
    if (scaleX == 1f && scaleY == 1f) {
        this
    } else {
        this.goToNewReferencePoint(scalePoint).scale(scaleX, scaleY).returnToOriginCoordinates(scalePoint)
    }

fun Rect.getOffsetInOuter(outer: Rect): Point =
    if (this.isEmpty || outer.isEmpty || this.top < outer.top || this.left < outer.left || this.bottom > outer.bottom || this.right > outer.right) {
        Point()
    } else {
        Point(this.left - outer.left, this.top - outer.top)
    }

private fun Rect.goToNewReferencePoint(point: Point): Rect =
    Rect(
        left - point.x,
        top - point.y,
        right - point.x,
        bottom - point.y
    )

private fun Rect.returnToOriginCoordinates(point: Point): Rect =
    Rect(
        left + point.x,
        top + point.y,
        right + point.x,
        bottom + point.y
    )

fun List<PointF>.convertInTo(rect: Rect): List<PointF> =
    mapNotNull {
        if (rect.contains(it.x.toInt(), it.y.toInt())) {
            PointF(it.x - rect.left.toFloat(), it.y - rect.top.toFloat())
        } else {
            null
        }
    }

fun PointF.convertInTo(rect: Rect, scaleFactor: Float = 1f): PointF =
    PointF((x - rect.left.toFloat()) * scaleFactor, (y - rect.top.toFloat()) * scaleFactor)


fun PointF.convertOutTo(rect: Rect, scaleFactor: Float = 1f): PointF =
    PointF(x / scaleFactor + rect.left.toFloat(), y / scaleFactor + rect.top.toFloat())

fun List<PointF>.convertInParentOff(rect: Rect): List<PointF> =
    map {
        PointF(it.x + rect.left.toFloat(), it.y + rect.top.toFloat())
    }

fun List<PointF>.fitIntoRect(targetRect: Rect, scale: Matrix.ScaleToFit = Matrix.ScaleToFit.FILL): List<PointF> =
    fitIntoRect(targetRect, Matrix(), scale)

fun List<PointF>.fitIntoRect(targetRect: Rect,
                             matrix: Matrix,
                             scale: Matrix.ScaleToFit = Matrix.ScaleToFit.FILL): List<PointF> {
    matrix.reset()
    val points = this.toPointArray()
    matrix.setRectToRect(this.getOutRectF(), targetRect.toRectF(), scale)
    matrix.mapPoints(points)
    return points.toPointFList()
}

fun List<PointF>.getOutRectF(): RectF =

    if (isNotEmpty()) {
        RectF(minByOrNull {
            it.x
        }!!.x,
            minByOrNull {
                it.y
            }!!.y,
            maxByOrNull {
                it.x
            }!!.x,
            maxByOrNull {
                it.y
            }!!.y
        )
    } else {
        RectF()
    }

fun List<PointF>.getOutRect(): Rect = getOutRectF().toRect()


fun PointF.getRectAround(width: Float, height:Float): Rect =
    Rect((x - width/2f).toInt(),
        (y - height/2f).toInt(),
        (x + width/2f).toInt(),
        (y + height/2f).toInt())

fun PointF.getSquareRectAround(size: Float): Rect =
    Rect((x - size/2f).toInt(),
        (y - size/2f).toInt(),
        (x + size/2f).toInt(),
        (y + size/2f).toInt())

fun List<PointF>.toPointArray(): FloatArray =
    FloatArray(this.size * 2).also {
        this.forEachIndexed { index, point ->
            if (index == 0) {
                it[index] = point.x
                it[index + 1] = point.y
            } else {
                it[index * 2] = point.x
                it[index * 2 + 1] = point.y
            }
        }
    }

fun FloatArray.toPointFList(): List<PointF> =
    ArrayList<PointF>().also {
        for (index in 0 until this.lastIndex step 2) {
            it.add(PointF(this[index], this[index + 1]))
        }
    }.toList()