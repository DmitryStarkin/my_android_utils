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

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Color.argb
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import com.starsoft.myandroidutil.collectionUtils.groupByDescending
import com.starsoft.myandroidutil.stringext.EMPTY_STRING
import com.starsoft.myandroidutil.stringext.SPACE
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Created by Dmitry Starkin on 10.03.2023 12:37.
 */
private const val COLOR_PARTS_TEMPLATE = "%2s"
private const val ZERO = "0"
private const val LEN_WITHOUT_ALPHA = 7
const val TRANSPARENT_COLOR = 0x00000000
const val HEX_COLOR_PREFIX = "#"
const val MAX_COLOR_COMPONENT_VALUE = 255
const val MIN_COLOR_COMPONENT_VALUE = 0
const val DEFAULT_ALPHA = MAX_COLOR_COMPONENT_VALUE
const val DEFAULT_GRAY_DIFF = 20
const val DEFAULT_RED_BRIGH_WEIGHT = 0.2126f
const val DEFAULT_GREEN_BRIGH_WEIGHT = 0.7152f
const val DEFAULT_BLUE_BRIGH_WEIGHT = 0.0722f
const val RED_WEIGHT = 30
const val GREEN_WEIGHT = 59
const val BLUE_WEIGHT = 11

sealed interface ColorComponent: ColorContainer {
    @get:IntRange(from = 0, to = 255)
    val value: Int

    data class Red(@IntRange(from = 0, to = 255)
                   override val value: Int,
    ): ColorComponent {
        override val r: Int = value
        override val g: Int = 0
        override val b: Int = 0
        override val a: Int = 0
        override val color: Int = argb(a, r, g, b)
    }

    data class Green(@IntRange(from = 0, to = 255)
                     override val value: Int,
    ): ColorComponent {
        override val r: Int = 0
        override val g: Int = value
        override val b: Int = 0
        override val a: Int = 0
        override val color: Int = argb(a, r, g, b)
    }

    data class Blue(@IntRange(from = 0, to = 255)
                    override val value: Int,
    ): ColorComponent {
        override val r: Int = 0
        override val g: Int = 0
        override val b: Int = value
        override val a: Int = 0
        override val color: Int = argb(a, r, g, b)
    }

    data class Alpha(@IntRange(from = 0, to = 255)
                     override val value: Int,
    ): ColorComponent {
        override val r: Int = 0
        override val g: Int = 0
        override val b: Int = 0
        override val a: Int = value
        override val color: Int = argb(a, r, g, b)
    }
}

interface ColorContainer{
    @get:ColorInt
    val color: Int

    @get:IntRange(from = 0, to = 255)
    val a: Int
        get() = Color.alpha(color)

    @get:IntRange(from = 0, to = 255)
    val r: Int
        get() = Color.red(color)

    @get:IntRange(from = 0, to = 255)
    val g: Int
        get() = Color.green(color)

    @get:IntRange(from = 0, to = 255)
    val b: Int
        get() = Color.blue(color)

    val argbColorString: String
        get() = convertToColorHexString(color, ColorStringOrder.AARRGGBB)

    val rgbaColorString: String
        get() = convertToColorHexString(color, ColorStringOrder.RRGGBBAA)

    val rgbColorString: String
        get() = convertToNoAlphaColorHexString(color)

    operator fun minus(other: ColorContainer): ColorContainer =
        if(this is MutableColorContainer){
            color  = argb(a, abs(r - other.r), abs(g - other.g), abs(b - other.b))
            this
        } else {
            ColorComponentsContainer(
                abs(r - other.r),
                abs(g - other.g),
                abs(b - other.b),
                a
            )
        }

    operator fun plus(other: ColorContainer): ColorContainer =
        if(this is MutableColorContainer){
            color  = argb(a,
                Integer.min(r + other.r, 255),
                Integer.min(g + other.g, 255),
                Integer.min(b + other.b, 255)
            )
            this
        } else {
            ColorComponentsContainer(
                Integer.min(r + other.r, 255),
                Integer.min(g + other.g, 255),
                Integer.min(b + other.b, 255),
                a
            )
        }

    operator fun unaryMinus(): ColorContainer =
        this - whiteColor

}

@SuppressLint("SupportAnnotationUsage")
interface MutableColorContainer: ColorContainer {
    @get:ColorInt
    @set:ColorInt
    override var color: Int
}

fun ColorContainer.componentsString(): String =
    "a=$a r=$r g=$g b=$b"

fun ColorContainer.fullARGBInfoString(): String =
    "$argbColorString int=$color ${componentsString()}"

fun ColorContainer.fullRGBInfoString(): String =
    "$rgbColorString int=$color ${componentsString()}"

data class ColorIntContainer(@ColorInt override val color: Int) : ColorContainer

class  MutableColor: MutableColorContainer {
    @ColorInt
    override var color : Int = Color.WHITE
}

data class NoAlphaColorStringContainer(
    override val rgbColorString: String,
    @ColorInt val defValue: Int = TRANSPARENT_COLOR
) : ColorContainer {
    override val color: Int = rgbColorString.parseAsARGBHexColor(defValue)
}

data class ARGBColorStringContainer(
    override val argbColorString: String,
    @ColorInt val defValue: Int = TRANSPARENT_COLOR
) : ColorContainer {
    override val color: Int = argbColorString.parseAsARGBHexColor(defValue)
}

data class RGBAColorStringContainer(
    override val argbColorString: String,
    @ColorInt val defValue: Int = TRANSPARENT_COLOR
) : ColorContainer {
    override val color: Int = argbColorString.parseAsRGBAHexColor(defValue)
}

data class ColorComponentsContainer(
    @IntRange(from = 0, to = 255)
    override val r: Int,
    @IntRange(from = 0, to = 255)
    override val g: Int,
    @IntRange(from = 0, to = 255)
    override val b: Int,
    @IntRange(from = 0, to = 255)
    override val a: Int = DEFAULT_ALPHA

) : ColorContainer {
    override val color: Int = argb(a, r, g, b)
}

interface ColorComparator {
    fun compare(@ColorInt c1: Int, @ColorInt c2: Int): Boolean =
        compare(ColorIntContainer(c1), ColorIntContainer(c2))

    fun compare(c1: ColorContainer, c2: ColorContainer): Boolean
}

val simpleColorComparator: ColorComparator by lazy {
    object : ColorComparator {
        override fun compare(c1: ColorContainer, c2: ColorContainer): Boolean {
            return c1.color == c2.color
        }
    }
}

val shadeColorComparator: ColorComparator by lazy {
    ShadeComparator()
}

class ShadeComparator(private val selector: ShadowSelector = defShadowSelector) : ColorComparator {
    override fun compare(c1: ColorContainer, c2: ColorContainer): Boolean =
        (c1.isBWColor(selector) && c2.isBWColor(selector)) ||
                (c1.isRedColor(selector) && c2.isRedColor(selector)) ||
                (c1.isGreenColor(selector) && c2.isGreenColor(selector)) ||
                (c1.isBlueColor(selector) && c2.isBlueColor(selector))

}

class EuclidComparator(private val delta: Double) : ColorComparator {
    override fun compare(c1: ColorContainer, c2: ColorContainer): Boolean =
        (sqrt((c1.r - c2.r).toDouble().pow(2)) < delta) &&
                (sqrt((c1.g - c2.g).toDouble().pow(2)) < delta) &&
                (sqrt((c1.b - c2.b).toDouble().pow(2)) < delta) &&
                (sqrt((c1.a - c2.a).toDouble().pow(2)) < delta)

}

class ABSComparator(private val delta: Int) : ColorComparator {
    override fun compare(c1: ColorContainer, c2: ColorContainer): Boolean =
        (abs(c1.r - c2.r) < delta) && (abs(c1.g - c2.g) < delta) && (abs(c1.b - c2.b) < delta) && (abs(
            c1.a - c2.a
        ) < delta)
}

class ShadePreComparator(
    private val secondComparator: ColorComparator,
    selector: ShadowSelector = defShadowSelector
) : ColorComparator {
    private val shadeColorComparator = ShadeComparator(selector)
    override fun compare(c1: ColorContainer, c2: ColorContainer): Boolean =
        if (shadeColorComparator.compare(c1, c2)) {
            secondComparator.compare(c1, c2)
        } else {
            false
        }
}

fun String?.parseAsHexColorToContainer(@ColorInt defValue: Int = TRANSPARENT_COLOR): ColorContainer =
    ColorIntContainer(parseAsHexColor(defValue))

fun ColorContainer.convertToColorHexString(order: ColorStringOrder = ColorStringOrder.AARRGGBB): String =
    convertToColorHexString(color, order)

fun ColorContainer.convertToNoAlphaColorHexString(order: ColorStringOrder = ColorStringOrder.AARRGGBB): String =
    convertToNoAlphaColorHexString(color, order)

fun convertToColorHexString(
    @ColorInt color: Int,
    order: ColorStringOrder = ColorStringOrder.AARRGGBB
): String =
    if (color == TRANSPARENT_COLOR) {
        EMPTY_STRING
    } else {
        val alphaHex: String =
            String.format(COLOR_PARTS_TEMPLATE, Integer.toHexString(Color.alpha(color))).replace(
                SPACE, ZERO
            )
        val blueHex: String =
            String.format(COLOR_PARTS_TEMPLATE, Integer.toHexString(Color.blue(color))).replace(
                SPACE, ZERO
            )
        val greenHex: String =
            String.format(COLOR_PARTS_TEMPLATE, Integer.toHexString(Color.green(color))).replace(
                SPACE, ZERO
            )
        val redHex: String =
            String.format(COLOR_PARTS_TEMPLATE, Integer.toHexString(Color.red(color))).replace(
                SPACE, ZERO
            )
        when (order) {
            ColorStringOrder.AARRGGBB -> "$HEX_COLOR_PREFIX$alphaHex$redHex$greenHex$blueHex"

            ColorStringOrder.RRGGBBAA -> "$HEX_COLOR_PREFIX$redHex$greenHex$blueHex$alphaHex"

            ColorStringOrder.BBAARRGG -> "$HEX_COLOR_PREFIX$blueHex$alphaHex$redHex$greenHex"
        }
    }

fun convertToNoAlphaColorHexString(
    @ColorInt color: Int,
    order: ColorStringOrder = ColorStringOrder.AARRGGBB
): String =
    if (color == TRANSPARENT_COLOR) {
        EMPTY_STRING
    } else {
        val blueHex: String =
            String.format(COLOR_PARTS_TEMPLATE, Integer.toHexString(Color.blue(color))).replace(
                SPACE, ZERO
            )
        val greenHex: String =
            String.format(COLOR_PARTS_TEMPLATE, Integer.toHexString(Color.green(color))).replace(
                SPACE, ZERO
            )
        val redHex: String =
            String.format(COLOR_PARTS_TEMPLATE, Integer.toHexString(Color.red(color))).replace(
                SPACE, ZERO
            )
        when (order) {
            ColorStringOrder.AARRGGBB -> "$HEX_COLOR_PREFIX$redHex$greenHex$blueHex"

            ColorStringOrder.RRGGBBAA -> "$HEX_COLOR_PREFIX$redHex$greenHex$blueHex"

            ColorStringOrder.BBAARRGG -> "$HEX_COLOR_PREFIX$blueHex$redHex$greenHex"
        }
    }

fun String?.parseAsBARGHexColorToContainer(@ColorInt defValue: Int = TRANSPARENT_COLOR): ColorContainer =
    ColorIntContainer(parseAsBARGHexColor(defValue))

fun String?.parseAsARGBHexColorToContainer(@ColorInt defValue: Int = TRANSPARENT_COLOR): ColorContainer =
    ColorIntContainer(parseAsARGBHexColor(defValue))

fun String?.parseAsRGBAHexColorToContainer(@ColorInt defValue: Int = TRANSPARENT_COLOR): ColorContainer =
    ColorIntContainer(parseAsRGBAHexColor(defValue))

fun String?.parseAsBARGHexColor(@ColorInt defValue: Int = TRANSPARENT_COLOR): Int =
    parseAsHexColor(defValue, ColorStringOrder.BBAARRGG)

fun String?.parseAsARGBHexColor(@ColorInt defValue: Int = TRANSPARENT_COLOR): Int =
    parseAsHexColor(defValue, ColorStringOrder.AARRGGBB)

fun String?.parseAsRGBAHexColor(@ColorInt defValue: Int = TRANSPARENT_COLOR): Int =
    parseAsHexColor(defValue, ColorStringOrder.RRGGBBAA)

fun String?.parseAsHexColor(
    @ColorInt defValue: Int = TRANSPARENT_COLOR,
    order: ColorStringOrder = ColorStringOrder.AARRGGBB
): Int =
    try {
        this?.let {
            if (it.isEmpty()) {
                defValue
            } else {
                if (it.length == LEN_WITHOUT_ALPHA || order == ColorStringOrder.AARRGGBB) {
                    Color.parseColor(it)
                } else {
                    val color = Color.parseColor(it)
                    Color.parseColor(convertToColorHexString(color, order))
                }
            }
        } ?: defValue

    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        defValue
    }

@IntRange(from = 0, to = 255)
fun getColorAlpha(@ColorInt color: Int): Int = Color.alpha(color)

@IntRange(from = 0, to = 255)
fun ColorContainer.getColorAlpha(): Int = getColorAlpha(color)

fun isColorTransparent(@ColorInt color: Int): Boolean = getColorAlpha(color) == 0

fun ColorContainer.isTransparent(): Boolean = isColorTransparent(color)

@ColorInt
fun setColorAlpha(@ColorInt color: Int, alpha: Int): Int =
    if (alpha < MIN_COLOR_COMPONENT_VALUE || alpha > MAX_COLOR_COMPONENT_VALUE) {
        color
    } else {
        Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
    }

@ColorInt
fun getBaseColor(@ColorInt color: Int): Int =
    Color.argb(MAX_COLOR_COMPONENT_VALUE, Color.red(color), Color.green(color), Color.blue(color))

@ColorInt
fun ColorContainer.getBaseColor(): Int = getBaseColor(color)

interface ShadowSet {
    val bwColors: Iterable<ColorContainer>
    val redColors: Iterable<ColorContainer>
    val greenColors: Iterable<ColorContainer>
    val blueColors: Iterable<ColorContainer>
    val mSelector: ShadowSelector
}

interface ShadowSelector {
    val isBWColor: (ColorContainer) -> Boolean
    val isRedColor: (ColorContainer) -> Boolean
    val isGreenColor: (ColorContainer) -> Boolean
    val isBlueColor: (ColorContainer) -> Boolean
}

class DefaultShadowSelector(@IntRange(from = 0, to = 40) val grayDiff: Int = DEFAULT_GRAY_DIFF) :
    ShadowSelector {
    override val isBWColor: (ColorContainer) -> Boolean
        get() = {
            (abs(it.r - it.g) <= grayDiff) && (abs(it.g - it.b) <= grayDiff) && (abs(it.b - it.r) <= grayDiff)
        }
    override val isRedColor: (ColorContainer) -> Boolean
        get() = {
            !isBWColor(it) && (it.r >= it.g && it.r >= it.b)
        }
    override val isGreenColor: (ColorContainer) -> Boolean
        get() = {
            !isBWColor(it) && (it.g > it.r && it.g >= it.b)
        }
    override val isBlueColor: (ColorContainer) -> Boolean
        get() = {
            !isBWColor(it) && (it.b > it.r && it.b >= it.g)
        }
}

class WeightShadowSelector(@IntRange(from = 0, to = 40) val grayDiff: Int = DEFAULT_GRAY_DIFF) :
    ShadowSelector {

    override val isBWColor: (ColorContainer) -> Boolean
        get() = {
            (abs(it.r - it.g) <= grayDiff) && (abs(it.g - it.b) <= grayDiff) && (abs(it.b - it.r) <= grayDiff)
        }

    override val isRedColor: (ColorContainer) -> Boolean
        get() = {
            val diff = absWeightColorDiffer.differenceBetween(it, redColor)
            !isBWColor(it) && (diff <= absWeightColorDiffer.differenceBetween(it, greenColor)) &&
                    (diff <= absWeightColorDiffer.differenceBetween(it, blueColor))
        }

    override val isGreenColor: (ColorContainer) -> Boolean
        get() = {
            val diff = absWeightColorDiffer.differenceBetween(it, greenColor)
            !isBWColor(it) && (diff < absWeightColorDiffer.differenceBetween(it, redColor)) &&
                    (diff <= absWeightColorDiffer.differenceBetween(it, blueColor))
        }

    override val isBlueColor: (ColorContainer) -> Boolean
        get() = {
            val diff = absWeightColorDiffer.differenceBetween(it, blueColor)
            !isBWColor(it) && (diff < absWeightColorDiffer.differenceBetween(it, redColor)) &&
                    (diff < absWeightColorDiffer.differenceBetween(it, greenColor))
        }
}

val defWeightShadowSelector: ShadowSelector by lazy {
    WeightShadowSelector(DEFAULT_GRAY_DIFF)
}

val defShadowSelector: ShadowSelector by lazy {
    DefaultShadowSelector(DEFAULT_GRAY_DIFF)
}

fun Iterable<ColorContainer>.toShadowSet(selector: ShadowSelector = defShadowSelector): ShadowSet =
    object : ShadowSet {
        override val bwColors: Iterable<ColorContainer> = getBWColors(selector)
        override val redColors: Iterable<ColorContainer> = getRedColors(selector)
        override val greenColors: Iterable<ColorContainer> = getGreenColors(selector)
        override val blueColors: Iterable<ColorContainer> = getBlueColors(selector)
        override val mSelector: ShadowSelector = selector
    }

fun ColorContainer.isBWColor(selector: ShadowSelector = defShadowSelector): Boolean =
    selector.isBWColor(this)

fun ColorContainer.isRedColor(selector: ShadowSelector = defShadowSelector): Boolean =
    selector.isRedColor(this)

fun ColorContainer.isGreenColor(selector: ShadowSelector = defShadowSelector): Boolean =
    selector.isGreenColor(this)

fun ColorContainer.isBlueColor(selector: ShadowSelector = defShadowSelector): Boolean =
    selector.isBlueColor(this)

fun Iterable<ColorContainer>.getBWColors(selector: ShadowSelector = defShadowSelector): List<ColorContainer> =
    mapNotNull {
        if (it.isBWColor(selector)) {
            it
        } else {
            null
        }
    }

fun Iterable<ColorContainer>.getRedColors(selector: ShadowSelector = defShadowSelector): List<ColorContainer> =
    mapNotNull {
        if (it.isRedColor(selector)) {
            it
        } else {
            null
        }
    }

fun Iterable<ColorContainer>.getGreenColors(selector: ShadowSelector = defShadowSelector): List<ColorContainer> =
    mapNotNull {
        if (it.isGreenColor(selector)) {
            it
        } else {
            null
        }
    }

fun Iterable<ColorContainer>.getBlueColors(selector: ShadowSelector = defShadowSelector): List<ColorContainer> =
    mapNotNull {
        if (it.isBlueColor(selector)) {
            it
        } else {
            null
        }
    }

fun Iterable<ColorContainer>.findBestMatchFor(color: ColorContainer): ColorContainer =
    findBestMatchFor(color, absColorDiffer)

fun Iterable<ColorContainer>.findBestMatchForOrNull(color: ColorContainer): ColorContainer? =
    findBestMatchForOrNull(color, absColorDiffer)

fun Iterable<ColorContainer>.findBestMatchFor(
    color: ColorContainer,
    differ: ColorDiffer
): ColorContainer {
    if (!iterator().hasNext()) {
        throw Exception("empty collections not supported")
    }
    var currentDifference = differ.maxDiff
    var result: ColorContainer = iterator().next()
    forEach {
        val difference = differ.differenceBetween(it, color)
        if (currentDifference >= difference) {
            currentDifference = difference
            result = it
        }
    }
    return result
}

fun Iterable<ColorContainer>.findBestMatchForAltImpl(
    color: ColorContainer,
    differ: ColorDiffer
): ColorContainer {
    if (!iterator().hasNext()) {
        throw Exception("empty collections not supported")
    }
    val new = sortedByDescending {
        differ.differenceBetween(it, transparentColor)
    }
    val dif = differ.differenceBetween(color, transparentColor)
    return new.find {
        differ.differenceBetween(it, transparentColor) <= dif
    } ?: new[new.lastIndex]
}

fun PreparedPalette.findBestMatchFor(color: ColorContainer): ColorContainer {
    if(difColors.isEmpty()) return color
    val dif = differ.differenceBetween(color, differColor)
    return difColors.find {
        it.diff <= dif
    } ?: difColors[difColors.lastIndex]
}

fun PreparedPalette.findBestMatchForWithCompare(color: ColorContainer, detailComparator: ColorComparator = ShadeComparator(
    defShadowSelector
)
): ColorContainer {
    if(difColors.isEmpty()) return color
    val dif = differ.differenceBetween(color, differColor)
    return difColors.find {
        it.diff <= dif && detailComparator.compare(color, it)
    } ?: difColors[difColors.lastIndex]
}

fun GroupedPreparedPalette.findBestMatchFor(color: ColorContainer, detailDiffer: ColorDiffer? = absWeightColorDiffer): ColorContainer {
    if(difColors.isEmpty()) return color
    val dif = differ.differenceBetween(color, differColor)
    return difColors.find {
        it[0].diff <= dif
    }?.let{
        if(it.size == 1){
            it[0]
        } else {
            it.findBestMatchFor(color, detailDiffer ?: differ)
        }
    }   ?: difColors[difColors.lastIndex][0]
}

fun GroupedPreparedPalette.improvedFindBestMatchFor(color: ColorContainer, detailDiffer: ColorDiffer? = absWeightColorDiffer): ColorContainer {
    if(difColors.isEmpty()) return color
    val dif = differ.differenceBetween(color, differColor)
    fun List<PreparedColor>.getMatched(): ColorContainer =
        if(size == 1){
            this[0]
        } else {
            findBestMatchFor(color, detailDiffer ?: differ)
        }
    var first = difColors[0]
    var second = difColors[difColors.lastIndex]
    for(i in difColors.indices){
        if(difColors[i][0].diff >= dif){
            first = difColors[i]
            if(i + 1 in difColors.indices){
                second =  difColors[i + 1]
            }
        } else {
            break
        }
    }
    return if(first[0].diff - dif < dif - second[0].diff){
        first.getMatched()
    } else {
        second.getMatched()
    }
}

interface PreparedColor: ColorContainer {
    val diff: Float
}

interface PreparedPalette {
    val difColors: List<PreparedColor>
    val differ: ColorDiffer
    val differColor: ColorContainer
}

interface GroupedPreparedPalette {
    val difColors: List<List<PreparedColor>>
    val differ: ColorDiffer
    val differColor: ColorContainer
}

data class PreparedColorImpl(
    val pColor: ColorContainer,
    override val diff: Float
) : PreparedColor, ColorContainer by pColor

data class PreparedPaletteImpl(
    val palette: Iterable<ColorContainer>,
    override val differ: ColorDiffer,
    override val differColor: ColorContainer = transparentColor
) : PreparedPalette {
    override val difColors: List<PreparedColor> =
        palette.map {
            PreparedColorImpl(it, differ.differenceBetween(it, differColor))
        }.sortedByDescending {
            it.diff
        }
}

data class GroupedPreparedPaletteImpl(
    val palette: Iterable<ColorContainer>,
    override val differ: ColorDiffer,
    override val differColor: ColorContainer = transparentColor
) : GroupedPreparedPalette {
    override val difColors: List<List<PreparedColor>> =
        palette.map {
            PreparedColorImpl(it, differ.differenceBetween(it, differColor))
        }.groupByDescending {
            it.diff
        }
}

fun Iterable<ColorContainer>.getGroupedPalette(
    differ: ColorDiffer,
    diffColor: ColorContainer = transparentColor
): GroupedPreparedPalette {
    if (!iterator().hasNext()) {
        throw Exception("empty collections not supported")
    }
    return GroupedPreparedPaletteImpl(this, differ, diffColor)
}

fun Iterable<ColorContainer>.getPreparedPalette(): PreparedPalette =
    getPreparedPalette(absColorDiffer)

fun Iterable<ColorContainer>.getPreparedPalette(
    differ: ColorDiffer,
    diffColor: ColorContainer = transparentColor
): PreparedPalette {
    if (!iterator().hasNext()) {
        throw Exception("empty collections not supported")
    }
    return PreparedPaletteImpl(this, differ, diffColor)
}

fun Iterable<ColorContainer>.findBestMatchForOrNull(
    color: ColorContainer,
    differ: ColorDiffer,
    selector: ShadowSelector = defShadowSelector
): ColorContainer? {
    if (!iterator().hasNext()) {
        throw Exception("empty collections not supported")
    }

    fun findOrNull(colors: List<ColorContainer>): ColorContainer? =
        colors.let {
            if (it.isEmpty()) {
                null
            } else {
                it.findBestMatchFor(color, differ)
            }
        }
    return if (color.isBWColor(selector)) {
        findOrNull(getBWColors(selector))
    } else if (color.isRedColor(selector)) {
        findOrNull(getRedColors(selector))
    } else if (color.isGreenColor(selector)) {
        findOrNull(getGreenColors(selector))
    } else {
        findOrNull(getBlueColors(selector))
    }
}

fun ShadowSet.findBestMatchForOrNull(color: ColorContainer, differ: ColorDiffer): ColorContainer? {
    fun findOrNull(colors: Iterable<ColorContainer>): ColorContainer? =
        colors.let {
            if (!it.iterator().hasNext()) {
                null
            } else {
                it.findBestMatchFor(color, differ)
            }
        }
    return if (color.isBWColor(mSelector)) {
        findOrNull(bwColors)
    } else if (color.isRedColor(mSelector)) {
        findOrNull(redColors)
    } else if (color.isGreenColor(mSelector)) {
        findOrNull(greenColors)
    } else {
        findOrNull(blueColors)
    }
}

interface ColorDiffer {
    val maxDiff: Float
        get() = differenceBetween(whiteColor, transparentColor)

    fun differenceBetween(c1: ColorContainer, c2: ColorContainer): Float
}

val absColorDiffer: ColorDiffer by lazy {
    ABSColorDiffer()
}

val absWeightColorDiffer: ColorDiffer by lazy {
    ABSWeightColorDiffer()
}

val euclidColorDiffer: ColorDiffer by lazy {
    EuclidColorDiffer()
}

class ABSColorDiffer() : ColorDiffer {
    override fun differenceBetween(c1: ColorContainer, c2: ColorContainer): Float = (
            abs(c1.r - c2.r) +
                    abs(c1.g - c2.g) +
                    abs(c1.b - c2.b) +
                    abs(c1.a - c2.a)).toFloat()
}

class ABSWeightColorDiffer() : ColorDiffer {
    override fun differenceBetween(c1: ColorContainer, c2: ColorContainer): Float = (
            abs(c1.r - c2.r) * RED_WEIGHT +
                    abs(c1.g - c2.g) * GREEN_WEIGHT +
                    abs(c1.b - c2.b) * BLUE_WEIGHT +
                    abs(c1.a - c2.a)
            ).toFloat()
}

class EuclidColorDiffer() : ColorDiffer {
    override fun differenceBetween(c1: ColorContainer, c2: ColorContainer): Float =
        sqrt(
            (c1.r - c2.r).toFloat().pow(2) +
                    (c1.g - c2.g).toFloat().pow(2) +
                    (c1.b - c2.b).toFloat().pow(2) +
                    (c1.a - c2.a).toFloat().pow(2)
        )
}

fun ColorContainer.isBetweenColors(
    c1: ColorContainer,
    c2: ColorContainer,
    threshold: Int = DEFAULT_GRAY_DIFF
): Boolean {
    val rOk = r in c1.r..c2.r
    val gOk = g in c1.g..c2.g
    val bOk = b in c1.b..c2.b
    val diff1 = abs(r - g)
    val diff2 = abs(g - b)
    val diff3 = abs(r - b)
    val diffOk = (diff1 < threshold && diff2 < threshold && diff3 < threshold)
    return diffOk && rOk && gOk && bOk
}

enum class ColorStringOrder {
    AARRGGBB,
    RRGGBBAA,
    BBAARRGG
}