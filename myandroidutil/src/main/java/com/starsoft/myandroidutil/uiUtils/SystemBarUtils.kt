/*
 * Copyright (c) 2026. Dmitry Starkin Contacts: t0506803080@gmail.com
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

package com.starsoft.myandroidutil.uiUtils

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Created by Dmitry Starkin on 11.08.2026 16:13.
 */

private var oldTopColor = Color.WHITE

private var oldBottomColor = Color.WHITE

fun Activity.setSystemBarsBg(topBg: Int, bottomBg: Int, rootView: View, topView: View){
    val bgb = rootView.background
    if(bgb  is ColorDrawable){
        oldBottomColor = bgb.color
    }
    val bgt = topView.background
    if(bgt  is ColorDrawable){
        oldBottomColor = bgt.color
    }
    rootView.setBackgroundColor(bottomBg)
    topView.setBackgroundColor(topBg)
    setSystemBarsColorMode(topBg, bottomBg)
}

fun Activity.restoreSystemBarsBg(bottomView: View, topView: View){
    bottomView.setBackgroundColor(oldBottomColor)
    topView.setBackgroundColor(oldTopColor)
    setSystemBarsColorMode(oldTopColor, oldBottomColor)
}

// backward compatibility
@Suppress("DEPRECATION")
fun Activity.setSystemBarsColors(statusColor:Int, navigationColor:Int): Pair<Int, Int>?{
    return window?.let{
        val old =  Pair(it.statusBarColor, it.navigationBarColor)
        it.statusBarColor = statusColor
        it.navigationBarColor = navigationColor
        it.decorView.let{decorView ->
            WindowInsetsControllerCompat(it, decorView).let{controller ->
                controller.isAppearanceLightStatusBars = !isColorDark(statusColor)
                controller.isAppearanceLightNavigationBars = !isColorDark(navigationColor)
            }
        }
        old
    }
}

// backward compatibility
@RequiresApi(Build.VERSION_CODES.M)
@Suppress("DEPRECATION")
fun Activity.setStatusBarColor(color:Int){
    var flags = window?.decorView?.systemUiVisibility // get current flag
    if (flags != null) {
        if(isColorDark(color)){
            flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            window?.decorView?.systemUiVisibility = flags
        }else{
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window?.decorView?.systemUiVisibility = flags
        }
    }
    window?.statusBarColor = color
}

fun Activity.setSystemBarsColorMode(statusColor:Int, navigationColor:Int){
    window?.let{
        it.decorView.let{decorView ->
            WindowInsetsControllerCompat(it, decorView).let{controller ->
                controller.isAppearanceLightStatusBars = !isColorDark(statusColor)
                controller.isAppearanceLightNavigationBars = !isColorDark(navigationColor)
            }
        }

    }
}

private fun isColorDark(color:Int) : Boolean{
    val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
    return darkness >= 0.5 && color != Color.TRANSPARENT

}