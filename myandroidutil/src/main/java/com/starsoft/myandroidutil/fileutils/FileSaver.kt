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

package com.starsoft.myandroidutil.fileutils

import android.graphics.Bitmap
import android.os.Environment
import com.starsoft.myandroidutil.logutils.LogWriter
import com.starsoft.myandroidutil.logutils.Logger
import com.starsoft.myandroidutil.providers.ContextProvider
import com.starsoft.simpleandroidasynclibrary.core.executorfun.runOnExecutor
import com.starsoft.simpleandroidasynclibrary.core.launch
import com.starsoft.simpleandroidasynclibrary.executors.newSingleThreadPool
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Exception


// This File Created at 25.11.2020 12:37.

object FileSaver {

    private val DEFAULT_COMRESSION = 80
    private val DEFAULT_IMAGE_FORMAT = Bitmap.CompressFormat.JPEG
    private val executor = newSingleThreadPool()

    var imageCompression = DEFAULT_COMRESSION
    var imageFormat = DEFAULT_IMAGE_FORMAT
    var overwriteFiles = true

    @JvmOverloads
    fun saveFromAssets(from: String, to: String, directory: DIRECTORY, onSusses: (File) -> Unit, onError: (Throwable) -> Unit = ::errorStub) {
        executor.launch({ f -> onSusses.invoke(f) }, { e -> onError.invoke(e) }) {

            val myInput = ContextProvider.context.assets.open(from)
            saveFile(to, directory, myInput)
        }

    }

    @JvmOverloads
    fun saveFromInputStream(from: InputStream, to: String, directory: DIRECTORY, onSusses: (File) -> Unit, onError: (Throwable) -> Unit = ::errorStub) {
        executor.launch({ f -> onSusses.invoke(f) }, { e -> onError.invoke(e) }) {

            saveFile(to, directory, from)
        }

    }

    @JvmOverloads
    fun saveBitmap(from: Bitmap, to: String, directory: DIRECTORY, onSusses: (File) -> Unit, onError: (Throwable) -> Unit = ::errorStub) {
        executor.launch ({ f -> onSusses.invoke(f) }, { e -> onError.invoke(e) }) {

            saveFile(to, directory, bitmap = from)
        }
    }

    fun stopSaving() {
        executor.purge()
    }


    private fun errorStub(e: Throwable) {
       e.printStackTrace()
    }

    private fun saveFile(filename: String, directory: DIRECTORY, inputStream: InputStream? = null, bitmap: Bitmap? = null, overwrite: Boolean = overwriteFiles): File {
        lateinit var file: File
        var myOutput: FileOutputStream? = null

        try {
            file = generateFile(filename, directory)

//TODO check if need overwrite file
            if (file.exists() && !overwrite) {
                return file
            } else {
                file.createNewFile()
            }

            myOutput = FileOutputStream(file)
            val buffer = ByteArray(1024)

            if (inputStream != null) {
                var length: Int = inputStream.read(buffer)
                while ((length) > 0 && !Thread.currentThread().isInterrupted) {
                    myOutput.write(buffer, 0, length)
                    length = inputStream.read(buffer)
                }
            } else if (bitmap != null) {
                bitmap.compress(imageFormat, imageCompression, myOutput)
            } else {

                throw Exception("There is no data to write to the file")
            }

        } finally {
            inputStream?.close()
            myOutput?.flush()
            myOutput?.close()
        }
        return file
    }

    private fun generateFile(filename: String, directory: DIRECTORY): File {
        val file: File

        when (directory) {

            DIRECTORY.CACH_IMAGES -> {

                file = File(getCacheDir("images"), "/$filename")
            }
            DIRECTORY.CACH_FILES -> {

                file = File(getCacheDir("files"), "/$filename")
            }

            DIRECTORY.CACH_LOGS -> {

                file = File(getCacheDir("logs"), "/$filename")
            }
            DIRECTORY.EXTERNAL_DOCUMENTS -> {

                file = File(ContextProvider.context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + filename)
            }
        }

        return file
    }

    private fun getCacheDir(name: String): File {

        val dir = File(ContextProvider.context.cacheDir.toString() + "/" + name + "/")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    enum class DIRECTORY {

        CACH_IMAGES,
        CACH_FILES,
        CACH_LOGS,
        EXTERNAL_DOCUMENTS
    }
}