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
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.starsoft.myandroidutil.R


// This File Created at 25.11.2020 13:37.

class MessageDialog: AppCompatDialogFragment() {

    companion object {
        val DIALOG_MESSAGE_TAG = "DialogMessage"
        val ICON_DIALOG_TAG = "DialogIcon"
        val BODY_MESSAGE_TAG = "DialogBody"
        val OK_BUTTON_NAME = "OKButtonName"
        val CANCEL_BUTTON_NAME = "CancelButtonName"
        val BODY_DIALOG_TAG = "BodyTag"
        var OKListener: ((DialogInterface, Int) -> Unit)? = null
            private set
        var cancelListener: ((DialogInterface, Int) -> Unit)? = null
            private set


        fun newInstance(
            bodyLayaut: Int?,
            message: String?,
            body: String?,
            okButtonName: String?,
            cancelButtonName: String?): MessageDialog {
            val dialog = MessageDialog()
            val args = Bundle()
            args.putInt(BODY_DIALOG_TAG, bodyLayaut ?: 0)
            args.putString(BODY_MESSAGE_TAG, body ?: "")
            args.putString(DIALOG_MESSAGE_TAG, message ?: "")
            args.putString(OK_BUTTON_NAME, okButtonName ?: "Ok")
            args.putString(CANCEL_BUTTON_NAME, cancelButtonName ?: "Cancel")
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireActivity())
        val inflater = requireActivity().layoutInflater
        if (requireArguments().getInt(BODY_DIALOG_TAG) != 0) {
            builder.setView(inflater.inflate(requireArguments().getInt(BODY_DIALOG_TAG), null))
        }
        builder.setTitle(requireArguments().getString(DIALOG_MESSAGE_TAG))
            .setMessage(requireArguments().getString(BODY_MESSAGE_TAG))
            .apply {
                if (requireArguments().getInt(ICON_DIALOG_TAG) != 0) {
                    setIcon(requireArguments().getInt(ICON_DIALOG_TAG))
                }
            }
            .setPositiveButton(requireArguments().getString(OK_BUTTON_NAME)) { dialog, which ->
                dialog.cancel()
                OKListener?.apply {
                    invoke(dialog, which)
                }
            }
            .setNegativeButton(requireArguments().getString(CANCEL_BUTTON_NAME)) { dialog, which ->
                dialog.cancel()
                cancelListener?.apply {
                    invoke(dialog, which)
                }
            }
        return builder.create()
    }

    fun setOKListener(listener: ((DialogInterface, Int) -> Unit)?): MessageDialog {
        OKListener = listener
        return this
    }

    fun setCancelListener(listener: ((DialogInterface, Int) -> Unit)?): MessageDialog {
        cancelListener = listener
        return this
    }
}