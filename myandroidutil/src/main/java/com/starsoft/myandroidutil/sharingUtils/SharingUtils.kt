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

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.annotation.WorkerThread
import androidx.core.content.FileProvider
import com.starsoft.myandroidutil.UriUyils.getFileDataFromUri
import com.starsoft.myandroidutil.fileutils.DIRECTORY
import com.starsoft.myandroidutil.fileutils.getMyCacheDir
import com.starsoft.myandroidutil.linkClickUtils.openWebLink
import com.starsoft.myandroidutil.providers.ContextProvider
import com.starsoft.myandroidutil.providers.mainContext
import com.starsoft.myandroidutil.stringext.EMPTY_STRING
import com.starsoft.myandroidutil.stringext.isEmail
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * Created by Dmitry Starkin on 08.05.2021 13:05.
 */
const val EXTENSION_JPEG = ".jpeg"
const val EXTENSION_ZIP = ".zip"
private val APPLICATION_ID = ContextProvider.context.packageName.toString()
private const val FULL_QUALITY = 100
private val DEFAULT_IMAGE_FORMAT = Bitmap.CompressFormat.JPEG
private const val START_QUALITY = 80
private const val QUALITY_STEP = 40
private const val ACCURACY_PRESENT = 5
private const val BASE64_LEN_COEFFICIENT = 0.75f


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
    intent.type = MME_TYPE_TEXT_PLAIN
    intent.putExtra(Intent.EXTRA_TEXT, text)
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_TASK)
    val chooser = Intent.createChooser(intent, chooserMessage)
    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    this.startActivity(chooser)
}

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

@WorkerThread
fun Context.getCompressedImageFile(image: Bitmap?, quality: Int, filePrefix: String): File {
    return this.getTempImageFile(
        image,
        Bitmap.CompressFormat.JPEG,
        quality,
        filePrefix,
        EXTENSION_JPEG
    )
}

@WorkerThread
fun OutputStream.writeImage(
    image: Bitmap, format: Bitmap.CompressFormat = DEFAULT_IMAGE_FORMAT,
    quality: Int = FULL_QUALITY
) {
    this@writeImage.use { out ->
        image.compress(format, quality, out)
        out.flush()
    }
}

@WorkerThread
@SuppressLint("Recycle")
fun Bitmap?.saveToUri(uri: Uri?, format: Bitmap.CompressFormat = DEFAULT_IMAGE_FORMAT,
                      quality: Int = FULL_QUALITY) {
    this?.let {
        uri?.apply {
            mainContext.contentResolver.openOutputStream(this)?.writeImage(it, format, quality)
        }
    }
}

//TODO check Uri has image content
@WorkerThread
fun Context.getBitmap(uri: Uri): Bitmap? {
    return this@getBitmap.contentResolver.openInputStream(uri)?.let {
        it.use { stream ->
            BitmapFactory.decodeStream(stream)
        }
    }
}

@WorkerThread
fun getBitmapFromBase64(source: String): Bitmap? {
    return Base64.decode(source, Base64.DEFAULT)?.let {
        it.inputStream().use { stream ->
            BitmapFactory.decodeStream(stream)
        }
    }
}

@WorkerThread
fun Context.getTempFileFromBase64(
    source: String,
    filePrefix: String,
    fileExtension: String
): File {
    val bytes = Base64.decode(source, Base64.DEFAULT)
    return File.createTempFile(
        filePrefix,
        System.currentTimeMillis().toString() + fileExtension,
        this@getTempFileFromBase64.getMyCacheDir(DIRECTORY.CACHE_IMAGES)
    ).also { file ->
        FileOutputStream(file).use { out ->
            out.write(bytes)
            out.flush()
        }
        file.deleteOnExit()
    }
}

@WorkerThread
private fun Context.checkAndReduceQuality(
    image: Bitmap?, maxSize: Long,
    filePrefix: String,
    resultQuality: Int = START_QUALITY,
    qualityStep: Int = QUALITY_STEP,
    topBound: Int = FULL_QUALITY,
    bottomBound: Int = 0,
    flag: Boolean = false
): Pair<Int, File> {
    if (resultQuality == FULL_QUALITY || image == null) return Pair(
        FULL_QUALITY,
        getCompressedImageFile(image, resultQuality, filePrefix)
    )
    val compressedFile = getCompressedImageFile(image, resultQuality, filePrefix)
    val compressed = compressedFile.length()
    return if ((compressed - (maxSize / 100) * ACCURACY_PRESENT) > maxSize && qualityStep > 0) {
        val stepped = if (flag) {
            qualityStep / 2
        } else {
            qualityStep
        }.getSteppedDown(resultQuality, bottomBound)
        checkAndReduceQuality(
            image,
            maxSize,
            filePrefix,
            stepped.first,
            stepped.second,
            resultQuality,
            bottomBound,
            false
        )
    } else if ((maxSize - compressed) > ((maxSize / 100) * ACCURACY_PRESENT) && qualityStep > 0) {
        val stepped = if (!flag) {
            qualityStep / 2
        } else {
            qualityStep
        }.getSteppedUp(resultQuality, topBound)
        checkAndReduceQuality(
            image,
            maxSize,
            filePrefix,
            stepped.first,
            stepped.second,
            topBound,
            resultQuality,
            true
        )
    } else {
        Pair(resultQuality, compressedFile)
    }

}

private fun Int.getSteppedDown(quality: Int, bottomBound: Int = 0): Pair<Int, Int> =
    if ((quality - this) > bottomBound) {
        Pair(maxOf(quality - this, quality / 2), this)
    } else {
        (this / 2).getSteppedDown(quality, bottomBound)
    }


private fun Int.getSteppedUp(quality: Int, topBound: Int = FULL_QUALITY): Pair<Int, Int> =
    if ((quality + this) < topBound) {
        Pair(quality + this, this)
    } else {
        (this / 2).getSteppedUp(quality, topBound)
    }

@WorkerThread
fun Context.getTemporaryImageFileFromUri(source: Uri, filePrefix: String): File {
    return File.createTempFile(
        filePrefix,
        System.currentTimeMillis().toString(),
        this@getTemporaryImageFileFromUri.getMyCacheDir(DIRECTORY.CACHE_IMAGES)
    ).also { file ->
        FileOutputStream(file).use { out ->
            this@getTemporaryImageFileFromUri.contentResolver.openInputStream(source)?.copyTo(out)
            out.flush()
        }
        file.deleteOnExit()
    }
}

//TODO check Uri has image content
@WorkerThread
fun Context.getTempImageFileFromUri(uri: Uri, maxSize: Long, filePrefix: String): File {
    return if ((getFileDataFromUri(uri)?.length ?: 0) < maxSize) {
        this.getTemporaryImageFileFromUri(uri, filePrefix)
    } else {
        val bitmap = getBitmap(uri)
        checkAndReduceQuality(bitmap, maxSize, filePrefix).second
    }
}

@WorkerThread
fun Context.getTempImageFileFromBase64(
    source: String,
    maxSize: Long,
    filePrefix: String,
    fileSuffix: String
): File {
    return if (source.getFileSizeFromBase64() < maxSize) {
        this.getTempFileFromBase64(source, filePrefix, fileSuffix)
    } else {
        val bitmap = getBitmapFromBase64(source)
        checkAndReduceQuality(bitmap, maxSize, filePrefix).second
    }
}

fun String?.getFileSizeFromBase64(): Long =
    ((this?.length?.toFloat() ?: 0f) * BASE64_LEN_COEFFICIENT).toLong()

fun Context.sendEmail(eMails: Array<String>, fields: EmailsField = EmailsField()) {
    if (eMails.isNotEmpty()) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = MME_TYPE_RFS822
        intent.putExtra(Intent.EXTRA_EMAIL, eMails)
//        TODO Dmitry
        intent.putExtra(Intent.EXTRA_SUBJECT, fields.theme)
        intent.putExtra(Intent.EXTRA_TEXT, fields.bodyTemplate)

        val packageManager = this.applicationContext.packageManager
        val matches: List<ResolveInfo> = packageManager.queryIntentActivities(intent, 0)

        if (matches.isNotEmpty()) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val choicer = Intent.createChooser(intent, fields.choicerMessage)
            choicer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.applicationContext.startActivity(choicer);
        } else if (fields.reserveLink.isNotEmpty()) {
            this.openWebLink(fields.reserveLink)
        }
    }
}

fun Context.sendMail(eMail: String, fields: EmailsField = EmailsField()) {
    if (eMail.isEmail()) {
        val intent = Intent()
        intent.action = Intent.ACTION_SENDTO
        intent.data = Uri.parse(
            "mailto:$eMail${
                if (fields.theme.isNotEmpty()) {
                    "?subject=${Uri.encode(fields.theme)}"
                } else {
                    "?subject="
                }
            }${
                if (fields.bodyTemplate.isNotEmpty()) {
                    "&body=${Uri.encode(fields.bodyTemplate)}"
                } else {
                    "&body="
                }
            }"
        )

        val packageManager = this.applicationContext.packageManager
        val matches: List<ResolveInfo> = packageManager.queryIntentActivities(intent, 0)

        if (matches.isNotEmpty()) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val choicer = Intent.createChooser(intent, fields.choicerMessage)
            choicer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.applicationContext.startActivity(choicer);
        } else if (fields.reserveLink.isNotEmpty()) {
            this.openWebLink(fields.reserveLink)
        }
    }
}

data class EmailsField(
    val choicerMessage: String = EMPTY_STRING,
    val theme: String = EMPTY_STRING,
    val bodyTemplate: String = EMPTY_STRING,
    val reserveLink: String = EMPTY_STRING
)

class SaveJpgImage : CreateDocument(MME_TYPE_IMAGE_JPG) {
    override fun createIntent(context: Context, input: String): Intent {
        super.createIntent(context, input)
        return Intent(Intent.ACTION_CREATE_DOCUMENT)
            .setType(MME_TYPE_IMAGE_JPG)
            .putExtra(Intent.EXTRA_TITLE, input)
    }
}

class SaveZip : CreateDocument(MME_TYPE_ZIP) {
    override fun createIntent(context: Context, input: String): Intent {
        super.createIntent(context, input)
        return Intent(Intent.ACTION_CREATE_DOCUMENT)
            .setType(MME_TYPE_ZIP)
            .putExtra(Intent.EXTRA_TITLE, input)
    }
}