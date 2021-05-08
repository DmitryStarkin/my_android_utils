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

package com.starsoft.myandroidutil.linnkClickUtils

import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.text.util.Linkify
import android.view.MotionEvent
import android.widget.TextView
import java.util.regex.Pattern


/**
 * Created by Dmitry Starkin on 08.05.2021 13:35.
 */
abstract class TextViewLinkHandler : LinkMovementMethod() {
    override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_UP) return super.onTouchEvent(widget, buffer, event)
        var x = event.x.toInt()
        var y = event.y.toInt()
        x -= widget.totalPaddingLeft
        y -= widget.totalPaddingTop
        x += widget.scrollX
        y += widget.scrollY
        val off: Int =
            widget.layout.let { it.getOffsetForHorizontal(it.getLineForVertical(y), x.toFloat()) }
        val link = buffer.getSpans(off, off, URLSpan::class.java)
        if (!link.isNullOrEmpty()) {
            onLinkClick(link[0].url)
            return true
        }
        return false
    }
    abstract fun onLinkClick(link: String)
}

fun TextView.highlightLinks(pattern: Pattern) {
    Linkify.addLinks(this, pattern, null)
}