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


/**
 * Created by Dmitry Starkin on 26.03.2023 12:47.
 */

val whiteColor: ColorContainer by lazy {
    ColorComponentsContainer(
        MAX_COLOR_COMPONENT_VALUE,
        MAX_COLOR_COMPONENT_VALUE,
        MAX_COLOR_COMPONENT_VALUE
    )
}

val blackColor: ColorContainer by lazy {
    ColorComponentsContainer(
        MIN_COLOR_COMPONENT_VALUE,
        MIN_COLOR_COMPONENT_VALUE,
        MIN_COLOR_COMPONENT_VALUE
    )
}

val redColor: ColorContainer by lazy {
    ColorComponentsContainer(
        MAX_COLOR_COMPONENT_VALUE,
        MIN_COLOR_COMPONENT_VALUE,
        MIN_COLOR_COMPONENT_VALUE
    )
}

val greenColor: ColorContainer by lazy {
    ColorComponentsContainer(
        MIN_COLOR_COMPONENT_VALUE,
        MAX_COLOR_COMPONENT_VALUE,
        MIN_COLOR_COMPONENT_VALUE
    )
}

val blueColor: ColorContainer by lazy {
    ColorComponentsContainer(
        MIN_COLOR_COMPONENT_VALUE,
        MIN_COLOR_COMPONENT_VALUE,
        MAX_COLOR_COMPONENT_VALUE
    )
}

val yellowColor: ColorContainer by lazy {
    redColor + greenColor
}

val magentaColor: ColorContainer by lazy {
    redColor + blueColor
}

val blueGreenColor: ColorContainer by lazy {
    greenColor + blueColor
}

val transparentColor: ColorContainer by lazy {
    ColorComponentsContainer(
        Color.red(Color.TRANSPARENT),
        Color.green(Color.TRANSPARENT),
        Color.blue(Color.TRANSPARENT),
        Color.alpha(Color.TRANSPARENT)
    )
}

val bwPalette : List<ColorContainer> by lazy { listOf(
    whiteColor,
    blackColor
)}

val basePalette : List<ColorContainer> by lazy { listOf(
    whiteColor,
    blackColor,
    redColor,
    greenColor,
    blueColor
)}

val extendPalette : List<ColorContainer> by lazy { listOf(
    whiteColor,
    blackColor,
    redColor,
    greenColor,
    blueColor,
    yellowColor,
    magentaColor,
    blueGreenColor
)}