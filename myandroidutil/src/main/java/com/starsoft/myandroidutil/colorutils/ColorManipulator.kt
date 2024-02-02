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

import android.graphics.Bitmap
import android.graphics.Color.argb
import androidx.annotation.IntRange
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.abs

/**
 * Created by Dmitry Starkin on 26.03.2023 12:49.
 */

/**
 * val bitmap
 *      val transformedBitmap = bitmap.copy(bitmap.config, true)
 *      .applyTransformations {
 *          createTransformations()
 *          //add transformations here for example
 *          .toBWNegative()
 *          .simplyCastToPaletteByShadow(KeynoaColors.values().toList(), defWeightShadowSelector)
 *      }
 */

private const val BACKGROUND_DELTA = 200f
private const val REPLACE_DELTA = 5

interface ColorTransformation{
    fun addTransformation(transformation: ColorContainer.() -> ColorContainer): ColorTransformation
    fun transform(bitmap: Bitmap): Bitmap
}

fun Bitmap.applyTransformations(transformation: ColorTransformation = emptySingleTransformation): Bitmap =
    transformation.transform(this)

fun Bitmap.applyTransformations(transformation: () -> ColorTransformation = {emptySingleTransformation}): Bitmap =
    transformation().transform(this)

fun Bitmap.applySingleTransformation(transformation: ColorTransformation.() -> ColorTransformation): Bitmap =
    emptySingleTransformation.transformation().transform(this)

fun createTransformations(recycleAfterUse: Boolean = true): ColorTransformation = if(recycleAfterUse){
    emptyMultiTransformation
} else {
    MultiTransformation(recycleAfterUse)
}

val emptySingleTransformation: ColorTransformation by lazy {
    SingleTransformation()
}

val emptyMultiTransformation: ColorTransformation by lazy {
    MultiTransformation()
}

private class SingleTransformation(private var transformation: (ColorContainer.() -> ColorContainer)? = null): ColorTransformation{

    override fun addTransformation(transformation: ColorContainer.() -> ColorContainer): ColorTransformation {
        this.transformation = transformation
        return this
    }

    override fun transform(bitmap: Bitmap): Bitmap =
        transformation?.let{
            val result = bitmap.transformColorsInPlace(it)
            transformation = null
            result
        } ?: bitmap
}

private class MultiTransformation(val autoClear: Boolean = true): ColorTransformation{

    private val transformations: ArrayList<ColorContainer.() -> ColorContainer> = ArrayList()

    override fun addTransformation(transformation: ColorContainer.() -> ColorContainer):  ColorTransformation{
        transformations.add(transformation)
        return this
    }

    override fun transform(bitmap: Bitmap): Bitmap =
        if(transformations.isEmpty()){
            bitmap
        } else {
            val result = bitmap.transformColorsInPlace(transformations)
            if(autoClear){
                transformations.clear()
            }
            result
        }
}

private fun Bitmap.transformColorsInPlace(transformations: List<ColorContainer.() -> ColorContainer>): Bitmap {
    val bitmap = if (this.isMutable) {
        this
    } else {
        val new = copy(this.config, true)
        recycle()
        new
    }
    val transformative = MutableColor()
    for (y in 0 until bitmap.height) {
        for (x in 0 until bitmap.width) {
            bitmap.setPixel(x, y,
                bitmap.getPixel(x, y).let{
                    var transformedPixel = it
                    transformations.forEach {transformation ->
                        transformedPixel = transformative.let { transformator ->
                            transformator.color = transformedPixel
                            transformator.transformation().color
                        }
                    }
                    transformedPixel
                }
            )
        }
    }
    return bitmap
}

fun Bitmap.transformColorsInPlace(transformation: ColorContainer.() -> ColorContainer): Bitmap {
    val bitmap = if (this.isMutable) {
        this
    } else {
        val new = copy(this.config, true)
        recycle()
        new
    }
    val transformative = MutableColor()
    for (y in 0 until bitmap.height) {
        for (x in 0 until bitmap.width) {
            bitmap.setPixel(x, y, transformative.let {
                it.color = bitmap.getPixel(x, y)
                it.transformation().color
            })
        }
    }
    return bitmap
}

fun ColorContainer.transparentByCoefficient(
    @IntRange(
        from = 0,
        to = 200
    ) transparencyCoefficient: Int
): ColorContainer {
    val transformative = MutableColor().also { it.color = whiteColor.color}
    fun MutableColorContainer.calculateMinColor(bgCoefficient: Int): ColorContainer {
        val avColor = (r + g + b) / 3
        val component = (avColor - ((bgCoefficient / BACKGROUND_DELTA) * avColor)).toInt()
        return this.also {
            it.color = argb(a, component, component, component)
        }
    }

    val minColorToRemove = transformative.calculateMinColor(transparencyCoefficient)

    return if (isBetweenColors(
            minColorToRemove, whiteColor

        ) && transparencyCoefficient != 0
    ) {
        if (this is MutableColorContainer) {
            also {
                it.color = transparentColor.color
            }
        } else {
            transparentColor
        }
    } else {
        this
    }
}

fun ColorContainer.compareAndReplace(
    sample: ColorContainer,
    replacement: ColorContainer,
    comparator: ColorComparator
): ColorContainer =
    if (comparator.compare(this, sample)) {
        if (this is MutableColorContainer) {
            also {
                it.color = replacement.color
            }
        } else {
            replacement
        }
    } else {
        this
    }

fun ColorContainer.setSaturation(@IntRange(
    from = -100,
    to = 100
) saturation: Int ): ColorContainer {
    val max = max(max(r, g), b)
    val min = min(min(r, g), b)
    val delta = if(saturation < 0){
        -min(((min.toFloat()/100) * abs(saturation)).toInt(), min - 1)
    } else if(saturation > 0){
        min(((max.toFloat()/100) * abs(saturation)).toInt(), 255 - max)
    } else {
        0
    }

    return if (this is MutableColorContainer) {
        also {
            it.color = argb(it.a, r + delta, g + delta, b + delta)
        }
    } else {
        ColorComponentsContainer(r + delta, g + delta, b + delta)
    }
}

fun ColorContainer.toGrayShadow(
    redW: Float = DEFAULT_RED_BRIGH_WEIGHT,
    greenW: Float = DEFAULT_GREEN_BRIGH_WEIGHT,
    blueW: Float = DEFAULT_BLUE_BRIGH_WEIGHT
): ColorContainer {
    val new = (r * redW + g * greenW + b * blueW).toInt()
    return if (this is MutableColorContainer) {
        also {
            it.color = argb(it.a, new, new, new)
        }
    } else {
        ColorComponentsContainer(new, new, new)
    }
}

fun ColorContainer.findBestMatchInPalette(
    palette: PreparedPalette,
    detailComparator: ColorComparator = ShadeComparator(
        defShadowSelector
    )
): ColorContainer {
    return if (this is MutableColorContainer) {
        also {
            it.color = palette.findBestMatchForWithCompare(this, detailComparator).color
        }
    } else {
        palette.findBestMatchForWithCompare(this, detailComparator)
    }
}

fun ColorContainer.findInGroupedPalette(
    palette: GroupedPreparedPalette,
    detailDiffer: ColorDiffer? = absWeightColorDiffer
): ColorContainer {
    return if (this is MutableColorContainer) {
        also {
            it.color = palette.findBestMatchFor(this, detailDiffer).color
        }
    } else {
        palette.findBestMatchFor(this, detailDiffer)
    }
}

fun ColorContainer.improvedFindInGroupedPalette(
    palette: GroupedPreparedPalette,
    detailDiffer: ColorDiffer? = absWeightColorDiffer
): ColorContainer {
    return if (this is MutableColorContainer) {
        also {
            it.color = palette.improvedFindBestMatchFor(this, detailDiffer).color
        }
    } else {
        palette.improvedFindBestMatchFor(this, detailDiffer)
    }
}

fun ColorContainer.findBestMatchInPaletteByShadow(
    shadowSet: ShadowSet,
    differ: ColorDiffer,
    default: ColorContainer = this
): ColorContainer {
    return if (this is MutableColorContainer) {
        also {
            it.color = shadowSet.findBestMatchForOrNull(this, differ)?.color ?: default.color
        }
    } else {
        shadowSet.findBestMatchForOrNull(this, differ) ?: default
    }
}

fun ColorContainer.simplyFindBestMatchInPaletteByShadow(
    shadowSet: ShadowSet,
    default: ColorContainer = this
): ColorContainer =
    if (this is MutableColorContainer) {
        also {
            it.color = (findBestMatchInPaletteByShadow(shadowSet, absColorDiffer) ?: default).color
        }
    } else {
        findBestMatchInPaletteByShadow(shadowSet, absColorDiffer) ?: default
    }

fun ColorContainer.setComponent(component: ColorComponent): ColorContainer {
    val newColor = when(component){
        is ColorComponent.Red -> {
            argb(a, component.value, g, b)
        }
        is ColorComponent.Green -> {
            argb(a, r, component.value, b)
        }
        is ColorComponent.Blue -> {
            argb(a, r, g, component.value)
        }
        is ColorComponent.Alpha -> {
            argb(component.value, r, g, b)
        }
    }
    return if (this is MutableColorContainer) {
        also {
            it.color = newColor
        }
    } else {
        ColorIntContainer(newColor)
    }
}

fun ColorTransformation.simplyFindBestMatchInPalette(
    palette: Iterable<ColorContainer>,
    differ: ColorDiffer = absColorDiffer
):ColorTransformation {
    val prepared = palette.getPreparedPalette(differ)
    return addTransformation {
        findBestMatchInPalette(prepared)
    }
}

fun ColorTransformation.setSaturation(@IntRange(
    from = -100,
    to = 100
) saturation: Int): ColorTransformation {
    return addTransformation {
        setSaturation( saturation)
    }
}

fun ColorTransformation.plusColor(color: ColorContainer): ColorTransformation {
    return addTransformation {
        this + color
    }
}

fun ColorTransformation.minusColor(color: ColorContainer): ColorTransformation {
    return addTransformation {
        this - color
    }
}

fun ColorTransformation.plusComponent(component: ColorComponent): ColorTransformation {
    return addTransformation {
        this + component
    }
}

fun ColorTransformation.setComponent(component: ColorComponent): ColorTransformation {
    return addTransformation {
        setComponent(component)
    }
}

fun ColorTransformation.minusComponent(component: ColorComponent): ColorTransformation {
    return addTransformation {
        this - component
    }
}

fun ColorTransformation.toNegative(): ColorTransformation {
    return addTransformation {
        -this
    }
}

fun ColorTransformation.toBWNegative(): ColorTransformation {
    return addTransformation {
        -toGrayShadow()
    }
}

fun ColorTransformation.simplyCastToPaletteWitchGrouping(
    palette: Iterable<ColorContainer>,
    groupDiffer: ColorDiffer = absWeightColorDiffer,
    detailDiffer: ColorDiffer? = absWeightColorDiffer
): ColorTransformation {
    val prepared = palette.getGroupedPalette(groupDiffer)
    return addTransformation {
        findInGroupedPalette(prepared, detailDiffer)
    }
}

fun ColorTransformation.simplyCastToPaletteWitchGroupingImproved(
    palette: Iterable<ColorContainer>,
    groupDiffer: ColorDiffer = absWeightColorDiffer,
    detailDiffer: ColorDiffer? = absWeightColorDiffer
): ColorTransformation {
    val prepared = palette.getGroupedPalette(groupDiffer)
    return addTransformation {
        improvedFindInGroupedPalette(prepared, detailDiffer)
    }
}

fun ColorTransformation.simplyCastToPaletteByShadow(
    palette: Iterable<ColorContainer>,
    selector: ShadowSelector = defShadowSelector
): ColorTransformation {
    val shadowSet = palette.toShadowSet(selector)
    return addTransformation {
        simplyFindBestMatchInPaletteByShadow(shadowSet)
    }
}

fun ColorTransformation.simplyCastToPaletteByShadow(
    palette: Iterable<ColorContainer>,
    default: ColorContainer,
    selector: ShadowSelector = defShadowSelector
): ColorTransformation {
    val shadowSet = palette.toShadowSet(selector)
    return addTransformation {
        simplyFindBestMatchInPaletteByShadow(shadowSet, default)
    }
}

fun ColorTransformation.toGrayShadow(
    redW: Float = DEFAULT_RED_BRIGH_WEIGHT,
    greenW: Float = DEFAULT_GREEN_BRIGH_WEIGHT,
    blueW: Float = DEFAULT_BLUE_BRIGH_WEIGHT
): ColorTransformation =
    addTransformation {
        toGrayShadow(redW, greenW, blueW)
    }

fun ColorTransformation.removeBackground(@IntRange(from = 0, to = 200) backgroundCoefficient: Int): ColorTransformation =
    addTransformation {
        transparentByCoefficient(backgroundCoefficient)
    }

fun ColorTransformation.replaceColor(
    sample: ColorContainer,
    replacement: ColorContainer,
    comparator: ColorComparator
): ColorTransformation =
    addTransformation {
        compareAndReplace(sample, replacement, comparator)
    }

fun ColorTransformation.removeColor(sample: ColorContainer): ColorTransformation =
    replaceColor(sample, transparentColor)

fun ColorTransformation.removeColorWithDelta(sample: ColorContainer, delta: Int = REPLACE_DELTA): ColorTransformation =
    replaceColorWithDelta(sample, transparentColor, delta)

fun ColorTransformation.replaceColor(sample: ColorContainer, replacement: ColorContainer): ColorTransformation =
    replaceColor(sample, replacement, simpleColorComparator)


fun ColorTransformation.replaceColorWithDelta(
    sample: ColorContainer,
    replacement: ColorContainer,
    delta: Int = REPLACE_DELTA
): ColorTransformation =
    replaceColor(sample, replacement, ABSComparator(delta))


fun ColorTransformation.removeRedColors(selector: ShadowSelector = defShadowSelector):ColorTransformation =
    replaceColor(redColor, transparentColor, ShadeComparator(selector))

fun ColorTransformation.removeGreenColors(selector: ShadowSelector = defShadowSelector): ColorTransformation =
    replaceColor(greenColor, transparentColor, ShadeComparator(selector))

fun ColorTransformation.removeBlueColors(selector: ShadowSelector = defShadowSelector): ColorTransformation =
    replaceColor(blueColor, transparentColor, ShadeComparator(selector))

fun ColorTransformation.removeGreyColors(selector: ShadowSelector = defShadowSelector): ColorTransformation =
    replaceColor(whiteColor, transparentColor, ShadeComparator(selector))