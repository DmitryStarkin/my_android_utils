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

import androidx.annotation.WorkerThread
import com.starsoft.myandroidutil.providers.mainContext
import com.starsoft.myandroidutil.sharingUtils.DIRECTORY
import com.starsoft.myandroidutil.sharingUtils.getMyCacheDir
import com.starsoft.myandroidutil.stringext.EMPTY_STRING
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Created by Dmitry Starkin on 17.03.2023 13:18.
 */

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









