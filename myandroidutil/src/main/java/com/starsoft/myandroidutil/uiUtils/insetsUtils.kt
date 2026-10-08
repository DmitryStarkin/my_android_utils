/*
 * Copyright (c) 2026. Dmitry Starkin Contacts: t0506803080@gmail.com
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

package com.starsoft.myandroidutil.uiUtils

import android.graphics.Rect
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import androidx.core.view.updatePadding
import kotlin.math.max

/**
 * Created by Dmitry Starkin on 11.08.2026 16:08.
 */

fun View.doOnApplyWindowInsets(block: (View, WindowInsetsCompat, Rect) -> WindowInsetsCompat) {
    val initialPadding = recordInitialPaddingForView(this)
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        block(v, insets, initialPadding)
    }

    requestApplyInsets()
}

fun View.consumeWindowInsets(consume: Boolean = true) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        if(consume){
            WindowInsetsCompat.CONSUMED
        } else {
            insets
        }
    }
    requestApplyInsets()
}

fun View.doSetTopSpaceByInsets(consume: Boolean = false) {
    doOnLayout {
        val initialPadding = recordInitialPaddingForView(this)
        val h = height
        val params = layoutParams
        ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
            val top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            params.height = h + top
            this.layoutParams = params
            this.updatePadding(
                top = initialPadding.top + top,
            )
            if(consume){
                WindowInsetsCompat.CONSUMED
            } else {
                insets
            }
        }
        requestApplyInsets()
    }
}

fun View.doSetBottomSpaceByInsets(consume: Boolean = true) {
    doOnLayout {
        val initialPadding = recordInitialPaddingForView(this)
        val h = height
        val params = layoutParams
        ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
            val bottom = max(
                insets.getInsets(WindowInsetsCompat.Type.ime()).bottom,
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            )
            if(bottom == 0){
                this.visibility = View.GONE
            } else {
                params.height = h + bottom
                this.layoutParams = params
                this.updatePadding(
                    bottom = initialPadding.bottom + bottom,
                )
            }
            if(consume){
                WindowInsetsCompat.CONSUMED
            } else {
                insets
            }
        }
        requestApplyInsets()
    }
}

fun View.doUpdateTopPagingByInsets(consume: Boolean = false) {
    val initialPadding = recordInitialPaddingForView(this)
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        this.updatePadding(
            top = initialPadding.top + insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
        )
        if(consume){
            WindowInsetsCompat.CONSUMED
        } else {
            insets
        }
    }
    requestApplyInsets()
}

fun View.doUpdateBottomPagingByInsets(consume: Boolean = false) {
    val initialPadding = recordInitialPaddingForView(this)
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val bottom = max(insets.getInsets(WindowInsetsCompat.Type.ime()).bottom, insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom)
        this.updatePadding(
            bottom = initialPadding.bottom + bottom
        )
        if(consume){
            WindowInsetsCompat.CONSUMED
        } else {
            insets
        }
    }
    requestApplyInsets()
}

fun View.doUpdatePagingByInsets(consume: Boolean = false) {
    val initialPadding = recordInitialPaddingForView(this)
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val bottom = max(insets.getInsets(WindowInsetsCompat.Type.ime()).bottom, insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom)
        this.updatePadding(
            top = initialPadding.top +  insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
            bottom = initialPadding.bottom + bottom,
            left = initialPadding.bottom + insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
            right = initialPadding.bottom + insets.getInsets(WindowInsetsCompat.Type.systemBars()).right
        )
        if(consume){
            WindowInsetsCompat.CONSUMED
        } else {
            insets
        }
    }
    requestApplyInsets()
}

private fun recordInitialPaddingForView(view: View) =
    Rect(view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom)