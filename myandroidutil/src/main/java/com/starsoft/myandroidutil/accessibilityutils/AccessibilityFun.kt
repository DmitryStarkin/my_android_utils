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

package com.starsoft.myandroidutil.accessibilityutils

import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.EditText
import android.widget.TextView
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