/*
 * Copyright (c) 2022. Dmitry Starkin Contacts: t0506803080@gmail.com
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

package com.starsoft.myandroidutil.UriUyils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.annotation.WorkerThread
import com.starsoft.myandroidutil.fileutils.DIRECTORY
import com.starsoft.myandroidutil.fileutils.getMyCacheDir
import com.starsoft.myandroidutil.sharingUtils.*
import com.starsoft.myandroidutil.stringext.EMPTY_STRING
import java.io.File
import java.io.FileOutputStream


/**
 * Created by Dmitry Starkin on 06.07.2022 12:05.
 */

private const val URI_SCHEME_FILE ="file"

@Suppress("BlockingMethodInNonBlockingContext")
@WorkerThread
fun Context.getTemporaryFileFromUri(source: Uri, filePrefix: String, fileSuffix: String, directory: DIRECTORY = DIRECTORY.CACHE_FILES): File {
    return File.createTempFile(
        filePrefix,
        System.currentTimeMillis().toString() + fileSuffix,
        this@getTemporaryFileFromUri.getMyCacheDir(directory)
    ).also { file ->
        FileOutputStream(file).use { out ->
            this@getTemporaryFileFromUri.contentResolver.openInputStream(source)?.use{input ->
                input.copyTo(out)
                out.flush()
            }
        }
        file.deleteOnExit()
    }
}

@WorkerThread
fun Context.getFileDataFromUri(uri: Uri): FileData? {
    return this@getFileDataFromUri.contentResolver.query(uri, null, null, null, null)?.use { cursor ->

        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        cursor.moveToFirst()
        FileData(cursor.getString(nameIndex), cursor.getLong(sizeIndex))
    }
}

/**
 * use it only if you have rights to  the file from URI
 */

fun Uri?.getFile(): File? =
    this?.path?.let{
        if(it.isNotEmpty() && this.scheme == URI_SCHEME_FILE){
            File(it)
        } else {
            null
        }
    }


fun Context.getUriTypeAsExtension(uri: Uri): String {
    val result = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
        val mime = MimeTypeMap.getSingleton()
        mime.getExtensionFromMimeType(this.contentResolver.getType(uri)) ?: EMPTY_STRING
    } else {
        uri.getFile()?.name?.substringAfterLast(".") ?: EMPTY_STRING
    }
    return if(result.isNotEmpty()){
        ".$result"
    } else {
        result
    }
}

fun Context.getUriMMEType(uri: Uri, defaultMmeType: String = MME_TYPE_UNKNOWN): String {
    val mime = MimeTypeMap.getSingleton()
    return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
        this.contentResolver.getType(uri) ?: defaultMmeType
    } else {
        mime.getMimeTypeFromExtension(uri.getFile()?.name?.substringAfterLast(".")) ?: defaultMmeType
    }
}

fun Context.getFileNameFromUri(uri: Uri?): String? =
    uri?.let{ur ->
        contentResolver.query(ur, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            .use {cursor ->
                if (cursor == null || !cursor.moveToFirst()) {
                    return null
                }

                val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (columnIndex == -1) {
                    return null
                }

                return cursor.getString(columnIndex)
            }
    }

data class FileData(val name: String, val length: Long)