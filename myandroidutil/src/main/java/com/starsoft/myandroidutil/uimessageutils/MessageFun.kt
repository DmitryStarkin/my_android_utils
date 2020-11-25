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

package com.starsoft.myandroidutil.uimessageutils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.starsoft.myandroidutil.R


// This File Created at 25.11.2020 13:38.

private val DIALOG_TAG: String = "dialogTag"

fun Context.makeShortToast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.makeLongToast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun View.makeSnackBarMessage(
    message: String,
    isError: Boolean = false,
    duration: Int? = null,
    action: ((View) -> Unit)? = null
) {
    val bar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    duration?.apply { bar.setDuration(this) }
    action?.apply { bar.setAction(R.string.button_ok, action) }
    if (isError) {
        bar.applyErrorStyle()
    }
    bar.show()
}

fun AppCompatActivity.makeDialogMessage(
    bodyLayout: Int? = null,
    message: String? = null,
    body: String? = null,
    okButtonName: String? = null,
    cancelButtonName: String? = null,
    OkListener: ((DialogInterface, Int) -> Unit)? = null,
    cancelListener: ((DialogInterface, Int) -> Unit)? = null
) {
    MessageDialog.newInstance(bodyLayout, message, body, okButtonName, cancelButtonName)
        .setOKListener(OkListener)
        .setCancelListener(cancelListener)
        .show(this.supportFragmentManager, DIALOG_TAG)
}

fun makePickerDialog(
    activity: AppCompatActivity,
    message: String,
    minValue: Int,
    maxValue: Int,
    currentValue: Int,
    okButtonName: String? = null,
    cancelButtonName: String? = null,
    listener: (DialogInterface, Int, Int) -> Unit
) {
    NumberPickerDialog.newInstance(
        message,
        minValue,
        maxValue,
        currentValue,
        okButtonName,
        cancelButtonName
    ).setListener(listener)
        .show(activity.supportFragmentManager, DIALOG_TAG)
}

/**
 * Create uncancelebale [AlertDialog] with specified layout res
 *
 * @param layoutId - id of layout res with dialog UI
 */
fun Context.createDialog(@LayoutRes layoutId: Int): AlertDialog? = AlertDialog.Builder(this)
    .setView(layoutId).create().apply {
        setCanceledOnTouchOutside(false)
        setCancelable(true)
    }

/**
 * Add red bg to [Snackbar]
 * Add error icon to Snackbar text
 */
fun Snackbar.applyErrorStyle(): Snackbar = this.also {
    view.setBackgroundColor(ContextCompat.getColor(view.context, android.R.color.holo_red_light))
    view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).also {
        it.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error_outline, 0, 0, 0)
        it.compoundDrawablePadding = 10
    }
}