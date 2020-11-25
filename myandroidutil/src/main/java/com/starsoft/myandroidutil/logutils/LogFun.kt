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

package com.starsoft.myandroidutil.logutils

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.core.content.FileProvider
import com.starsoft.myandroidutil.fileutils.FileSaver
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*


// This File Created at 24.11.2020 14:36.

private const val FILE_EXTENSIONS = ".txt"
private const val FILE_NAME = "Log"
private const val SEND_FILE_NAME = "LogToSend.txt"
private val TIME_STAMP_PATTERN = "yyyy-MM_dd_HH-mm-ss-SSS"


fun Context.getLogFile(): File {
    return File(this.getLogsDir(), FILE_NAME + FILE_EXTENSIONS)
}

fun Context.generateLogFile(): File {

    val file = getLogFile()

    if (file.exists()) {
        return file
    } else {
        file.createNewFile()
    }
    return file
}

private fun Context.getLogsDir(): File {

    val dir = File(this.cacheDir.toString() + "/" + "logs" + "/")
    if (!dir.exists()) {
        dir.mkdirs()
    }
    return dir
}

fun Context.sendLog(perform: Boolean = true, eMails: Array<String> = arrayOf("t0506803080@gmail.com")) {

    val file = this.getLogFile()
    if (file.exists() && perform) {
        LogWriter.writeLogMessage("Log send",
            { LogWriter.blockLog = true
                LogWriter.reset()
                FileSaver.saveFromInputStream(
                    FileInputStream(file), SEND_FILE_NAME, FileSaver.DIRECTORY.CACH_LOGS,
                    onSusses = { f ->
                        LogWriter.blockLog = false
                        sendLogToEmail(eMails, f)
                    }, onError = { e ->
                        LogWriter.blockLog = false
                        e.printStackTrace()
                    })})
    }
}

fun Context.sendLogToEmail(eMails: Array<String> = arrayOf("t0506803080@gmail.com"), logFile: File) {

    if (logFile.exists()) {
        val time = SimpleDateFormat(TIME_STAMP_PATTERN, Locale.getDefault()).format(Date())
        val contentUri: Uri = FileProvider.getUriForFile(this, "io.getonekey.onekeyapp.fileprovider", logFile)
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "message/rfc822"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(Intent.EXTRA_STREAM, contentUri)
        intent.putExtra(Intent.EXTRA_EMAIL, eMails)
//        TODO Dmitry
        intent.putExtra(Intent.EXTRA_SUBJECT, "Dmitry")
        intent.putExtra(Intent.EXTRA_TEXT, "Log $time file in attachment")


        val packageManager = this.applicationContext.packageManager
        val matches: List<ResolveInfo> = packageManager.queryIntentActivities(intent, 0)

        if (matches.isNotEmpty()) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val choiser = Intent.createChooser(intent, "Send log witch")
            choiser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.applicationContext.startActivity(choiser);
        } else {
//            makeLongToast(App.instance.getApplicationContext(), "Impossible send this file")
        }
    }
}