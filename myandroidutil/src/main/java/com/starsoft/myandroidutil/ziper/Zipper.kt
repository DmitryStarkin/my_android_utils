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

import android.content.Context
import android.net.Uri
import androidx.annotation.IntRange
import androidx.annotation.WorkerThread
import com.starsoft.myandroidutil.providers.mainContext
import com.starsoft.myandroidutil.sharingUtils.DIRECTORY
import com.starsoft.myandroidutil.sharingUtils.getMyCacheDir
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.Deflater.DEFAULT_COMPRESSION
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Created by Dmitry Starkin on 12.06.2023 11:08.
 */

private const val MODE_WRITE = "w"
private const val MODE_READ = "r"

@WorkerThread
fun File.zipCache(directory: DIRECTORY) {
    zipDir(mainContext.getMyCacheDir(directory))
}

@WorkerThread
fun Uri.zipCache(directory: DIRECTORY) {
    zipDir(mainContext.getMyCacheDir(directory))
}

@WorkerThread
fun File.zipDir(dir: File){
    if(!dir.isDirectory){
        throw Exception("must be an directory")
    } else {
        dir.listFiles()?.toList()?.let { zipFiles(it.toList()) }
    }
}

@WorkerThread
fun Uri.zipDir(dir: File){
    if(!dir.isDirectory){
        throw Exception("must be an directory")
    } else {
        dir.listFiles()?.toList()?.let { zipFiles(it.toList()) }
    }
}

@WorkerThread
fun File.zipFiles(files: List<File>, @IntRange(from = -1, to = 9) compression: Int = DEFAULT_COMPRESSION) {
    ZipOutputStream(BufferedOutputStream(FileOutputStream(this))).also{it.setLevel(compression)}.use { outStream ->
        outStream.zipFiles(files)
    }
}

@WorkerThread
fun Uri.zipFiles(files: List<File>, @IntRange(from = -1, to = 9) compression: Int = DEFAULT_COMPRESSION) {
    mainContext.contentResolver.openFileDescriptor(this, MODE_WRITE).use { descriptor ->
        descriptor?.fileDescriptor?.let {
            ZipOutputStream(BufferedOutputStream(FileOutputStream(it))).also{it.setLevel(compression)}.use { outStream ->
                outStream.zipFiles(files)
            }
        }
    }
}


@WorkerThread
fun Context.zipFiles(zipFile: Uri, files: List<File>, @IntRange(from = -1, to = 9) compression: Int = DEFAULT_COMPRESSION) {
    contentResolver.openFileDescriptor(zipFile, MODE_WRITE).use { descriptor ->
        descriptor?.fileDescriptor?.let {
            ZipOutputStream(BufferedOutputStream(FileOutputStream(it))).also{stream ->
                stream.setLevel(compression)}.use { outStream ->
                outStream.zipFiles(files)
            }
        }
    }
}

private fun ZipOutputStream.zipFiles(files: List<File>) {
    files.forEach { file ->
        putNextEntry(ZipEntry(file.name))
        BufferedInputStream(FileInputStream(file)).use { inStream ->
            inStream.copyTo(this)
        }
    }
}
