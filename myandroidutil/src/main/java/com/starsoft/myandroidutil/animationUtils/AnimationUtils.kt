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
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes

/**
 * Created by Dmitry Starkin on 11.04.2024 13:10.
 */

fun View.animate(@AnimRes animRes: Int, duration: Long? = null, listener: Animation.AnimationListener? = null, offset: Long? = null){
    val animation: Animation = AnimationUtils.loadAnimation(this.context, animRes)
    this.animate(animation, duration, listener, offset)
}

fun View.animate(anim: Animation, duration: Long? = null,  listener: Animation.AnimationListener? = null, offset: Long? = null){
    duration?.apply { anim.duration = this }
    offset?.apply { anim.startOffset = this }
    listener?.apply { anim.setAnimationListener(this) }
    this.startAnimation(anim)
}


@JvmOverloads
fun View.changeVisibilityWithAnimation(visibility: VisibilityState,
                                       @AnimRes animRes: Int,
                                       duration: Long? = DEFAULT_ANIMATION_DELAY,
                                       offset: Long? = DEFAULT_ANIMATION_OFFSET,
                                       listener: Animation.AnimationListener? = null,) {
    if(this.visibility != visibility.state) {
        val animation: Animation = AnimationUtils.loadAnimation(this.context, animRes)
        this.visibility = visibility.state
        this.animate(animation, duration, listener, offset)
    }
}

@JvmOverloads
fun View.changeVisibilityWithAnimation(visibility: VisibilityState,
                                       animation: Animation,
                                       duration: Long? = DEFAULT_ANIMATION_DELAY,
                                       offset: Long? = DEFAULT_ANIMATION_OFFSET,
                                       listener: Animation.AnimationListener? = null,) {
    if(this.visibility != visibility.state) {
        this.visibility = visibility.state
        this.animate(animation, duration, listener, offset)
    }
}

@JvmOverloads
fun View.swapWitchAnimation(other: View,
                            @AnimRes goneAnimation: Int,
                            @AnimRes visibleAnimation: Int,
                            goneDuration: Long? = DEFAULT_ANIMATION_DELAY,
                            visibleDuration: Long? = DEFAULT_ANIMATION_DELAY,
                            offset: Long? = DEFAULT_ANIMATION_OFFSET,
                            listener: Animation.AnimationListener? = null) {
    val gone: Animation = AnimationUtils.loadAnimation(this.context, goneAnimation)
    val visible: Animation = AnimationUtils.loadAnimation(this.context, visibleAnimation)
    swapWitchAnimation(other, gone, visible, goneDuration, visibleDuration, offset, listener)
}

@JvmOverloads
fun View.swapWitchAnimation(other: View,
                            goneAnimation: Animation,
                            visibleAnimation: Animation,
                            goneDuration: Long? = DEFAULT_ANIMATION_DELAY,
                            visibleDuration: Long? = DEFAULT_ANIMATION_DELAY,
                            offset: Long? = DEFAULT_ANIMATION_OFFSET,
                            listener: Animation.AnimationListener? = null) {

    val toVisible = if(this.visibility == View.GONE || this.visibility == View.INVISIBLE){this} else {other}
    val toInvisible = if(this.visibility == View.VISIBLE){this} else {other}
    if(toVisible == toInvisible) throw IllegalStateException("all views are the same state")
    toInvisible.changeVisibilityWithAnimation(
        VisibilityState.GONE,
        goneAnimation,
        goneDuration,
        offset,
        listener = object : AnimationListener {
            override fun onAnimationEnd(animation: Animation) {
                super.onAnimationEnd(animation)
                toVisible.changeVisibilityWithAnimation(
                    VisibilityState.VISIBLE,
                    visibleAnimation,
                    visibleDuration,
                    offset,
                    listener
                )
            }
        }
    )
}

interface AnimationListener : Animation.AnimationListener {
    override fun onAnimationStart(animation: Animation) {
//        stub
    }

    override fun onAnimationEnd(animation: Animation) {
        //        stub
    }

    override fun onAnimationRepeat(animation: Animation) {
        //        stub
    }
}