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

package com.starsoft.myandroidutil.colorutils

import android.graphics.Color
import android.graphics.Color.argb
import androidx.annotation.ColorInt
import com.starsoft.myandroidutil.stringext.EMPTY_STRING
import com.starsoft.myandroidutil.stringext.SPACE
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt


/**
 * Created by Dmitry Starkin on 10.03.2023 12:37.
 */
const val TRANSPARENT_COLOR = 0x00000000
const val HEX_COLOR_PREFIX = "#"
private const val COLOR_PARTS_TEMPLATE = "%2s"
private const val ZERO = "0"
const val MAX_ALPHA = 255
const val MIN_ALPHA = 0
const val DEFAULT_ALPHA = MAX_ALPHA
const val GRAY_MAX_DIFF = 20
private const val LEN_WITHOUT_ALPHA = 7

interface ColorContainer {
    @get:ColorInt
    val color: Int
    val a: Int
        get() = Color.alpha(color)
    val r: Int
        get() = Color.red(color)
    val g: Int
        get() = Color.green(color)
    val b: Int
        get() = Color.blue(color)
}

data class ColorIntContainer(@ColorInt override val color: Int): ColorContainer

data class ColorARGBContainer(
    override val  r: Int,
    override val  g: Int,
    override val  b: Int,
    override val  a: Int = DEFAULT_ALPHA

): ColorContainer{
    override val color: Int  = argb(a, r, g, b)
}

val whiteContainer: ColorContainer by lazy {
    ColorARGBContainer(
        255,
        255,
        255
    )
}

val blackContainer: ColorContainer by lazy {
    ColorARGBContainer(
        0,
        0,
        0
    )
}

interface ColorComparator {
    fun compare(@ColorInt c1: Int, @ColorInt c2: Int): Boolean =
        compare (ColorIntContainer(c1), ColorIntContainer(c2))
    fun compare(c1: ColorContainer, c2: ColorContainer): Boolean
}

val simpleColorComparator : ColorComparator by lazy {
    object : ColorComparator {
        override fun compare(c1: ColorContainer, c2: ColorContainer): Boolean {
            return c1.color == c2.color
        }
    }
}

val shadeColorComparator: ColorComparator by lazy {
    object : ColorComparator {
        override fun compare(c1: ColorContainer, c2: ColorContainer): Boolean {
            return (c1.isBWColor() && c2.isBWColor()) ||
                    (c1.isRedColor() && c2.isRedColor()) ||
                    (c1.isGreenColor() && c2.isGreenColor()) ||
                    (c1.isBlueColor() && c2.isBlueColor())
        }
    }
}

class EuclidComparator(private val delta: Double): ColorComparator{
    override fun compare(c1: ColorContainer, c2: ColorContainer): Boolean {
        val difference= sqrt((c1.r - c2.r).toDouble().pow(2) +
                (c1.g - c2.g).toDouble().pow(2) +
                (c1.b - c2.b).toDouble().pow(2)+
                (c1.a - c2.a).toDouble().pow(2))
        return difference < delta
    }
}

class ABSComparator(private val delta: Int): ColorComparator{
    override fun compare(c1: ColorContainer, c2: ColorContainer): Boolean {
        val difference= abs(c1.r - c2.r) +
                abs(c1.g - c2.g) +
                abs(c1.b - c2.b) +
                abs(c1.a - c2.a)
        return difference < delta
    }
}


fun String?.parseAsHexColor(@ColorInt defValue: Int = TRANSPARENT_COLOR): Int =
    try {
        this?.let {
            if(it.isEmpty()){
                defValue
            }else{
                Color.parseColor(it)
            }
        } ?: defValue

    } catch (e: IllegalArgumentException){
        e.printStackTrace()
        defValue
    }

fun String?.parseAsHexColorToContainer(@ColorInt defValue: Int = TRANSPARENT_COLOR): ColorContainer =
    ColorIntContainer(parseAsHexColor(defValue))

fun ColorContainer.convertToColorHexString(order: ColorStringOrder = ColorStringOrder.Reverse): String =
    convertToColorHexString(color, order)

fun ColorContainer.convertToNoAlphaColorHexString(order: ColorStringOrder = ColorStringOrder.Reverse): String =
    convertToNoAlphaColorHexString(color, order)

fun convertToColorHexString(@ColorInt color: Int, order: ColorStringOrder = ColorStringOrder.Reverse): String =
    if(color == TRANSPARENT_COLOR){
        EMPTY_STRING
    } else {
        val alphaHex: String = String.format(COLOR_PARTS_TEMPLATE, Integer.toHexString(Color.alpha(color))).replace(
            SPACE, ZERO)
        val blueHex: String = String.format(COLOR_PARTS_TEMPLATE, Integer.toHexString(Color.blue(color))).replace(
            SPACE, ZERO)
        val greenHex: String = String.format(COLOR_PARTS_TEMPLATE, Integer.toHexString(Color.green(color))).replace(
            SPACE, ZERO)
        val redHex: String = String.format(COLOR_PARTS_TEMPLATE, Integer.toHexString(Color.red(color))).replace(
            SPACE, ZERO)
        when(order ) {
            ColorStringOrder.Direct -> "$HEX_COLOR_PREFIX$alphaHex$redHex$greenHex$blueHex"

            ColorStringOrder.Reverse ->"$HEX_COLOR_PREFIX$redHex$greenHex$blueHex$alphaHex"

            ColorStringOrder.Web -> "$HEX_COLOR_PREFIX$blueHex$alphaHex$redHex$greenHex"
        }
    }

fun convertToNoAlphaColorHexString(@ColorInt color: Int, order: ColorStringOrder = ColorStringOrder.Reverse): String =
    if(color == TRANSPARENT_COLOR){
        EMPTY_STRING
    } else {
        val blueHex: String = String.format(COLOR_PARTS_TEMPLATE, Integer.toHexString(Color.blue(color))).replace(
            SPACE, ZERO)
        val greenHex: String = String.format(COLOR_PARTS_TEMPLATE, Integer.toHexString(Color.green(color))).replace(
            SPACE, ZERO)
        val redHex: String = String.format(COLOR_PARTS_TEMPLATE, Integer.toHexString(Color.red(color))).replace(
            SPACE, ZERO)
        when(order ) {
            ColorStringOrder.Direct -> "$HEX_COLOR_PREFIX$redHex$greenHex$blueHex"

            ColorStringOrder.Reverse ->"$HEX_COLOR_PREFIX$redHex$greenHex$blueHex"

            ColorStringOrder.Web -> "$HEX_COLOR_PREFIX$blueHex$redHex$greenHex"
        }
    }

fun String?.parseAsWebHexColorToContainer(@ColorInt defValue: Int = TRANSPARENT_COLOR): ColorContainer =
    ColorIntContainer(parseAsWebHexColor(defValue))


fun String?.parseAsWebHexColor(@ColorInt defValue: Int = TRANSPARENT_COLOR): Int =
    try {
        this?.let {
            if(it.isEmpty()){
                defValue
            } else {
                if(it.length == LEN_WITHOUT_ALPHA){
                    Color.parseColor(it)
                }else{
                    val color = Color.parseColor(it)
                    Color.parseColor(convertToColorHexString(color, ColorStringOrder.Web))
                }
            }
        } ?: defValue

    } catch (e: IllegalArgumentException){
        e.printStackTrace()
        defValue
    }

fun getColorAlpha(@ColorInt color: Int): Int = Color.alpha(color)

@ColorInt
fun setColorAlpha(@ColorInt color: Int, alpha: Int): Int =
    if (alpha < MIN_ALPHA || alpha > MAX_ALPHA ) {
        color
    } else {
        Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
    }

@ColorInt
fun getBaseColor(@ColorInt color: Int): Int = Color.argb(MAX_ALPHA, Color.red(color), Color.green(color), Color.blue(color))

@ColorInt
fun ColorContainer.getBaseColor(): Int = getBaseColor(color)

fun ColorContainer.isBWColor(): Boolean =
    (abs(r - g) <= GRAY_MAX_DIFF) && (abs(g - b) <= GRAY_MAX_DIFF) && (abs(b - r) <= GRAY_MAX_DIFF)

fun ColorContainer.isRedColor(): Boolean = (r >= g && r >= b)

fun ColorContainer.isGreenColor(): Boolean = (g > r && g >= b)

fun ColorContainer.isBlueColor(): Boolean = (b > r && b > g)

fun Iterable<ColorContainer>.getBWColors(): List<ColorContainer> =
    mapNotNull {
        if (it.isBWColor()) {
            it
        } else {
            null
        }
    }

fun Iterable<ColorContainer>.getRedColors(): List<ColorContainer> =
    mapNotNull {
        if (it.isRedColor()) {
            it
        } else {
            null
        }
    }

fun Iterable<ColorContainer>.getGreenColors(): List<ColorContainer> =
    mapNotNull {
        if (it.isGreenColor()) {
            it
        } else {
            null
        }
    }

fun Iterable<ColorContainer>.getBlueColors(): List<ColorContainer> =
    mapNotNull {
        if (it.isBlueColor()) {
            it
        } else {
            null
        }
    }

fun Iterable<ColorContainer>.findBestMatchFor(color: ColorContainer): ColorContainer {
    var currentDifference = 3 * 255
    var result: ColorContainer = whiteContainer
    forEach {
        val difference: Int = abs(color.r - it.r) + abs(color.g - it.g) + abs(color.b - it.b)
        if (currentDifference > difference) {
            currentDifference = difference
            result = it
        }
    }
    return result
}

enum class ColorStringOrder{
    Direct,
    Reverse,
    Web
}