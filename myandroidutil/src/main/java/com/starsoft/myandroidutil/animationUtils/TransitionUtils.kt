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

import android.view.View
import android.view.ViewGroup
import androidx.transition.Transition
import androidx.transition.TransitionManager

/**
 * Created by Dmitry Starkin on 11.04.2024 13:05.
 */

const val DEFAULT_ANIMATION_DELAY = 600L
const val DEFAULT_ANIMATION_OFFSET = 0L

@JvmOverloads
fun View.transit(transition: Transition, listener: Transition.TransitionListener? = null, duration: Long? = DEFAULT_ANIMATION_DELAY,
                 offset: Long? = DEFAULT_ANIMATION_OFFSET) {
    offset?.apply {transition.startDelay = this}
    duration?.apply { transition.duration = this }
    transition.addTarget(this)
    transition.apply { listener?.apply { addListener(this) } }
    TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
}

@JvmOverloads
fun View.changeVisibilityWithTransition(visibility: VisibilityState,
                                        transition: Transition,
                                        listener: Transition.TransitionListener? = null,
                                        duration: Long? = DEFAULT_ANIMATION_DELAY,
                                        offset: Long? = DEFAULT_ANIMATION_OFFSET) {
    if(this.visibility != visibility.state) {
        this.transit(transition, listener, duration, offset)
        this.visibility = visibility.state
    }
}

@JvmOverloads
fun View.swapWitchTransition(other: View,
                             goneTransition: Transition,
                             visibleTransition: Transition,
                             listener: Transition.TransitionListener? = null,
                             goneDuration: Long? = DEFAULT_ANIMATION_DELAY,
                             visibleDuration: Long? = DEFAULT_ANIMATION_DELAY,
                             offset: Long? = DEFAULT_ANIMATION_OFFSET) {

    val toVisible = if(this.visibility == View.GONE){this} else {other }
    val toInvisible = if(this.visibility == View.VISIBLE){this} else {other}
    toInvisible.changeVisibilityWithTransition(
        VisibilityState.GONE,
        goneTransition,
        listener = object : AnimationTransitionListener {
            override fun onTransitionEnd(transition: Transition) {
                super.onTransitionEnd(transition)
                toVisible.changeVisibilityWithTransition(
                    VisibilityState.VISIBLE,
                    visibleTransition,
                    listener,
                    visibleDuration,
                    offset
                )
            }
        },
        goneDuration,
        offset
    )
}


enum class VisibilityState(val state: Int) {
    GONE(View.GONE),
    VISIBLE(View.VISIBLE),
    INVISIBLE(View.INVISIBLE)
}

interface AnimationTransitionListener : Transition.TransitionListener {
    override fun onTransitionStart(transition: Transition) {
//        stub
    }

    override fun onTransitionEnd(transition: Transition) {
        //        stub
    }

    override fun onTransitionCancel(transition: Transition) {
        //        stub
    }

    override fun onTransitionPause(transition: Transition) {
        //        stub
    }

    override fun onTransitionResume(transition: Transition) {
        //        stub
    }
}