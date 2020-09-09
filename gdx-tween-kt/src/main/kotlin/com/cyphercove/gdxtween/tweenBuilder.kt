/* ******************************************************************************
 * Copyright 2020 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.cyphercove.gdxtween

import com.cyphercove.gdxtween.targettweens.AccessorTween

/**
 * Allows setting up a tween using a lambda with a [TweenBuilder], so the static references [Tweens] and [Ease] can be
 * omitted.
 */
inline fun <T: Tween<*, *>> tween(tweenSetup: TweenBuilder.() -> T): T {
    return tweenSetup(TweenBuilder)
}

/**
 * Pass-through functions and properties for Tween and Ease. Can be subclassed to add additional members.
 */
open class TweenBuilder {

    companion object: TweenBuilder()

    fun cubic() = Ease.cubic()
    fun quartic() = Ease.quintic()

    fun to(target: AccessorTween.Accessor, duration: Float) = Tweens.to(target, duration)
    // ...
}