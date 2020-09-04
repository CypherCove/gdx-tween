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
 * An owner of two [TweenRunner]s. Use this interface to be able to start [Tween]s without passing the tween runner
 * explicitly. AlphaTweens will be handled separately by the [alphaTweenRunner] so alpha can be modified independently
 * without interrupting color tweens.
 */
interface Tweener {
    val tweenRunner: TweenRunner
    val alphaTweenRunner: TweenRunner

    /**
     * Starts the tween using the [tweenRunner].
     */
    fun Tween<*, *>.start() = start(tweenRunner)

    /**
     * Starts the AlphaTween using the [alphaTweenRunner].
     */
    fun AlphaTween.start() = start(alphaTweenRunner)
}

/**
 * Removes any running or pending (delayed) tweens for the target object immediately, from both runners. No listener
 * will be called.
 * @param target The target object whose tween or tween chain is to be removed.
 * @return Whether a tween or tween chain existed and was removed.
 */
fun Tweener.clearTweens(target: Any): Boolean {
    return tweenRunner.clearTweens(target) or alphaTweenRunner.clearTweens(target)
}

/** A basic implementation of [Tweener] that can be used as a delegate. */
class DelegateTweenManager: Tweener {
    override val tweenRunner = TweenRunner()
    override val alphaTweenRunner = TweenRunner()
}