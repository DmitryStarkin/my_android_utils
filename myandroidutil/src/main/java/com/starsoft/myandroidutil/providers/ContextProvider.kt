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

package com.starsoft.myandroidutil.providers

import android.annotation.SuppressLint
import android.content.Context


// This File Created at 25.11.2020 10:35.


@SuppressLint("StaticFieldLeak")
class ContextProvider(_context: Context) {

    companion object {

        lateinit var context: Context
    }

    init {
        context = _context
    }

    fun getContext(): Context {
        return context
    }
}