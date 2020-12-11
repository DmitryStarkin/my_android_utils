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

package com.starsoft.myandroidutil.prefs

import android.content.SharedPreferences
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.starsoft.myandroidutil.providers.mainContext

// This File Created at 10.12.2020 19:14.

//TODO handle exceptions need
open class BaseSecurePrefs @JvmOverloads constructor(private val fileName: String = DEFAULT_PREF_FILE,
                                                     private val keyGenParameterSpec: KeyGenParameterSpec? = null,
                                                     private val authenticationParam: AuthenticationParam? = null,
                                                     private val KeyScheme: MasterKey.KeyScheme = MasterKey.KeyScheme.AES256_GCM,
                                                     private val masterKeyAlias: String = MasterKey.DEFAULT_MASTER_KEY_ALIAS) : BasePreferences() {

    private val securePref: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(mainContext, masterKeyAlias).apply {
            keyGenParameterSpec?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setKeyGenParameterSpec(it)
                } else {
                    null
                }
            } ?: run {
                setKeyScheme(KeyScheme)
                authenticationParam?.let { 	setRequestStrongBoxBacked(it.RequestStrongBoxBacked)
                setUserAuthenticationRequired(it.AuthenticationRequired, it.AuthenticationValidityDurationSeconds)
                }
            }
        }.build()

        EncryptedSharedPreferences.create(
                mainContext,
                fileName,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    }

    override fun getPreferences(): SharedPreferences {
        return securePref
    }

    data class AuthenticationParam(
            val RequestStrongBoxBacked: Boolean,
            val AuthenticationRequired: Boolean,
            var AuthenticationValidityDurationSeconds: Int = MasterKey.getDefaultAuthenticationValidityDurationSeconds()
    ){
        init {
            if(AuthenticationValidityDurationSeconds < 1 || AuthenticationValidityDurationSeconds > Int.MAX_VALUE){
                AuthenticationValidityDurationSeconds = MasterKey.getDefaultAuthenticationValidityDurationSeconds()
            }
        }
    }
}