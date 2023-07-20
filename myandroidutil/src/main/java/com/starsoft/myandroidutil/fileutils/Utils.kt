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

package com.starsoft.myandroidutil.fileutils

import android.content.Context
import android.os.Environment
import androidx.annotation.WorkerThread
import com.starsoft.myandroidutil.providers.ContextProvider
import com.starsoft.myandroidutil.providers.mainContext
import com.starsoft.myandroidutil.stringext.EMPTY_STRING
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Created by Dmitry Starkin on 17.03.2023 13:18.
 */

enum class DIRECTORY(val directoryName: String) {
    CACHE_IMAGES("/images/"),
    CACHE_FILES("/files/"),
    CACHE_LOGS("/logs/"),
    EXTERNAL_DOCUMENTS(
        ContextProvider.context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()
    )
}

fun Context.getMyCacheDir(dir: DIRECTORY): File {

    val directory = File(this.cacheDir.toString() + dir.directoryName)
    if (!directory.exists()) {
        directory.mkdirs()
    }
    return directory
}

fun Context.getMyCacheSubDir(dir: DIRECTORY, name: String): File =

    File(getMyCacheDir(dir), name).let {
        if(!it.exists()){
            it.mkdirs()
        }
        it
    }

fun File.getSubDir(name: String): File =
    if(!this.isDirectory){
        throw Exception("must be an directory")
    } else {
        File(this, name).let {
            if(!it.exists()){
                it.mkdirs()
            }
            it
        }
    }

fun Context.deleteMyCacheDir(dir: DIRECTORY): Boolean{

    val directory = File(this.cacheDir.toString() + dir.directoryName)
    return if (directory.exists()) {
        try {
            directory.deleteRecursively()
            true
        } catch (e: IOException){
            e.printStackTrace()
            false
        }
    } else{
        true
    }
}

fun Context.getExtFilesDir(subdirName: String = EMPTY_STRING): File {
    val filePath: String =
      getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            .toString() + "/" + subdirName
    return File(filePath).also {
        if (!it.exists()) {
            it.mkdir()
            it.setReadable(true)
        }
    }
}

fun Context.deleteMyExternalFilesDir(subdirName: String): Boolean{
    val directory = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        .toString() + "/" + subdirName)
    return if (directory.exists()) {
        try {
            directory.deleteRecursively()
            true
        } catch (e: IOException){
            e.printStackTrace()
            false
        }
    } else{
        true
    }
}

@WorkerThread
fun DIRECTORY.addFileFromStream(inputStream: InputStream, filename: String, overwrite: Boolean = true): File =
    mainContext.getMyCacheDir(this).addFileFromStream(inputStream, filename, overwrite)

@WorkerThread
fun File.addFileFromStream(inputStream: InputStream, filename: String, overwrite: Boolean = true): File =
    if(!this.isDirectory){
        throw Exception("must be an directory")
    } else {
        inputStream.use {
            val file = File(this, filename)
            if (file.exists() && !overwrite) {
                return file
            } else {
                file.createNewFile()
            }
            FileOutputStream(file).use { output ->
                it.copyTo(output)
                output.flush()
            }
            file
        }
    }


@WorkerThread
fun DIRECTORY.addTextFile(data: String, filename: String, overwrite: Boolean = true): File =
    mainContext.getMyCacheDir(this).addTextFile(data, filename, overwrite)

@WorkerThread
fun File.addTextFile(data: String, filename: String, overwrite: Boolean = true): File =
    if(!this.isDirectory){
        throw Exception("must be an directory")
    } else {
        val file = File(this, filename)
        if (file.exists() && !overwrite) {
             file
        } else {
            file.createNewFile()
            file.writeString(data)
        }
    }

@WorkerThread
fun File.writeString(data: String): File =
    ByteArrayInputStream(data.toByteArray()).use {
        FileOutputStream(this).use { output ->
            it.copyTo(output)
            output.flush()
        }
        this
    }

@WorkerThread
fun File.readAsText(): String =
        try {
           inputStream().bufferedReader().use { it.readText() }
        } catch (e: FileNotFoundException){
            e.printStackTrace()
            EMPTY_STRING
        }









