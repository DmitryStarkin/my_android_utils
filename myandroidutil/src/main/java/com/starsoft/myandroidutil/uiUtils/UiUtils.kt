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
import android.graphics.Typeface
import android.os.Build
import android.os.IBinder
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.IntRange
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.starsoft.myandroidutil.R
import com.starsoft.myandroidutil.shimmer.Shimmer
import kotlin.random.Random
import com.starsoft.myandroidutil.shimmer.ShimmerDrawable
import com.starsoft.myandroidutil.stringext.applyStyleSpan

/**
 * Created by Dmitry Starkin on 08.05.2021 14:01.
 */

const val TRANSPARENT_COLOR = 0x00000000

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

@Deprecated(
    message = "this not work Use android.app.Activity.hideKeyboard() instead",
    replaceWith = ReplaceWith("requireActivity().hideKeyboard()", "com.starsoft.myandroidutil.uiUtils.hideKeyboard()")
)
fun View.hideKeyboard() =
    ViewCompat.getWindowInsetsController(this)?.hide(WindowInsetsCompat.Type.ime())

/**
 * Open keyboard on specific edit text using InputMethodManager
 */
@Deprecated(
    message = "Use showKeyboard() instead",
    replaceWith = ReplaceWith("showKeyboard()", "com.starsoft.myandroidutil.uiUtils.showKeyboard()")
)
fun android.app.Activity.showKeyboard(editText: EditText) {
    editText.requestFocusFromTouch()
    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .showSoftInput(editText, 0)
}

fun Activity.getRootView(): View {
    return findViewById<View>(android.R.id.content)
}


@Deprecated(
    message = "Use isKeyboardVisible() instead",
    replaceWith = ReplaceWith("isKeyboardVisible()", "com.starsoft.myandroidutil.uiUtils.isKeyboardVisible()")
)
fun Activity.isKeyboardOpen(): Boolean {
    val visibleBounds = Rect()
    this.getRootView().getWindowVisibleDisplayFrame(visibleBounds)
    val heightDiff = getRootView().height - visibleBounds.height()
    val marginOfError = convertDpToPixel(50F, this)
    return heightDiff > marginOfError
}


@Deprecated(
    message = "Use isKeyboardInVisible() instead",
    replaceWith = ReplaceWith("isKeyboardInVisible()", "com.starsoft.myandroidutil.uiUtils.isKeyboardInVisible()")
)
fun Activity.isKeyboardClosed(): Boolean {
    return !this.isKeyboardOpen()
}

fun Activity.isKeyboardVisible(): Boolean =
    getRootView().isKeyboardVisible()

fun Activity.isKeyboardInVisible(): Boolean =
    !isKeyboardVisible()


fun Fragment.isKeyboardVisible(): Boolean =
    try {
        requireActivity().getRootView().isKeyboardVisible()
    } catch (e: Throwable) {
        e.printStackTrace()
        false
    }

fun Fragment.isKeyboardInVisible(): Boolean =
    !isKeyboardVisible()

/**
 * Close keyboard using Insets
 */
fun Fragment.hideKeyboard() {
    try {
        requireActivity().hideKeyboard()
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

/**
 * Show keyboard using Insets
 */
fun Fragment.showKeyboard()  {
    try {
        requireActivity().showKeyboard()
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

/**
 * Close keyboard using Insets
 */
fun android.app.Activity.hideKeyboard() {
    WindowCompat.getInsetsController(window, window.decorView).apply {
        hide(WindowInsetsCompat.Type.ime())
    }
}

/**
 * Show keyboard using Insets
 */
fun android.app.Activity.showKeyboard() {
    WindowCompat.getInsetsController(window, window.decorView).apply {
        show(WindowInsetsCompat.Type.ime())
    }
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

fun View.createShimmerWitchColor(color: Int, corners: Int = 0): ShimmerDrawable =
    ShimmerDrawable().apply {
        setCorner(corners)
        shimmer = Shimmer.ColorHighlightBuilder()
            .setBaseColor(color)
            .setHighlightColor(color)
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setBaseAlpha(1f)
            .setHighlightAlpha(0.98f)
            .setDuration(500L)
            .setDropoff(0.8f)
            .setIntensity(0.9f)
            .setTilt(10f)
            .setShape(Shimmer.Shape.LINEAR)
            .setStartDelay(Random.nextLong(0L, 200L))
            .build()
        startShimmer()
    }


@RequiresApi(Build.VERSION_CODES.M)
fun View.createShimmer(@ColorRes color: Int? = null, corners: Int = 0): ShimmerDrawable {
    val baseColor = color?.let { this@createShimmer.context.getColor(it) }
        ?: this@createShimmer.context.getPrimaryColor()
    return this.createShimmerWitchColor(baseColor, corners)
}

fun Context.getPrimaryColor(): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
    return typedValue.data
}

fun RadioGroup.setTypefacesDependChecked(@IntRange(from = 0, to = Typeface.BOLD_ITALIC.toLong()) checkedFace: Int, @IntRange(from = 0, to = Typeface.BOLD_ITALIC.toLong()) unCheckedFace: Int){
    children.toList().forEach {
        if(it is AppCompatRadioButton){
            if(it.id == checkedRadioButtonId){
                it.text = it.text?.applyStyleSpan(StyleSpan(checkedFace))
            } else {
                it.text = it.text.applyStyleSpan(StyleSpan(unCheckedFace))
            }
        }
    }
    invalidate()
}

fun RadioGroup.setTypefacesDependChecked(checkedFace: Typeface, unCheckedFace: Typeface){
    children.toList().forEach {
        if(it is AppCompatRadioButton){
            if(it.id == checkedRadioButtonId){
                it.typeface = checkedFace
            } else {
                it.typeface = unCheckedFace
            }
        }
    }
    invalidate()
}

fun RadioGroup.getCheckedButton(): RadioButton? =
    children.toList().find {
        it is RadioButton && it.id == checkedRadioButtonId

    } as RadioButton?