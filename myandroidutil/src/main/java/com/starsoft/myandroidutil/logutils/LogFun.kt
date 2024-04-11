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

@file:JvmName("LogHelper")

package com.starsoft.myandroidutil.logutils

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.util.Log
import androidx.annotation.MainThread
import androidx.core.content.FileProvider.getUriForFile
import com.starsoft.myandroidutil.fileutils.DIRECTORY
import com.starsoft.myandroidutil.fileutils.FileSaver
import com.starsoft.myandroidutil.fileutils.getMyCacheDir
import com.starsoft.myandroidutil.fileutils.writeFromStream
import com.starsoft.myandroidutil.providers.ContextProvider
import com.starsoft.myandroidutil.providers.mainContext
import com.starsoft.myandroidutil.refutils.getBuildConfigValue
import com.starsoft.myandroidutil.uimessageutils.makeLongToast
import com.starsoft.simpleandroidasynclibrary.core.launch
import com.starsoft.simpleandroidasynclibrary.executors.newSingleThreadPool
import com.starsoft.simpleandroidasynclibrary.executors.preinstal.base.threadpools.SingleThreadPool
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*


// This File Created at 24.11.2020 14:36.

private const val FILE_EXTENSIONS = ".txt"
private const val FILE_NAME = "Log"
private const val SEND_FILE_NAME = "LogToSend.txt"
private const val TIME_STAMP_PATTERN = "yyyy-MM_dd_HH-mm-ss-SSS"
private val APPLICATION_ID get() = ContextProvider.context.packageName

private val executor: SingleThreadPool by lazy { newSingleThreadPool() }

fun Context.getLogFile(): File {
    return File(this.getMyCacheDir(DIRECTORY.CACHE_LOGS), FILE_NAME + FILE_EXTENSIONS)
}

fun getLogFile(): File {
    return mainContext.getLogFile()
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

fun generateLogFile(): File {
    return mainContext.generateLogFile()
}

@MainThread
@JvmOverloads
fun Context.saveLogToFile(file: File, onSusses: (File?) -> Unit, onError: ((Throwable) -> Unit)? = null){
    val logFile = this.getLogFile()
    if (!logFile.exists()){
        onSusses(null)
        return
    }
    try{
        if(file.exists()){
            file.delete()
        }
        file.createNewFile()
    } catch (e: Throwable){
        e.printStackTrace()
        onError?.invoke(e)
        return
    }
    LogWriter.blockLog = true
    LogWriter.reset()
    executor.launch({ f ->
        if(logFile.delete()){
            LogWriter.blockLog = false
            LogWriter.reWriteCaption()
        } else {
            LogWriter.blockLog = false
        }
        onSusses(f) },
        { e ->
            LogWriter.blockLog = false
            LogWriter.writeLogMessage("Log error $e")
            onError?.invoke(e) ?: throw (e) })
        {
            file.writeFromStream(FileInputStream(logFile))
        }

}

@MainThread
@JvmOverloads
fun Context.sendLogToEmails(perform: Boolean = true, eMails: Array<String> = arrayOf("t0506803080@gmail.com"), sendError: (File) -> Unit = {})  {
    val file = this.getLogFile()
    if (file.exists() && perform) {
        LogWriter.writeLogMessage("Log send",
            {
                LogWriter.blockLog = true
                LogWriter.reset()
                val fileToSend = File(getMyCacheDir(DIRECTORY.CACHE_LOGS), SEND_FILE_NAME)
                saveLogToFile(fileToSend, {file ->
                    file?.apply {
                        if(!sendFileToEmail(eMails, this)){
                            sendError.invoke(file)
                        }
                    }
                },{e ->
                    LogWriter.blockLog = false
                    LogWriter.writeLogMessage("Log send error $e")
                    e.printStackTrace()
                })
            }
        )
    }
}

@Deprecated(
    message = "Use sendLogToEmails() instead",
    replaceWith = ReplaceWith("sendLogToEmails()", "com.starsoft.myandroidutil.logutils.sendLogToEmails()")
)
@MainThread
@JvmOverloads
fun Context.sendLog(perform: Boolean = true, eMails: Array<String> = arrayOf("t0506803080@gmail.com")) {
    val file = this.getLogFile()
    if (file.exists() && perform) {
        LogWriter.writeLogMessage("Log send",
            { LogWriter.blockLog = true
                LogWriter.reset()
                FileSaver.saveFromInputStream(
                    FileInputStream(file), SEND_FILE_NAME, DIRECTORY.CACHE_LOGS,
                    onSusses = { f ->
                        if(file.delete()){
                        LogWriter.blockLog = false
                        LogWriter.reWriteCaption()
                        } else{
                            LogWriter.blockLog = false
                        }
                        sendFileToEmail(eMails, f)
//                        TODO need best solution
                        f.deleteOnExit()
                    }, onError = { e ->
                        LogWriter.blockLog = false
                        LogWriter.writeLogMessage("Log send error $e")
                        e.printStackTrace()
                    })})
    }
}

@Deprecated(
    message = "Use sendLogToEmails() instead",
    replaceWith = ReplaceWith("sendLogToEmails()", "com.starsoft.myandroidutil.logutils.sendLogToEmails()")
)
@MainThread
@JvmOverloads
fun sendLog(perform: Boolean = true, eMails: Array<String> = arrayOf("t0506803080@gmail.com")) {
    mainContext.sendLog(perform, eMails)
}

@MainThread
@JvmOverloads
fun sendLogToEmails(perform: Boolean = true, eMails: Array<String> = arrayOf("t0506803080@gmail.com"), sendError: (File) -> Unit = {}) {
    mainContext.sendLogToEmails(perform, eMails, sendError)
}

@JvmOverloads
fun Context.sendFileToEmail(eMails: Array<String> = arrayOf("t0506803080@gmail.com"), fileToSend: File): Boolean {

    if (fileToSend.exists()) {
        val time = SimpleDateFormat(TIME_STAMP_PATTERN, Locale.getDefault()).format(Date())
        val contentUri: Uri = try {
            Log.d("Utils", "Current app $APPLICATION_ID.fileprovider")
             getUriForFile(this, "$APPLICATION_ID.fileprovider", fileToSend)
        } catch (e :Throwable){
            e.printStackTrace()
            this.makeLongToast("no permissions to access to the file")
            return false
        }
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "message/rfc822"
        intent.putExtra(Intent.EXTRA_STREAM, contentUri)
        intent.putExtra(Intent.EXTRA_EMAIL, eMails)
//        TODO Dmitry
        intent.putExtra(Intent.EXTRA_SUBJECT, "Dmitry")
        intent.putExtra(Intent.EXTRA_TEXT, fileToSend.name + " $time file in attachment")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val packageManager = this.applicationContext.packageManager
        val matches: List<ResolveInfo> = packageManager.queryIntentActivities(intent, 0)

        if (matches.isNotEmpty()) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val choicer = Intent.createChooser(intent, "Send log witch")
            choicer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.applicationContext.startActivity(choicer);
            return true
        } else {
            this.makeLongToast("Impossible send this file")
            return false
        }
    } else {
        this.makeLongToast("File not exists")
        return false
    }
}

@JvmOverloads
fun sendFileToEmail(eMails: Array<String> = arrayOf("t0506803080@gmail.com"), fileToSend: File) {
    mainContext.sendFileToEmail(eMails, fileToSend)
}