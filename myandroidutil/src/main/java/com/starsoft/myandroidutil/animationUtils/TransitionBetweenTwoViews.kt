/*
 * Copyright (c) 2024. Dmitry Starkin Contacts: t0506803080@gmail.com
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

package com.starsoft.myandroidutil.animationUtils

import android.graphics.PointF
import android.graphics.Rect
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import com.starsoft.myandroidutil.RectUtils.castToOuterCoordinates
import com.starsoft.myandroidutil.RectUtils.getOffsetInOuter

/**
 * Created by Dmitry Starkin on 11.04.2024 13:10.
 */

/**
 * @param source the rectangle of the source view content in the coordinate system of the screen
 * @param destination first value the rectangle of the destination content
 * in the coordinate system of the screen
 * (it is assumed that the content can be shifted inside the view)
 * second value the rectangle of the destination view in the coordinate system of the screen
 * @param destinationParent the rectangle of the destination view parent in the coordinate system of the screen
 *
 *
 * usage simulate to usage TransitionBetweenTwoViewsRev [TransitionBetweenTwoViewsRev]
 */
class TransitionBetweenTwoViews(source: Rect, destination: Pair<Rect, Rect>, destinationParent: Rect) : AnimationSet(false) {
    init {
        val sourceInDestCoordinates = source.castToOuterCoordinates(destinationParent)
        val destImageOffset = PointF(destination.first.getOffsetInOuter(destination.second))
        val scaleX =  source.width().toFloat()/destination.first.width().toFloat()
        val scaleY = source.height().toFloat()/destination.first.height().toFloat()
        addAnimation(
            TranslateAnimation(
                ABSOLUTE, (sourceInDestCoordinates.left.toFloat() + destImageOffset.x) /scaleX,
                RELATIVE_TO_SELF, 0f, ABSOLUTE,
                (sourceInDestCoordinates.top.toFloat() + destImageOffset.y)/scaleY, RELATIVE_TO_SELF, 0f
            )
        )
        addAnimation(
            ScaleAnimation(
                scaleX, 1f,
                scaleY, 1f, 0f, 0f
            )

        )
    }
}