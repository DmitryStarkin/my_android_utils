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

import android.graphics.ColorMatrix
import androidx.annotation.IntRange
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Created by Dmitry Starkin on 02.02.2024 14:24.
 */

enum class ColorChannel(val line: Int, val column: Int){
    Red(0, 0),
    Green(1, 1),
    Blue(2,2),

}

private fun lazyEmptyColorMatrixProvider(initialiseValue: () -> ColorMatrix): ReadOnlyProperty<Nothing?, ColorMatrix> =
    object : ReadOnlyProperty<Nothing?, ColorMatrix> {

        private var value: ColorMatrix? = null

        override fun getValue(thisRef: Nothing?, property: KProperty<*>): ColorMatrix {

            return this.value?.also { it.reset() } ?: initialiseValue().also {
                this.value = it
            }
        }
    }

val emptyColorMatrix: ColorMatrix by lazyEmptyColorMatrixProvider {
    ColorMatrix()
}

fun getOpaqueMatrix(@IntRange(from = -255 , to = 255) opaque: Int): ColorMatrix  =
    ColorMatrix(
        floatArrayOf(
            1f, 0f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f, 0f,
            0f, 0f, 1f, 0f, 0f,
            0f, 0f, 0f, 1f, opaque.toFloat()
        )
    )

fun getFillAllByMonoColorMatrix(color: ColorContainer): ColorMatrix =
    ColorMatrix(floatArrayOf(
        0f, 0f, 0f, 0f, color.r.toFloat(),
        0f, 0f, 0f, 0f, color.g.toFloat(),
        0f, 0f, 0f, 0f, color.b.toFloat(),
        0f, 0f, 0f, 0f, color.a.toFloat()
    ))

fun getFillAllByMonoColorTransExcludeMatrix(color: ColorContainer): ColorMatrix =
    ColorMatrix(floatArrayOf(
        0f, 0f, 0f, 0f, color.r.toFloat(),
        0f, 0f, 0f, 0f, color.g.toFloat(),
        0f, 0f, 0f, 0f, color.b.toFloat(),
        0f, 0f, 0f, 1f, 0f
    ))

//TODO not testing
fun transferOpaqueMatrix(color: ColorContainer): ColorMatrix =
    ColorMatrix(
        floatArrayOf(
            1f, 0f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f, 0f,
            0f, 0f, 1f, 0f, 0f,
            0f, 0f, 0f, color.a.toFloat(), 0f
        )
    )

fun getRedRotatedMatrix(angle: Float): ColorMatrix =
    ColorMatrix().also {
        it.setRotate(0, angle)
    }

fun getGreenRotatedMatrix(angle: Float): ColorMatrix =
    ColorMatrix().also {
        it.setRotate(1, angle)
    }

fun getBlueRotatedMatrix(angle: Float): ColorMatrix =
    ColorMatrix().also {
        it.setRotate(2, angle)
    }

fun getAlphaChannelCoefficientMatrix(rAlpha: Float, gAlpha: Float, bAlpha: Float): ColorMatrix =
    ColorMatrix(floatArrayOf(
        1f, 0f, 0f, rAlpha, 0f,
        0f, 1f, 0f, gAlpha, 0f,
        0f, 0f, 1f, bAlpha, 0f,
        0f, 0f, 0f, 1f, 0f
    ))

fun getColorCoefficientMatrix(r: Float, g: Float, b: Float): ColorMatrix =
    ColorMatrix(floatArrayOf(
        r, 0f, 0f, 0f, 0f,
        0f, g, 0f, 0f, 0f,
        0f, 0f, b, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    ))

fun getSwappedChannelsMatrix(first: ColorChannel, second: ColorChannel): ColorMatrix {
    val ar = floatArrayOf(
        1f, 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 1f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    ).also {
        it[first.line * 5 + first.line * 1] = 0f
        it[second.line * 5 + first.line] = 1f
        it[second.line * 5 + second.line * 1] = 0f
        it[first.line * 5 + second.line] = 1f
    }
    return ColorMatrix(ar)
}

fun getSaturationMatrix(@IntRange(
    from = -100,
    to = 100
) saturation: Int){
    ColorMatrix().also { it.setSaturation(saturation.toFloat()/100) }
}

fun getComplexMatrix(vararg matrixSet: ColorMatrix): ColorMatrix =
    ColorMatrix().also {
        matrixSet.forEach { nextMatrix ->
            it.preConcat(nextMatrix)
        }
    }

val negativeColorMatrix: ColorMatrix by lazy {
    ColorMatrix(
        floatArrayOf(
            -1f, 0f, 0f, 0f, 255f,
            0f, -1f, 0f, 0f, 255f,
            0f, 0f, -1f, 0f, 255f,
            0f, 0f, 0f, 1f, 0f
        )
    )
}

val sepiaColorMatrix: ColorMatrix by lazy {
    ColorMatrix().also{
        it.setSaturation(0f)
        it.postConcat(ColorMatrix().also { second ->
            second.setScale(1f, 1f, 0.8f, 1f)
        })
    }
}

val sepiaColorMatrix2: ColorMatrix by lazy {
    ColorMatrix(
        floatArrayOf(
            0.393f, 0.349f, 0.272f, 0f, 0f,
            0.769f, 0.686f, 0.534f, 0f, 0f,
            0.189f, 0.168f, 0.131f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
    )
}

val colorBlurMatrix: ColorMatrix by lazy {
    ColorMatrix(
        floatArrayOf(
            1f/9f, 1f/9f, 1f/9f, 1f/9f, 1f/9f,
            1f/9f, 1f/9f, 1f/9f, 1f/9f, 1f/9f,
            1f/9f, 1f/9f, 1f/9f, 1f/9f, 1f/9f,
            1f/9f, 1f/9f, 1f/9f, 1f/9f, 1f/9f
        )
    )
}

val grayShadowColorMatrix: ColorMatrix by lazy {
    ColorMatrix().also { it.setSaturation(0f) }
}

val grayShadowColorMatrix2: ColorMatrix by lazy {
    ColorMatrix(
        floatArrayOf(
            0.213f, 0.715f, 0.072f, 0.0f, 0.0f,
            0.213f, 0.715f, 0.072f, 0.0f, 0.0f,
            0.213f, 0.715f, 0.072f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f
        )
    )
}

val grayShadowColorMatrix3: ColorMatrix by lazy {
    ColorMatrix(
        floatArrayOf(
            0.3f, 0.59f, 0.11f, 0f, 0f,
            0.3f, 0.59f, 0.11f, 0f, 0f,
            0.3f, 0.59f, 0.11f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
    )
}

val grayShadowColorMatrix4: ColorMatrix by lazy {
    ColorMatrix(
        floatArrayOf(
            0.5f, 0.5f, 0.5f, 0f, 0f,
            0.5f, 0.5f, 0.5f, 0f, 0f,
            0.5f, 0.5f, 0.5f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
    )
}

val bwNegativeColorMatrix: ColorMatrix by lazy {
    ColorMatrix().also { it.setConcat(negativeColorMatrix, grayShadowColorMatrix2) }
}