/*
 * Copyright (c) 2021. Dmitry Starkin Contacts: t0506803080@gmail.com
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

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.IBinder
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.starsoft.myandroidutil.stringext.BOLD_SPAN
import com.starsoft.myandroidutil.stringext.applyStyleSpanToMatches

/**
 * Created by Dmitry Starkin on 08.05.2021 14:01.
 */
fun Context.resolveOrThrow(@AttrRes attributeResId: Int): Int {
    val typedValue = TypedValue()
    if (this.theme.resolveAttribute(attributeResId, typedValue, true)) {
        return typedValue.data
    }
    throw IllegalArgumentException(
        this.resources.getResourceName(attributeResId)
    )
}

/**
 * Close keyboard using InputMethodManager
 */
fun android.app.Activity.hideKeyboard(token: IBinder) {
    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(
            token,
            0
        )
}

//TODO this not work currently
fun View.hideKeyboard() =
    ViewCompat.getWindowInsetsController(this)?.hide(WindowInsetsCompat.Type.ime())

/**
 * Open keyboard on specific edit text using InputMethodManager
 */
fun android.app.Activity.showKeyboard(editText: EditText) {
    editText.requestFocusFromTouch()
    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .showSoftInput(editText, 0)
}

fun Activity.getRootView(): View {
    return findViewById<View>(android.R.id.content)
}

fun Activity.isKeyboardOpen(): Boolean {
    val visibleBounds = Rect()
    this.getRootView().getWindowVisibleDisplayFrame(visibleBounds)
    val heightDiff = getRootView().height - visibleBounds.height()
    val marginOfError = convertDpToPixel(50F, this)
    return heightDiff > marginOfError
}

fun Activity.isKeyboardClosed(): Boolean {
    return !this.isKeyboardOpen()
}

fun View.isKeyboardVisible(): Boolean =
    ViewCompat.getRootWindowInsets(this)?.isVisible(WindowInsetsCompat.Type.ime())
        ?: false

fun View.isKeyboardClosed(): Boolean = !this.isKeyboardVisible()

fun View.getKeyboardHeight(): Int =
    ViewCompat.getRootWindowInsets(this)?.getInsets(WindowInsetsCompat.Type.ime())?.bottom ?: 0

fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        requestApplyInsets()
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

fun convertDpToPixel(dp: Float, context: Context): Int {
    val resources = context.resources
    val metrics = resources.displayMetrics
    val px = dp * (metrics.densityDpi / 160f)
    return px.toInt()
}

fun pixelsToSp(context: Context, px: Float): Float {
    val scaledDensity =
        context.resources.displayMetrics.scaledDensity
    return px / scaledDensity
}