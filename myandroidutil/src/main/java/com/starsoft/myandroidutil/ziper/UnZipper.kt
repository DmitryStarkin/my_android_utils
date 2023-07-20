/*
 * Copyright (c) 2023. Dmitry Starkin Contacts: t0506803080@gmail.com
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

package com.starsoft.myandroidutil.ziper

import android.net.Uri
import androidx.annotation.WorkerThread
import com.starsoft.myandroidutil.UriUyils.getTemporaryFileFromUri
import com.starsoft.myandroidutil.fileutils.DIRECTORY
import com.starsoft.myandroidutil.fileutils.addFileFromStream
import com.starsoft.myandroidutil.fileutils.getMyCacheDir
import com.starsoft.myandroidutil.fileutils.getSubDir
import com.starsoft.myandroidutil.providers.mainContext
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * Created by Dmitry Starkin on 17.03.2023 12:36.
 */

private const val ZIP_FILE_SUFFIX = ".szf"
private const val ZIP_FILE_PREFIX = "temp"

@WorkerThread
fun Uri.unZipToCache(directory: DIRECTORY) {
    unZipToDir(mainContext.getMyCacheDir(directory))
}

@WorkerThread
fun File.unZipToCache(directory: DIRECTORY) {
    unZipToDir(mainContext.getMyCacheDir(directory))
}

@WorkerThread
fun Uri.unZipToDir(dir: File) = mainContext.getTemporaryFileFromUri(this, ZIP_FILE_PREFIX, ZIP_FILE_SUFFIX).unZipToDir(dir)

@WorkerThread
fun File.unZipToDir(dir: File){
    if(!dir.isDirectory){
        throw Exception("must be an directory")
    } else {
        ZipFile(this).use {zipFile ->
            val zippedFiles = ArrayList<ZipEntry>()
            zipFile.entries().iterator().forEach { entry ->
                if (entry.isDirectory) {
                    dir.getSubDir(entry.name)
                } else {
                    zippedFiles.add(entry)
                }
            }
            zippedFiles.forEach{zipEntry ->
                dir.addFileFromStream(zipFile.getInputStream(zipEntry), zipEntry.name)
            }
        }
    }
}