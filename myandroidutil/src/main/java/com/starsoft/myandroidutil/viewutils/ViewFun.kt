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

package com.starsoft.myandroidutil.viewutils

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.transition.Transition
import androidx.transition.TransitionManager


// This File Created at 25.11.2020 13:00.
const val DEFAULT_ANIMATION_DELAY = 600L

fun View.animation(transition: Transition, duration: Long = DEFAULT_ANIMATION_DELAY){
    transition.duration = duration
    transition.addTarget(this)
    TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
}

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

fun View.changeVisibilityWithAnimation(visibility: Int, transition: Transition, duration: Long = DEFAULT_ANIMATION_DELAY){
    if(visibility != View.VISIBLE && visibility != View.GONE && visibility != View.GONE ) return
    this.animation(transition, duration)
    this.visibility = visibility
}