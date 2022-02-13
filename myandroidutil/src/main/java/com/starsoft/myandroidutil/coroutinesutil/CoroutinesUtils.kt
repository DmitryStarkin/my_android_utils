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

package com.starsoft.myandroidutil.coroutinesutil

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by Dmitry Starkin on 07.02.2022 10:56.
 */

/**
 * Calls [lambda] as suspend function on Dispatchers.IO [Dispatchers]
 * @param lambda function to call
 */
suspend fun <V> runSuspend(lambda: () -> V): V =
    withContext(Dispatchers.IO) {
        lambda.invoke()
    }

/**
 * Calls [lambda] as suspend function on Dispatchers.IO [Dispatchers]
 * @param lambda function to call
 */
suspend fun <T, V> T.runSuspend(lambda: T.() -> V): V =
    withContext(Dispatchers.IO) {
        this@runSuspend.lambda()
    }