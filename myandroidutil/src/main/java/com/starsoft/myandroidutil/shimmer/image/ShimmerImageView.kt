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

package com.starsoft.myandroidutil.shimmer.image

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import com.starsoft.myandroidutil.R
import com.starsoft.myandroidutil.uiUtils.TRANSPARENT_COLOR
import com.starsoft.myandroidutil.uiUtils.createShimmerWitchColor
import com.starsoft.myandroidutil.uiUtils.getPrimaryColor

/**
 * Created by Dmitry Starkin on 06.07.2022 12:29.
 */

@RequiresApi(Build.VERSION_CODES.M)
class ShimmerImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr){
    init {
        val color:Int
        val corners:Int
        val attributes = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ShimmerImageView,
            0, 0)

        try {
            color = attributes.getColor(R.styleable.ShimmerImageView_shimmer_color, context.getPrimaryColor())
            corners = attributes.getDimension(R.styleable.ShimmerImageView_shimmer_corners_radius, 0F).toInt()
        } finally {
            attributes.recycle()
        }
        setBackgroundColor(TRANSPARENT_COLOR)
        setImageDrawable(this.createShimmerWitchColor(color, corners))
    }
}