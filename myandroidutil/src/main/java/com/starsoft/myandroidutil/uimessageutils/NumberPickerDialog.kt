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

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment


// This File Created at 25.11.2020 14:42.

class NumberPickerDialog : AppCompatDialogFragment() {
    companion object {
        val DIALOG_MESSAGE_TAG = "NumberPickerDialog"
        val ICON_DIALOG_TAG = "DialogIcon"
        val MIN_VALUE_TAG = "min"
        val MAX_VALUE_TAG = "max"
        val CURRENT_VALUE_TAG = "current"
        val OK_BUTTON_NAME = "OKButtonName"
        val CANCEL_BUTTON_NAME = "CancelButtonName"
        var picker: NumberPicker? = null
        var mListener: ((DialogInterface, Int, Int) -> Unit)? = null
            private set
        var keepListener = false
            private set

        fun newInstance(
            message: String, minValue: Int, maxValue: Int, currentValue: Int,
            okButtonName: String?,
            cancelButtonName: String?
        ): NumberPickerDialog {
            val dialog = NumberPickerDialog()
            val args = Bundle()
            args.putInt(MIN_VALUE_TAG, minValue)
            args.putInt(MAX_VALUE_TAG, maxValue)
            args.putInt(CURRENT_VALUE_TAG, currentValue)
            args.putString(DIALOG_MESSAGE_TAG, message)
            args.putString(MessageDialog.OK_BUTTON_NAME, okButtonName ?: "Ok")
            args.putString(MessageDialog.CANCEL_BUTTON_NAME, cancelButtonName ?: "Cancel")
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        picker = NumberPicker(activity!!)
        picker?.setMaxValue(
            arguments!!.getInt(
                MAX_VALUE_TAG
            )
        )
        picker?.setMinValue(
            arguments!!.getInt(
                MIN_VALUE_TAG
            )
        )
        picker?.setValue(
            arguments!!.getInt(
                CURRENT_VALUE_TAG
            )
        )
        picker?.setWrapSelectorWheel(false)
        return AlertDialog.Builder(activity!!).setTitle(arguments!!.getString(DIALOG_MESSAGE_TAG))
            .setIcon(arguments!!.getInt(ICON_DIALOG_TAG))
            .setPositiveButton(arguments!!.getString(OK_BUTTON_NAME)) { dialog, which ->
                dialog.cancel()
                mListener?.apply {
                    invoke(
                        dialog, which, picker?.value ?: arguments!!.getInt(
                            CURRENT_VALUE_TAG
                        )
                    )
                }
            }
            .setNegativeButton(arguments!!.getString(CANCEL_BUTTON_NAME)) { dialog, which -> dialog.cancel() }
            .setView(picker)
            .create()
    }

    override fun onStart() {
        super.onStart()
        mListener ?: this.dismiss()
    }

    override fun onStop() {
        super.onStop()
        if (!keepListener) {
            mListener = null
        }
    }

    fun setListener(
        listener: (DialogInterface, Int, Int) -> Unit,
        keepListener: Boolean = false
    ): NumberPickerDialog {
        mListener = listener
        return this
    }

    interface OnDialogPickerValueChange : (DialogInterface, Int, Int) -> Unit {
        override fun invoke(p1: DialogInterface, p2: Int, p3: Int) {
            OnValueSet(p1, p2, p3)
        }

        fun OnValueSet(dialog: DialogInterface, witch: Int, value: Int)
    }
}