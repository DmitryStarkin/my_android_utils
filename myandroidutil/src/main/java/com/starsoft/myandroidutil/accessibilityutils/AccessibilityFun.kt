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

@file:JvmName("AccessibilityHelper")

package com.starsoft.myandroidutil.accessibilityutils

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.graphics.Region
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import com.starsoft.myandroidutil.logutils.d
import com.starsoft.myandroidutil.providers.mainContext
import com.starsoft.myandroidutil.refutils.isInstanceOrExtend
import com.starsoft.myandroidutil.stringext.insertTo


// This File Created at 26.11.2020 13:59.

fun AccessibilityNodeInfo.click(): Boolean {
    if (this.supportAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK) && this.isClickable) {
        return this.performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }
    val parent = this.parent
    return parent?.click() ?: false
}

fun AccessibilityNodeInfo.insertText(text: String): Boolean {

    if (!this.supportAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_TEXT) || !this.isEnabled) return false
    this.refresh()
    val cursorPosition = this.textSelectionStart
    if (cursorPosition == -1) {

        return this.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, Bundle().apply {
            putCharSequence(
                    AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    text
            )
        })
    }
    val oldText = (this.text ?: "").toString()

    return this.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, Bundle().apply {
        putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                oldText.insertTo(cursorPosition, text)
        )
    })
}

@JvmOverloads
fun AccessibilityNodeInfo.setText(arguments: Bundle? = null): Boolean {
    if (!this.supportAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_TEXT) || !this.isEnabled) return false
    return this.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
}

fun AccessibilityNodeInfo.clearText() {
    this.setText()
}

fun AccessibilityNodeInfo.changeText(text: CharSequence): Boolean {
    this.refresh()
    return setText(Bundle().apply {
        putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                text
        )
    })
}

fun AccessibilityNodeInfo.supportAction(action: AccessibilityNodeInfo.AccessibilityAction): Boolean {

    for (_action in this.actionList) {
        if (_action == action) {
            return true
        }
    }
    return false
}

fun AccessibilityNodeInfo.getTextFrom(): CharSequence {

    try {
        if (this.className?.let {
                Class.forName(it.toString())
                    .isInstanceOrExtend(TextView::class.java)
            } == true
        ) {
            return (this.text ?: "")
        }
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }
    return ""
}

/**
 * helper for get AccessibilityManager
 * @return AccessibilityManager
 */
fun getAccessibilityManager(): AccessibilityManager {
    return mainContext.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
}

/**
 * checks whether the TouchExploration is enabled
 * @return true if enabled false otherwise
 */
fun isTouchExplorationEnabled(): Boolean {
    return getAccessibilityManager().isTouchExplorationEnabled
}

/**
 * checks whether the accessibility service is enabled
 * @param serviceId accessibility service ID
 * @return true if enabled false otherwise
 */
fun isAccessibilityServiceEnabled(serviceId: String): Boolean {
    for (info in getAccessibilityManager().getEnabledAccessibilityServiceList(
            AccessibilityEvent.TYPES_ALL_MASK
    )) {
        if (serviceId == info.id) {
            return true
        }
    }
    return false
}

fun Context.cancelBypassTouchExploration(service: AccessibilityService): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        display?.let {
            service.cancelBypassTouchExploration(
                it.displayId
            )
        } ?: false
    } else {
        false
    }


fun Context.bypassTouchExploration(service: AccessibilityService): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this@bypassTouchExploration.display?.let {
                val displaySize = Point()
                it.getRealSize(displaySize)
                service.bypassTouchExploration(
                    it.displayId,
                    displaySize
                )
            } ?: false

    } else {false}




/**
 * set  Bypass Touch Exploration in screen region
 * @param displaySize screen region for Bypass Touch Exploration
 * @return true if success false otherwise
 */
@SuppressLint("NewApi")
fun AccessibilityService.bypassTouchExploration(displayId: Int, displaySize: Point): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
        isTouchExplorationEnabled() &&
        isAccessibilityServiceEnabled(this.serviceInfo.id)
    ) {
        val touchRegion = Region(0, 0, displaySize.x, displaySize.y)
            setTouchExplorationPassthroughRegion(displayId, touchRegion)
            return true

    }
    return false
}

/**
 * cancel Bypass Touch Exploration
 *
 * @return true if success false otherwise
 */
@SuppressLint("NewApi")
fun AccessibilityService.cancelBypassTouchExploration(displayId: Int): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
        isTouchExplorationEnabled() &&
        isAccessibilityServiceEnabled(this.serviceInfo.id)
    ) {
            setTouchExplorationPassthroughRegion(displayId, Region())
            return true
    }
    return false
}

/**
 * forces Acceptability to say the views text  if TouchExploration is enabled
 * @param view view to say
 */
fun announceTextFromView(view: View?) {
    if (view != null && view.visibility == View.VISIBLE && view is TextView && isTouchExplorationEnabled()) {
        val event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_ANNOUNCEMENT)
        event.className = view.javaClass.name
        event.packageName = view.getContext().packageName
        event.isEnabled = true
        event.text.add(view.text)
        getAccessibilityManager().sendAccessibilityEvent(event)
    }
}