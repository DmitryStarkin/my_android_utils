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
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Rect
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Created by Dmitry Starkin on 18.02.2024 11:53.
 */

const val  MIN_OUTLINE_IN_PIXEL = 2f
const val  ONE_PIXEL = 1f

fun Bitmap.getMaskDirectly(maskColor: ColorContainer, needCopy: Boolean = true): Bitmap =
    if(needCopy){copy(config, true)}else{toMutable()}
        .applyTransformations {
            getTransformationsInvoker()
                .replaceAllColors(transparentColor, maskColor)
        }

fun Bitmap.getMaskByMatrix(maskColor: ColorContainer, needCopy: Boolean = true): Bitmap =
    if(needCopy){copy(config, true)}else{toMutable()}.applyColorMatrix(getFillAllByMonoColorTransExcludeMatrix(maskColor))

fun Bitmap.getMask(maskColor: ColorContainer, needCopy: Boolean = true): Bitmap =
    if(needCopy){copy(config, true)}else{toMutable()}.let {
        Canvas(it).also { canvas->
            canvas.drawColor(maskColor.color, PorterDuff.Mode.SRC_IN)
        }
        it
    }

fun Bitmap.duoTone(firstColor: ColorContainer, secondColor: ColorContainer): Bitmap =
    applyTransformations {
        getTransformationsInvoker()
            .addBitmapTransformation { applyColorMatrix(grayShadowColorMatrix2) }
            .applyLightingColorFilter(firstColor, secondColor)
    }

fun Bitmap.bw(): Bitmap = toMutable().applyColorMatrix(grayShadowColorMatrix2)

/**
 * @return if performed return new Bitmap
 */
fun Bitmap.outlineOverBounds(radius: Float, outlineColor: ColorContainer, step: Int = 1, onlyMask: Boolean = false): Bitmap {
    val param  = getOverBoundsOutlineParams(radius)
    if(param.isOutlineNotVisible){
        return this
    }
    val monoColorBitmap = getMask(outlineColor)
    val outlinedBitmap = Bitmap.createBitmap(param.scaledWidth.toInt(), param.scaledHeight.toInt(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outlinedBitmap)
    canvas.drawColor(Color.TRANSPARENT)
    for(i in 0 until 360 step step){
        val deg = Math.toRadians(i.toDouble())
        canvas.drawBitmap(monoColorBitmap, (param.dX + sin(deg) * param.thickness).toFloat(), (param.dY + cos(deg) * param.thickness).toFloat(), null)
    }
    if(!onlyMask){
        canvas.drawBitmap(this, param.dX , param.dY, null)
    }
    monoColorBitmap.recycle()
    this.recycle()
    return outlinedBitmap
}

/**
 * @return the same Bitmap
 */
fun Bitmap.outlineInBounds(radius: Float, outlineColor: ColorContainer, step: Int = 1, onlyMask: Boolean = false): Bitmap {
    val param  = getInBoundsOutlineParams(radius)
    if(param.isOutlineNotVisible){
        return this
    }
    val scaledBitmap = Bitmap.createScaledBitmap(this, param.scaledWidth.toInt(), param.scaledHeight.toInt(), false)
    val monoColorBitmap = scaledBitmap.getMask(outlineColor)
    this.reconfigure(param.visibleRect.width(), param.visibleRect.height(), this.config)
    val dX = param.dX - param.visibleRect.left
    val dY = param.dY - param.visibleRect.top
    val canvas = Canvas(this)
    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    for(i in 0 until 360 step step){
        val deg = Math.toRadians(i.toDouble())
        canvas.drawBitmap(monoColorBitmap, (dX  + sin(deg) * param.thickness).toFloat(), (dY + cos(deg) * param.thickness).toFloat(), null)
    }
    if(!onlyMask){
        canvas.drawBitmap(scaledBitmap, dX, dY, null)
    }
    scaledBitmap.recycle()
    monoColorBitmap.recycle()
    return this
}

//TODO this is not tested
/**
 * bitmap not recycle
 * mask not recycle
 * @return new Bitmap or null if impossible
 */
fun Bitmap.fastOutline(radius: Float, mask: Bitmap): Bitmap? {
    val param  = getOverBoundsOutlineParamsByDiagonal(radius)
    if(param.isOutlineNotVisible){
        return null
    }
    return setOverMask(mask.copy(mask.config,true).rescaleMask(param), param)
}

//TODO this is not tested
private fun Bitmap.rescaleMask(param: OutlineParams): Bitmap {
    val newMask = Bitmap.createScaledBitmap(this, param.scaledWidth.toInt(), param.scaledHeight.toInt(), false)
    this.recycle()
    return newMask
}

//TODO this is slovest then outlineOverBounds
/**
 * @return new Bitmap
 */
fun Bitmap.outlineOverBoundsAlt(radius: Float, outlineColor: ColorContainer, step: Int = 1): Bitmap {
    val param  = getOverBoundsOutlineParamsByDiagonal(radius)
    if(param.isOutlineNotVisible){
        return this
    }
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = outlineColor.color
        style = Paint.Style.STROKE
        color = outlineColor.color
        strokeWidth = param.thickness * 2
        pathEffect = CornerPathEffect(param.thickness)
    }
    val outlinedBitmap = Bitmap.createBitmap(param.scaledWidth.toInt(), param.scaledHeight.toInt(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outlinedBitmap)
    for(y in 0 until height){
        for(x in 0 until width step step){
            if(needDrawPoint(x, y)){
                canvas.drawPoint(x.toFloat() + param.dX, y.toFloat() + param.dY, paint);
            }
        }
    }
    canvas.drawBitmap(this, param.dX , param.dY, null)
    this.recycle()
    return outlinedBitmap
}

fun Bitmap.setOverMask(mask: Bitmap, param: OutlineParams): Bitmap {
    val canvas = Canvas(mask)
    canvas.drawBitmap(this, param.dX , param.dY, null)
    return mask
}

data class OutlineParams(
    val thickness: Float,
    val scaledWidth: Float,
    val scaledHeight: Float,
    val dX: Float,
    val dY: Float,
    val visibleRect: Rect
){
    val isOutlineNotVisible get() = thickness <= ONE_PIXEL  || scaledWidth < ONE_PIXEL  || scaledHeight < ONE_PIXEL
}

fun Bitmap.getOutlineBoundsByDiagonal(minVisibleArea: Float = 0.5f): Pair<Float, Float> {
    val diagonal = sqrt((width.toFloat()).pow(2) + (height.toFloat()).pow(2))
    return Pair(1f/(diagonal/100f),
        min((width.toFloat() - (width.toFloat() * minVisibleArea))/2,
            (height.toFloat() - (height.toFloat() * minVisibleArea))/2) /(diagonal/100f))
}

private fun Bitmap.getOverBoundsOutlineParamsByDiagonal(thinPresent: Float): OutlineParams {
    val thin = sqrt((width.toFloat()).pow(2) + (height.toFloat()).pow(2)) * thinPresent
    val xScaleFactor = (width + (2 * thin))/width
    val yScaleFactor = (height + (2 * thin))/height
    val scaledW = width.toFloat() * xScaleFactor
    val scaledH = height.toFloat() * yScaleFactor
    return OutlineParams(
        thin,
        scaledW,
        scaledH,
        (scaledW - width)/2,
        (scaledH - height)/2,
        Rect(0,0, scaledW.toInt(), scaledH.toInt())
    )
}

private fun Bitmap.getInBoundsOutlineParamsByDiagonal(thinPresent: Float): OutlineParams {
    val thin = sqrt((width.toFloat()).pow(2) + (height.toFloat()).pow(2)) * thinPresent
    val xScaleFactor = (width - (2 * thin))/width
    val yScaleFactor = (height - (2 * thin))/height
    val scaledW = width.toFloat() * xScaleFactor
    val scaledH = height.toFloat() * yScaleFactor
    val rectDx = ((width.toFloat() - (scaledW + (thin*2)))/2).toInt()
    val rectDy = ((height.toFloat() - (scaledH + (thin*2)))/2).toInt()
    return OutlineParams(
        thin,
        scaledW,
        scaledH,
        (width - scaledW)/2,
        (height - scaledH)/2,
        Rect(rectDx, rectDy, rectDx + (scaledW + (thin*2)).toInt(),
            rectDy + (scaledH + (thin*2)).toInt())
    )
}

private fun Bitmap.getOverBoundsOutlineParams(thinPresent: Float): OutlineParams {
    val thin = min(width.toFloat(), height.toFloat()) * thinPresent
    val xScaleFactor = (width + (2 * thin))/width
    val yScaleFactor = (height + (2 * thin))/height
    val scaledW = width.toFloat() * xScaleFactor
    val scaledH = height.toFloat() * yScaleFactor
    return OutlineParams(
        thin,
        scaledW,
        scaledH,
        (scaledW - width)/2,
        (scaledH - height)/2,
        Rect(0,0, scaledW.toInt(), scaledH.toInt())
    )
}

private fun Bitmap.getInBoundsOutlineParams(thinPresent: Float): OutlineParams {
    val min =  min(width.toFloat(), height.toFloat())
    val thin = min  * thinPresent
    val scaleFactor = (min - (2 * thin))/min
    val scaledW = width.toFloat() * scaleFactor
    val scaledH = height.toFloat() * scaleFactor
    val rectDx = ((width.toFloat() - (scaledW + (thin*2)))/2).toInt()
    val rectDy = ((height.toFloat() - (scaledH + (thin*2)))/2).toInt()
    return OutlineParams(
        thin,
        scaledW,
        scaledH,
        (width - scaledW)/2,
        (height - scaledH)/2,
        Rect(rectDx, rectDy, rectDx + (scaledW + (thin*2)).toInt(),
            rectDy + (scaledH + (thin*2)).toInt())
    )
}

fun Bitmap.getOutlineBounds(minVisibleArea: Float = 0.5f): Pair<Float, Float> {
    val side = min(width.toFloat(), height.toFloat())
    return Pair((MIN_OUTLINE_IN_PIXEL/(side/100f))/100f , (min((width.toFloat() - (width.toFloat() * minVisibleArea))/2,
        (height.toFloat() - (height.toFloat() * minVisibleArea))/2) /(side/100f))/100f )
}

fun Bitmap.thinToDiagonalThinPresent(thin: Float): Float = thin / sqrt((width.toFloat()).pow(2) + (height.toFloat()).pow(2))

private fun Bitmap.needDrawPoint(pX: Int, pY: Int): Boolean{
    if(getPixel(pX, pY) == transparentColor.color){
        return false
    }
    if(pX == 0 || pX == width - 1 || pY == 0 || pY == height - 1){
        return true
    }
    for(y in (pY - 1) .. (pY + 1)){
        for(x in (pX - 1) .. (pX + 1)){
            if((y in 0.. height) && (x in 0..width)){
                if(getPixel(x,y) == transparentColor.color){
                    return true
                }
            }
        }
    }
    return false
}