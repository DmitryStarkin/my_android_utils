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

package com.starsoft.myandroidutil.navigationUtils.interfaces

import android.os.Bundle


/**
 * Created by Dmitry Starkin on 13.02.2022 18:56.
 */
interface Rout {
    val destination: Class<*>
    val data: Bundle?
        get() = null

    val tag: String
    get() = destination.name

    class RoutStub(): Rout{
        override val destination = RoutStub::class.java
    }

    class Close(): Rout{
        override val destination = Close::class.java
    }

    data class OpenLink
        (val link: String) : Rout {
        override val destination: Class<*>
            get() = OpenLink::class.java
    }
}