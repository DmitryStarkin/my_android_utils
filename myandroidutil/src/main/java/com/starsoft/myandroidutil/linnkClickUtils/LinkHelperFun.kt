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

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.text.util.Linkify
import android.view.MotionEvent
import android.widget.TextView
import com.starsoft.myandroidutil.R
import java.util.regex.Pattern


/**
 * Created by Dmitry Starkin on 08.05.2021 13:35.
 */

const val  EMPTY_STRING = ""

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

fun String.extractProtocol(): String {
    return Regex(WEB_PROTOCOL_REGEX_STRING).find(this)?.value ?: ""
}

fun Context.openWebLink(link: String, choicerMessage: String = EMPTY_STRING) {

    val linkToOpen = if (link.extractProtocol().isEmpty()) {
        "$LINK_PREFIX$link"
    } else {
        link
    }
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkToOpen))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // needed for call Activity using context outside other Activity
    this.packageManager?.apply {
        intent.resolveActivity(this)?.apply {
            try {
                this@openWebLink.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                this@openWebLink.startActivity(
                        Intent.createChooser(
                                intent,
                                choicerMessage
                        )
                )
            }
        }
    }
}

fun Context.sendEmail(eMails: Array<String>, fields: EmailsField = EmailsField() ) {
    if(eMails.isNotEmpty()) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "message/rfc822"
        intent.putExtra(Intent.EXTRA_EMAIL, eMails)
//        TODO Dmitry
        intent.putExtra(Intent.EXTRA_SUBJECT, fields.sender)
        intent.putExtra(Intent.EXTRA_TEXT, fields.caption)

        val packageManager = this.applicationContext.packageManager
        val matches: List<ResolveInfo> = packageManager.queryIntentActivities(intent, 0)

        if (matches.isNotEmpty()) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val choicer = Intent.createChooser(intent, fields.choicerMessage)
            choicer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.applicationContext.startActivity(choicer);
        } else if (fields.reserveLink.isNotEmpty()) {
            this.openWebLink(fields.reserveLink)
        }
    }
}

data class EmailsField(
        val choicerMessage: String = EMPTY_STRING,
        val sender : String = EMPTY_STRING,
        val caption : String = EMPTY_STRING,
        val reserveLink: String = EMPTY_STRING
)