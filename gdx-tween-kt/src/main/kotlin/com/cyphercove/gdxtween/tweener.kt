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

import com.cyphercove.gdxtween.tweens.AlphaTween

/**
 * An owner of two TweenManagers. Use this interface to be able to start Tweens without passing the tween manager
 * explicitly. AlphaTweens will be handled separately by the [alphaTweenManager] so alpha can be modified independently
 * without interrupting color tweens.
 */
interface Tweener {
    val tweenManager: TweenManager
    val alphaTweenManager: TweenManager

    fun Tween<*, *>.start() = start(tweenManager)
    fun AlphaTween.start() = start(alphaTweenManager)
}

/** A basic implementation of [Tweener] that can be used as a delegate. */
class CompanionTweenManager: Tweener {
    override val tweenManager = TweenManager()
    override val alphaTweenManager = TweenManager()
}