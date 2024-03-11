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
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color.argb
import android.graphics.ColorFilter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.LightingColorFilter
import android.graphics.MaskFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.PorterDuffXfermode
import android.os.Build
import androidx.annotation.IntRange
import androidx.annotation.RequiresApi
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.abs

/**
 * Created by Dmitry Starkin on 26.03.2023 12:49.
 */

/**
 * val bitmap
 *      val transformedBitmap =
 *      bitmap.applyTransformations {
 *          getTransformationsInvoker()
 *          //add transformations here for example
 *          .toBWNegative()
 *          .simplyCastToPaletteByShadow(KeynoaColors.values().toList(), defWeightShadowSelector)
 *          .addBitmapTransformation { applyColorMatrix(grayShadowColorMatrix2) }
 *      }
 */

private const val BACKGROUND_DELTA = 200f
private const val REPLACE_DELTA = 5


interface DrawTools{
    val paint: Paint
    val path: Path
}

class SimplyDrawTools(antialias: Boolean = true): DrawTools{
    override val paint: Paint = Paint().also{ it.isAntiAlias = antialias }
    override val path: Path = Path()
}

val commonDrawTools: DrawTools by lazy {
    SimplyDrawTools()
}

interface Transformation{
    fun addColorTransformation(transformation: ColorContainer.() ->  ColorContainer): Transformation =
        transformation.colorContainerFunToTransformation()
    fun addBitmapTransformation(transformation: Bitmap.() ->  Bitmap): Transformation =
        transformation.bitmapFunToTransformation()
    fun transform(bitmap: Bitmap): Bitmap
}

interface ColorTransformation: (ColorContainer) -> ColorContainer,  Transformation

interface BitmapTransformation: (Bitmap) -> Bitmap,  Transformation

fun (Bitmap.() -> Bitmap).bitmapFunToTransformation(): Transformation =
    object : BitmapTransformation{
        override fun invoke(p1: Bitmap): Bitmap {
            return p1.this@bitmapFunToTransformation()
        }
        override fun addColorTransformation(transformation:ColorContainer.() ->  ColorContainer): Transformation = throw Exception("use invoker fot this")
        override fun addBitmapTransformation(transformation: Bitmap.() -> Bitmap): Transformation = throw Exception("use invoker fot this")
        override fun transform(bitmap: Bitmap): Bitmap = throw Exception("use invoker fot this")
    }

fun (ColorContainer.() -> ColorContainer).colorContainerFunToTransformation(): Transformation =

    object : ColorTransformation{
        override fun invoke(p1: ColorContainer): ColorContainer {
            return p1.this@colorContainerFunToTransformation()
        }
        override fun addBitmapTransformation(transformation: Bitmap.() -> Bitmap): Transformation = throw Exception("use invoker fot this")
        override fun addColorTransformation(transformation:ColorContainer.() ->  ColorContainer): Transformation = throw Exception("use invoker fot this")
        override fun transform(bitmap: Bitmap): Bitmap = throw Exception("use invoker fot this")
    }

fun Bitmap.applyTransformations(transformation: Transformation = emptySingleTransformationInvoker): Bitmap =
    transformation.transform(this)

fun Bitmap.applyTransformations(transformation: () -> Transformation = {emptySingleTransformationInvoker}): Bitmap =
    transformation().transform(this)

fun Bitmap.applySingleTransformation(transformation: Transformation.() -> Transformation): Bitmap =
    emptySingleTransformationInvoker.transformation().transform(this)

fun getTransformationsInvoker(recycleAfterUse: Boolean = true): Transformation = if(recycleAfterUse){
    emptyMultiTransformationInvoker
} else {
    MultiTransformationInvoker(recycleAfterUse)
}

val emptySingleTransformationInvoker: Transformation by lazy {
    SingleTransformationInvoker()
}

val emptyMultiTransformationInvoker: Transformation by lazy {
    MultiTransformationInvoker()
}

private class SingleTransformationInvoker(private var transformation: Transformation? = null): Transformation {

    override fun addColorTransformation(transformation: ColorContainer.() ->  ColorContainer): Transformation {
        this.transformation = transformation.colorContainerFunToTransformation()
        return this
    }

    override fun addBitmapTransformation(transformation: Bitmap.() ->  Bitmap): Transformation {
        this.transformation = transformation.bitmapFunToTransformation()
        return this
    }

    override fun transform(bitmap: Bitmap): Bitmap =
        transformation?.let{
            val result = bitmap.transformInPlace(it)
            transformation = null
            result
        } ?: bitmap
}

private class MultiTransformationInvoker(val autoClear: Boolean = true): Transformation{

    private val transformations: ArrayList<Transformation> = ArrayList()

    override fun addColorTransformation(transformation: ColorContainer.() ->  ColorContainer):  Transformation{
        transformations.add(transformation.colorContainerFunToTransformation())
        return this
    }

    override fun addBitmapTransformation(transformation: Bitmap.() ->  Bitmap):  Transformation{
        transformations.add(transformation.bitmapFunToTransformation())
        return this
    }

    override fun transform(bitmap: Bitmap): Bitmap =
        if(transformations.isEmpty()){
            bitmap
        } else {
            val result = bitmap.transformInPlace(transformations)
            if(autoClear){
                transformations.clear()
            }
            result
        }
}

private fun Bitmap.transformInPlace(transformations: List<Transformation>): Bitmap {

    val colorTransformations : ArrayList<ColorTransformation> = ArrayList()
    val bitmapTransformations : ArrayList<BitmapTransformation> = ArrayList()
    var bitmap = toMutable()
    fun applyColorTransformations(transformations: List<ColorTransformation>){
        val transformative = MutableColor()
        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                bitmap.setPixel(x, y,
                    bitmap.getPixel(x, y).let{
                        var transformedPixel = it
                        transformations.forEach {transformation ->
                            transformedPixel = transformative.let { transformer ->
                                transformer.color = transformedPixel
                                transformation.invoke(transformer).color
                            }
                        }
                        transformedPixel
                    }
                )
            }
        }
    }

    fun applyBitmapTransformations(transformations: List<BitmapTransformation>){
        transformations.forEach{
            bitmap = it.invoke(bitmap)
        }
    }
    transformations.forEach{
        if(it is ColorTransformation && bitmapTransformations.isEmpty()){
            colorTransformations.add(it)
        } else if(it is ColorTransformation && bitmapTransformations.isNotEmpty()){
            applyBitmapTransformations(bitmapTransformations)
            bitmapTransformations.clear()
            colorTransformations.add(it)
        } else if (it is BitmapTransformation && colorTransformations.isEmpty()){
            bitmapTransformations.add(it)
        } else if(it is BitmapTransformation && colorTransformations.isNotEmpty()){
            applyColorTransformations(colorTransformations)
            colorTransformations.clear()
            bitmapTransformations.add(it)
        }
    }
    applyColorTransformations(colorTransformations)
    applyBitmapTransformations(bitmapTransformations)
    return bitmap
}

private fun Bitmap.transformInPlace(transformation: Transformation): Bitmap {
    var bitmap = toMutable()
    if(transformation is ColorTransformation){
        val transformative = MutableColor()
        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                bitmap.setPixel(x, y, transformative.let {
                    it.color = bitmap.getPixel(x, y)
                    transformation.invoke(it).color
                })
            } }
    } else if(transformation is BitmapTransformation) {
        bitmap = transformation.invoke(bitmap)
    }
    return bitmap
}

@RequiresApi(Build.VERSION_CODES.Q)
fun Bitmap.applyBlendModeColorFilter(
    color: ColorContainer,
    bMode : BlendMode = BlendMode.SRC,
    drawTools: DrawTools = commonDrawTools
):Bitmap {
    drawTools.paint.apply {
        colorFilter = BlendModeColorFilter(color.color, bMode)
        Canvas(this@applyBlendModeColorFilter).drawBitmap(this@applyBlendModeColorFilter,0f,0f,this)
    }
    drawTools.paint.reset()
    return this
}

fun Bitmap.applyBlurFilter(
    radius: Float,
    style : BlurMaskFilter.Blur = BlurMaskFilter.Blur.NORMAL,
    pMode : PorterDuff.Mode? = PorterDuff.Mode.SRC,
    drawTools: DrawTools = commonDrawTools
):Bitmap {
    drawTools.paint.apply {
        maskFilter = BlurMaskFilter(if(radius <= 0f){0.1f}else{radius}, style)
        xfermode = PorterDuffXfermode(pMode)
        Canvas(this@applyBlurFilter).drawBitmap(this@applyBlurFilter,0f,0f,this)
    }
    drawTools.paint.reset()
    return this
}

fun Bitmap.applyFilters(
    cFilter: ColorFilter,
    mFilter: MaskFilter,
    pMode : PorterDuff.Mode? = PorterDuff.Mode.SRC,
    drawTools: DrawTools = commonDrawTools
):Bitmap {
    drawTools.paint.apply {
        maskFilter = mFilter
        colorFilter = cFilter
        xfermode = PorterDuffXfermode(pMode)
        Canvas(this@applyFilters).drawBitmap(this@applyFilters,0f,0f,this)
    }
    drawTools.paint.reset()
    return this
}

fun Bitmap.applyLightingColorFilter(
    color1: ColorContainer,
    color2: ColorContainer,
    pMode : PorterDuff.Mode? = PorterDuff.Mode.SRC,
    drawTools: DrawTools = commonDrawTools
):Bitmap {
    drawTools.paint.apply {
        colorFilter = LightingColorFilter(color1.color, color2.color)
        xfermode = PorterDuffXfermode(pMode)
        Canvas(this@applyLightingColorFilter).drawBitmap(this@applyLightingColorFilter,0f,0f,this)
    }
    drawTools.paint.reset()
    return this
}

fun Bitmap.applyPorterDuffColorFilter(
    color: ColorContainer,
    mode: PorterDuff.Mode = PorterDuff.Mode.LIGHTEN,
    pMode : PorterDuff.Mode? = PorterDuff.Mode.SRC,
    drawTools: DrawTools = commonDrawTools
):Bitmap {
    drawTools.paint.apply {
        colorFilter = PorterDuffColorFilter(color.color, mode)
        xfermode = PorterDuffXfermode(pMode)
        Canvas(this@applyPorterDuffColorFilter).drawBitmap(this@applyPorterDuffColorFilter,0f,0f,this)
    }
    drawTools.paint.reset()
    return this
}


fun Bitmap.applyColorMatrix(
    colorMatrix: ColorMatrix,
    mode: PorterDuff.Mode? = PorterDuff.Mode.SRC,
    drawTools: DrawTools = commonDrawTools
):Bitmap {
    drawTools.paint.apply {
        colorFilter = ColorMatrixColorFilter(colorMatrix)
        xfermode = PorterDuffXfermode(mode)
        Canvas(this@applyColorMatrix).drawBitmap(this@applyColorMatrix, 0f, 0f, this)
    }
    drawTools.paint.reset()
    return this
}

fun Transformation.mSetSaturation(@IntRange(
    from = -100,
    to = 100
) saturation: Int): Transformation = applyColorMatrixColorFilter(emptyColorMatrix.also { it.setSaturation(saturation.toFloat()/100) })



fun Transformation.applyColorMatrixColorFilter(
    colorMatrix: ColorMatrix,
    mode: PorterDuff.Mode? = PorterDuff.Mode.SRC,
    drawTools: DrawTools = commonDrawTools
):Transformation {
    return addBitmapTransformation {
        applyColorMatrix(colorMatrix, mode, drawTools)
    }
}

fun Transformation.applyLightingColorFilter(
    color1: ColorContainer,
    color2: ColorContainer,
    mode: PorterDuff.Mode? = PorterDuff.Mode.SRC,
    drawTools: DrawTools = commonDrawTools
):Transformation {
    return addBitmapTransformation {
        applyLightingColorFilter(color1, color2, mode, drawTools)
    }
}

fun Transformation.applyPorterDuffColorFilter(
    color1: ColorContainer,
    mode: PorterDuff.Mode = PorterDuff.Mode.LIGHTEN,
    pMode: PorterDuff.Mode? = PorterDuff.Mode.SRC,
    drawTools: DrawTools = commonDrawTools
):Transformation {
    return addBitmapTransformation {
        applyPorterDuffColorFilter(color1, mode, pMode, drawTools)
    }
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

fun ColorContainer.replaceIfNotMatch(
    sample: ColorContainer,
    replacement: ColorContainer,
    comparator: ColorComparator
): ColorContainer =
    if (!comparator.compare(this, sample)) {
        //Log.d("test","not comp ${this.r}${this.g}${this.b}${this.a}")
        if (this is MutableColorContainer) {
            also {
                it.color = replacement.color
            }
        } else {
            replacement
        }
    } else {
        //Log.d("test","comp ${this.r}${this.g}${this.b}${this.a}")
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
    blueW: Float = DEFAULT_BLUE_BRIGH_WEIGHT,
    keepTransparent: Boolean = false
): ColorContainer {
    if(keepTransparent && simpleColorComparator.compare(this, transparentColor)){
        return this
    }
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
    default: ColorContainer = this,
    exclude: ColorContainer? = null
): ColorContainer {
    return exclude?.let{
        if(simpleColorComparator.compare(this, exclude)){
            this
        } else {
            null
        }
    } ?: run{
        if (this is MutableColorContainer) {
            also {
                it.color = shadowSet.findBestMatchForOrNull(this, differ)?.color ?: default.color
            }
        } else {
            shadowSet.findBestMatchForOrNull(this, differ) ?: default
        }
    }

}

fun ColorContainer.simplyFindBestMatchInPaletteByShadow(
    shadowSet: ShadowSet,
    default: ColorContainer = this,
    exclude: ColorContainer? = null
): ColorContainer =
    if (this is MutableColorContainer) {
        also {
            it.color = (findBestMatchInPaletteByShadow(shadowSet, absColorDiffer, default, exclude) ?: default).color
        }
    } else {
        findBestMatchInPaletteByShadow(shadowSet, absColorDiffer, default, exclude) ?: default
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

fun Transformation.simplyFindBestMatchInPalette(
    palette: Iterable<ColorContainer>,
    differ: ColorDiffer = absColorDiffer
):Transformation {
    val prepared = palette.getPreparedPalette(differ)
    return addColorTransformation {
        findBestMatchInPalette(prepared)
    }
}

fun Transformation.setSaturation(@IntRange(
    from = -100,
    to = 100
) saturation: Int): Transformation {
    return addColorTransformation {
        setSaturation(saturation)
    }
}

fun Transformation.plusColor(color: ColorContainer): Transformation {
    return addColorTransformation {
        this + color
    }
}

fun Transformation.minusColor(color: ColorContainer): Transformation {
    return addColorTransformation {
        this - color
    }
}

fun Transformation.plusComponent(component: ColorComponent): Transformation {
    return addColorTransformation {
        this + component
    }
}

fun Transformation.setComponent(component: ColorComponent): Transformation {
    return addColorTransformation {
        setComponent(component)
    }
}

fun Transformation.minusComponent(component: ColorComponent): Transformation {
    return addColorTransformation {
        this - component
    }
}

fun Transformation.toNegative(): Transformation {
    return addColorTransformation {
        -this
    }
}

fun Transformation.toBWNegative(
    keepTransparent: Boolean = false): Transformation {
    return addColorTransformation {
        -toGrayShadow(keepTransparent = keepTransparent)
    }
}

fun Transformation.simplyCastToPaletteWitchGrouping(
    palette: Iterable<ColorContainer>,
    groupDiffer: ColorDiffer = absWeightColorDiffer,
    detailDiffer: ColorDiffer? = absWeightColorDiffer
): Transformation {
    val prepared = palette.getGroupedPalette(groupDiffer)
    return addColorTransformation {
        findInGroupedPalette(prepared, detailDiffer)
    }
}

fun Transformation.simplyCastToPaletteWitchGroupingImproved(
    palette: Iterable<ColorContainer>,
    groupDiffer: ColorDiffer = absWeightColorDiffer,
    detailDiffer: ColorDiffer? = absWeightColorDiffer
): Transformation {
    val prepared = palette.getGroupedPalette(groupDiffer)
    return addColorTransformation {
        improvedFindInGroupedPalette(prepared, detailDiffer)
    }
}

fun Transformation.simplyCastToPaletteByShadow(
    palette: Iterable<ColorContainer>,
    exclude: ColorContainer? = null,
    selector: ShadowSelector = defShadowSelector
): Transformation {
    val shadowSet = palette.toShadowSet(selector)
    return addColorTransformation {
        simplyFindBestMatchInPaletteByShadow(shadowSet, exclude = exclude)
    }
}

fun Transformation.simplyCastToPaletteByShadow(
    palette: Iterable<ColorContainer>,
    default: ColorContainer,
    exclude: ColorContainer? = null,
    selector: ShadowSelector = defShadowSelector
): Transformation {
    val shadowSet = palette.toShadowSet(selector)
    return addColorTransformation {
        simplyFindBestMatchInPaletteByShadow(shadowSet, default, exclude)
    }
}

fun Transformation.toGrayShadow(
    redW: Float = DEFAULT_RED_BRIGH_WEIGHT,
    greenW: Float = DEFAULT_GREEN_BRIGH_WEIGHT,
    blueW: Float = DEFAULT_BLUE_BRIGH_WEIGHT,
    keepTransparent: Boolean = false
): Transformation =
    addColorTransformation {
        toGrayShadow(redW, greenW, blueW, keepTransparent)
    }

fun Transformation.removeBackground(@IntRange(from = 0, to = 200) backgroundCoefficient: Int):Transformation =
    addColorTransformation {
        transparentByCoefficient(backgroundCoefficient)
    }

fun Transformation.replaceColor(
    sample: ColorContainer,
    replacement: ColorContainer,
    comparator: ColorComparator
): Transformation =
    addColorTransformation {
        compareAndReplace(sample, replacement, comparator)
    }

fun Transformation.replaceAllColors(
    exclude: ColorContainer,
    replacement: ColorContainer,
    comparator: ColorComparator
): Transformation =
    addColorTransformation {
        replaceIfNotMatch(exclude, replacement, comparator)
    }

fun Transformation.removeColor(sample: ColorContainer): Transformation =
    replaceColor(sample, transparentColor)

fun Transformation.removeColorWithDelta(sample: ColorContainer, delta: Int = REPLACE_DELTA):Transformation =
    replaceColorWithDelta(sample, transparentColor, delta)

fun Transformation.replaceColor(sample: ColorContainer, replacement: ColorContainer): Transformation =
    replaceColor(sample, replacement, simpleColorComparator)

fun Transformation.replaceAllColors(exclude: ColorContainer, replacement: ColorContainer): Transformation =
    replaceAllColors(exclude, replacement, simpleColorComparator)

fun Transformation.replaceColorWithDelta(
    sample: ColorContainer,
    replacement: ColorContainer,
    delta: Int = REPLACE_DELTA
): Transformation =
    replaceColor(sample, replacement, ABSComparator(delta))


fun Transformation.removeRedColors(selector: ShadowSelector = defShadowSelector):Transformation =
    replaceColor(redColor, transparentColor, ShadeComparator(selector))

fun Transformation.removeGreenColors(selector: ShadowSelector = defShadowSelector): Transformation =
    replaceColor(greenColor, transparentColor, ShadeComparator(selector))

fun Transformation.removeBlueColors(selector: ShadowSelector = defShadowSelector): Transformation =
    replaceColor(blueColor, transparentColor, ShadeComparator(selector))

fun Transformation.removeGreyColors(selector: ShadowSelector = defShadowSelector): Transformation =
    replaceColor(whiteColor, transparentColor, ShadeComparator(selector))