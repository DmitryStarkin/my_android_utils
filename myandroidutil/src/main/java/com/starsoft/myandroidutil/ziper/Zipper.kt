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
import com.starsoft.myandroidutil.UriUyils.getFileNameFromUri
import com.starsoft.myandroidutil.fileutils.DIRECTORY
import com.starsoft.myandroidutil.fileutils.getMyCacheDir
import com.starsoft.myandroidutil.providers.mainContext
import com.starsoft.myandroidutil.stringext.EMPTY_STRING
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

internal const val SEPARATOR = "/"
private const val MODE_WRITE = "w"
private const val MODE_REWRITE = "rwt"
private const val MODE_READ = "r"

@WorkerThread
fun File.zipCache(directory: DIRECTORY, @IntRange(from = -1, to = 9) compression: Int = DEFAULT_COMPRESSION, renameMap: HashMap<String, String>? = null) {
    zipDir(mainContext.getMyCacheDir(directory),compression, renameMap)
}

@WorkerThread
fun Uri.zipCache(directory: DIRECTORY, @IntRange(from = -1, to = 9) compression: Int = DEFAULT_COMPRESSION, renameMap: HashMap<String, String>? = null) {
    zipDir(mainContext.getMyCacheDir(directory),compression, renameMap)
}

@WorkerThread
fun File.zipDir(dir: File, @IntRange(from = -1, to = 9) compression: Int = DEFAULT_COMPRESSION, renameMap: HashMap<String, String>? = null){
    if(!dir.isDirectory){
        throw Exception("must be an directory")
    } else {
        dir.listFiles()?.toList()?.let { zipFiles(it.toList(), dir, compression, renameMap) }
    }
}

@WorkerThread
fun Uri.zipDir(dir: File, @IntRange(from = -1, to = 9) compression: Int = DEFAULT_COMPRESSION, renameMap: HashMap<String, String>? = null){
    if(!dir.isDirectory){
        throw Exception("must be an directory")
    } else {
        dir.listFiles()?.toList()?.let { zipFiles(it.toList(),compression, renameMap) }
    }
}

@WorkerThread
fun File.zipFiles(files: List<File>, @IntRange(from = -1, to = 9) compression: Int = DEFAULT_COMPRESSION, renameMap: HashMap<String, String>? = null) {
    ZipOutputStream(BufferedOutputStream(FileOutputStream(this))).also{it.setLevel(compression)}.use { outStream ->
        outStream.zipFiles(files, renameMap)
    }
}

@WorkerThread
fun File.zipUris(uris: List<Uri>, @IntRange(from = -1, to = 9) compression: Int = DEFAULT_COMPRESSION, renameMap: HashMap<String, String>? = null) {
    ZipOutputStream(BufferedOutputStream(FileOutputStream(this))).also{it.setLevel(compression)}.use { outStream ->
        outStream.zipUris(uris, renameMap)
    }
}

@WorkerThread
fun Uri.zipFiles(files: List<File>, @IntRange(from = -1, to = 9) compression: Int = DEFAULT_COMPRESSION, renameMap: HashMap<String, String>? = null) {
    mainContext.contentResolver.openFileDescriptor(this, MODE_REWRITE).use { descriptor ->
        descriptor?.fileDescriptor?.let {
            ZipOutputStream(BufferedOutputStream(FileOutputStream(it))).also{it.setLevel(compression)}.use { outStream ->
                outStream.zipFiles(files, renameMap)
            }
        }
    }
}

@WorkerThread
fun Uri.zipUris(uris: List<Uri>, @IntRange(from = -1, to = 9) compression: Int = DEFAULT_COMPRESSION, renameMap: HashMap<String, String>? = null) {
    mainContext.contentResolver.openFileDescriptor(this, MODE_REWRITE).use { descriptor ->
        descriptor?.fileDescriptor?.let {
            ZipOutputStream(BufferedOutputStream(FileOutputStream(it))).also{zipStream ->
                zipStream.setLevel(compression)}.use { outStream ->
                outStream.zipUris(uris, renameMap)
            }
        }
    }
}

@WorkerThread
fun Context.zipFiles(zipFile: Uri, files: List<File>, @IntRange(from = -1, to = 9) compression: Int = DEFAULT_COMPRESSION, renameMap: HashMap<String, String>? = null) {
    contentResolver.openFileDescriptor(zipFile, MODE_REWRITE).use { descriptor ->
        descriptor?.fileDescriptor?.let {
            ZipOutputStream(BufferedOutputStream(FileOutputStream(it))).also{stream ->
                stream.setLevel(compression)}.use { outStream ->
                outStream.zipFiles(files, renameMap)
            }
        }
    }
}


private fun ZipOutputStream.zipUris(uris: List<Uri>, renameMap: HashMap<String, String>? = null) {
    uris.forEach { file ->
        mainContext.contentResolver.openFileDescriptor(file, MODE_READ).use { descriptor ->
            descriptor?.fileDescriptor?.let {
                val name = mainContext.getFileNameFromUri(file)
                if(name != null){
                    if(renameMap != null && name in renameMap.keys){
                        putNextEntry(ZipEntry(renameMap.get(name) ?: name))
                    } else {
                        putNextEntry(ZipEntry(name))
                    }
                    BufferedInputStream(FileInputStream(it)).use { inStream ->
                        inStream.copyTo(this)
                    }
                }
            }
        }
    }
}

private fun ZipOutputStream.zipFiles(files: List<File>, renameMap: HashMap<String, String>? = null) {
    files.forEach { file ->
        val name = file.name
        if(renameMap != null && name in renameMap.keys){
            putNextEntry(ZipEntry(renameMap.get(name) ?: name))
        } else {
            putNextEntry(ZipEntry(name))
        }
        BufferedInputStream(FileInputStream(file)).use { inStream ->
            inStream.copyTo(this)
        }
    }
}

@WorkerThread
fun File.zipFiles(files: List<File>,  base: File, @IntRange(from = -1, to = 9) compression: Int = DEFAULT_COMPRESSION, renameMap: HashMap<String, String>? = null) {
    ZipOutputStream(BufferedOutputStream(FileOutputStream(this))).also{it.setLevel(compression)}.use { outStream ->
        outStream.zipFiles(files, base, renameMap)
    }
}

@WorkerThread
fun Uri.zipFiles(files: List<File>,   base: File, @IntRange(from = -1, to = 9) compression: Int = DEFAULT_COMPRESSION, renameMap: HashMap<String, String>? = null) {
    mainContext.contentResolver.openFileDescriptor(this, MODE_REWRITE).use { descriptor ->
        descriptor?.fileDescriptor?.let {
            ZipOutputStream(BufferedOutputStream(FileOutputStream(it))).also{it.setLevel(compression)}.use { outStream ->
                outStream.zipFiles(files, base, renameMap)
            }
        }
    }
}

private fun ZipOutputStream.zipFiles(files: List<File>, base: File, renameMap: HashMap<String, String>? = null) {
    files.forEach { file ->
        val name = file.toRelativeString(base)
        if(renameMap != null && name.substringAfterLast(SEPARATOR, name) in renameMap.keys){
            val prefix = name.substringAfterLast(SEPARATOR, EMPTY_STRING)
            putNextEntry(ZipEntry("$prefix$SEPARATOR${renameMap.get(name) ?: name}"))
        } else {
            putNextEntry(ZipEntry(name))
        }
        BufferedInputStream(FileInputStream(file)).use { inStream ->
            inStream.copyTo(this)
        }
    }
}