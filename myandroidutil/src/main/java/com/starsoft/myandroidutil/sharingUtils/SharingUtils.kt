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

package com.starsoft.myandroidutil.sharingUtils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import androidx.annotation.WorkerThread
import androidx.core.content.FileProvider
import com.starsoft.myandroidutil.providers.ContextProvider
import java.io.File
import java.io.FileOutputStream


/**
 * Created by Dmitry Starkin on 08.05.2021 13:05.
 */
private val APPLICATION_ID = ContextProvider.context.packageName.toString()
const val  MME_TYPE_TEXT = "text/plain"

enum class DIRECTORY(val directoryName: String) {
    CACHE_IMAGES("/images/"),
    CACHE_FILES("/files/"),
    CACHE_LOGS("/logs/"),
    EXTERNAL_DOCUMENTS(ContextProvider.context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString())
}

fun Context.getMyCacheDir(dir: DIRECTORY): File {

    val directory = File(this.cacheDir.toString() + dir.directoryName)
    if (!directory.exists()) {
        directory.mkdirs()
    }
    return directory
}

fun Context.shareFile(file: File, mmeType: String, description: String, chooserMessage: String) {

    val contentUri: Uri =
        FileProvider.getUriForFile(this, APPLICATION_ID + ".fileprovider", file)
    val intent = Intent()
    intent.action = Intent.ACTION_SEND
    intent.type = mmeType
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.putExtra(Intent.EXTRA_TEXT, description)
    intent.putExtra(Intent.EXTRA_STREAM, contentUri)
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_TASK)


    val chooser = Intent.createChooser(intent, chooserMessage)
    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    this.startActivity(chooser)

}

fun Context.shareText(text: String, chooserMessage: String) {

    val intent = Intent()
    intent.action = Intent.ACTION_SEND
    intent.type = MME_TYPE_TEXT
    intent.putExtra(Intent.EXTRA_TEXT, text)
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_TASK)

    val chooser = Intent.createChooser(intent, chooserMessage)
    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    this.startActivity(chooser)
}

@Suppress("BlockingMethodInNonBlockingContext")
        /**
         * this is blocking call
         */
@WorkerThread
fun Context.getTempImageFile(
    image: Bitmap?,
    format: Bitmap.CompressFormat,
    quality: Int,
    filePrefix: String,
    fileExtension: String
): File {

    return File.createTempFile(
            filePrefix,
            System.currentTimeMillis().toString() + fileExtension,
            this@getTempImageFile.getMyCacheDir(DIRECTORY.CACHE_IMAGES)
        ).also { file ->
            FileOutputStream(file).use { out ->
                image?.compress(format, quality, out)
                out.flush()
            }
            file.deleteOnExit()
        }
}

@Suppress("BlockingMethodInNonBlockingContext")
        /**
         * this is blocking call
         */
@WorkerThread
fun Context.getTemporaryFileFromUri(source: Uri, filePrefix: String): File {
    return File.createTempFile(
            filePrefix,
            System.currentTimeMillis().toString(),
            this@getTemporaryFileFromUri.getMyCacheDir(DIRECTORY.CACHE_FILES)
        ).also { file ->
            FileOutputStream(file).use { out ->
                this@getTemporaryFileFromUri.contentResolver.openInputStream(source)?.copyTo(out)
                out.flush()
            }
            file.deleteOnExit()
        }

}