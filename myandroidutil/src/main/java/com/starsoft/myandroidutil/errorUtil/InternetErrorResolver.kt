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

package com.starsoft.myandroidutil.errorUtil

import android.content.Context
import com.starsoft.myandroidutil.R


/**
 * Created by Dmitry Starkin on 07.02.2022 11:00.
 */
fun Context.resolveErrorByCode(code: Int): String =
    getString(R.string.error) + when(code){
        400 -> { getString(R.string.code_400) }
        401 -> { getString(R.string.code_401) }
        402 -> { getString(R.string.code_402) }
        403 -> { getString(R.string.code_403) }
        404 -> { getString(R.string.code_404) }
        405 -> { getString(R.string.code_405) }
        406 -> { getString(R.string.code_406) }
        407 -> { getString(R.string.code_407) }
        408 -> { getString(R.string.code_408) }
        409 -> { getString(R.string.code_409) }
        410 -> { getString(R.string.code_410) }
        411 -> { getString(R.string.code_411) }
        413 -> { getString(R.string.code_413) }
        414 -> { getString(R.string.code_414) }
        415 -> { getString(R.string.code_415) }
        416 -> { getString(R.string.code_416) }
        417 -> { getString(R.string.code_417) }
        418 -> { getString(R.string.code_418) }
        419 -> { getString(R.string.code_419) }
        421 -> { getString(R.string.code_421) }
        422 -> { getString(R.string.code_422) }
        423 -> { getString(R.string.code_423) }
        424 -> { getString(R.string.code_424) }
        425 -> { getString(R.string.code_425) }
        426 -> { getString(R.string.code_426) }
        428 -> { getString(R.string.code_428) }
        429 -> { getString(R.string.code_429) }
        431 -> { getString(R.string.code_431) }
        449 -> { getString(R.string.code_449) }
        451 -> { getString(R.string.code_451) }
        499 -> { getString(R.string.code_499) }
        500 -> { getString(R.string.code_500) }
        501 -> { getString(R.string.code_501) }
        502 -> { getString(R.string.code_502) }
        503 -> { getString(R.string.code_503) }
        504 -> { getString(R.string.code_504) }
        505 -> { getString(R.string.code_505) }
        506 -> { getString(R.string.code_506) }
        507 -> { getString(R.string.code_507) }
        508 -> { getString(R.string.code_508) }
        509 -> { getString(R.string.code_509) }
        510 -> { getString(R.string.code_510) }
        511 -> { getString(R.string.code_511) }
        520 -> { getString(R.string.code_520) }
        521 -> { getString(R.string.code_521) }
        522 -> { getString(R.string.code_522) }
        523 -> { getString(R.string.code_523) }
        524 -> { getString(R.string.code_524) }
        525 -> { getString(R.string.code_525) }
        526 -> { getString(R.string.code_526) }
        else ->{ "${getString(R.string.witch_code)} $code" }
    }