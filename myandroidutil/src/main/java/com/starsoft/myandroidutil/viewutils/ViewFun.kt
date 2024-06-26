/*
 * Copyright (c) 2020. Dmitry Starkin Contacts: t0506803080@gmail.com
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

@file:JvmName("ViewHelper")

package com.starsoft.myandroidutil.viewutils

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.starsoft.myandroidutil.uiUtils.isKeyboardVisible


/**
 * Created by Dmitry Starkin at 25.11.2020 13:00.
 */

private const val REQUEST_LAYOUT_MAX_TIME = 5000L
private const val REQUEST_LAYOUT_DELAY = 30L
private const val REQUEST_LAYOUT_ATTEMPTS = (REQUEST_LAYOUT_MAX_TIME / REQUEST_LAYOUT_DELAY).toInt()

private val mainHandler = Handler(Looper.getMainLooper())

fun View.invokeAfterLayout(attempt: Int = 0, lambda: () -> Unit) {
    if (isAttachedToWindow && !isLayoutRequested && !isInLayout) {
        try {
            lambda.invoke()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    } else {
        if (attempt < REQUEST_LAYOUT_ATTEMPTS) {
            mainHandler.postDelayed({
                invokeAfterLayout(attempt + 1, lambda)
            }, REQUEST_LAYOUT_DELAY)
        }
    }
}

fun Fragment.invokeAfterKeyboardOpened(lambda: () -> Unit) {
    try {
        requireActivity().invokeAfterKeyboardOpened(0, lambda)
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

fun Activity.invokeAfterKeyboardOpened(attempt: Int = 0, lambda: () -> Unit) {
    if (isKeyboardVisible()) {
        try {
            lambda.invoke()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    } else {
        if (attempt < REQUEST_LAYOUT_ATTEMPTS) {
            mainHandler.postDelayed({
                invokeAfterKeyboardOpened(attempt + 1, lambda) }, REQUEST_LAYOUT_DELAY)
        }
    }
}

@JvmOverloads
@SuppressLint("RestrictedApi")
fun PopupMenu.showWitchHelper(anchor: View, theme: Int? = null){
    val menuHelper = if(theme == null){
        MenuPopupHelper(anchor.context, this.getMenu() as MenuBuilder,anchor)
    } else {
        val wrapper = ContextThemeWrapper(anchor.context, theme)
        MenuPopupHelper(wrapper, this.getMenu() as MenuBuilder,anchor)
    }
    menuHelper.setForceShowIcon(true)
    menuHelper.gravity = Gravity.END
    menuHelper.show()
}

