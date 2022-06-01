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

package com.starsoft.myandroidutil.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import com.starsoft.myandroidutil.accessibilityutils.isTouchExplorationEnabled


/**
 * Created by Dmitry Starkin on 06.02.2022 18:15.
 */
class HoverConstraint  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes){

    var touchInterceptor: ((MotionEvent) -> Unit)? = null

    //    for possible handle click and long click in touch Exploration mode
    override fun onHoverEvent(event: MotionEvent): Boolean {
        if (isTouchExplorationEnabled()) {
            when (event.actionMasked) {
                MotionEvent.ACTION_HOVER_ENTER -> event.action = MotionEvent.ACTION_DOWN
                MotionEvent.ACTION_HOVER_MOVE -> event.action = MotionEvent.ACTION_MOVE
                MotionEvent.ACTION_HOVER_EXIT -> event.action = MotionEvent.ACTION_UP
            }
            return dispatchTouchEvent(event)
        }
        return false
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.apply { touchInterceptor?.invoke(this) }
        return super.dispatchTouchEvent(ev)
    }
}