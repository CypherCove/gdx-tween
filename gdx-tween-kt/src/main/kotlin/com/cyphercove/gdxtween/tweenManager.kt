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

/**
 * An owner of a [TweenRunner]. Use this interface to be able to start [TargetTween]s without passing the [TweenRunner]
 * explicitly.
 */
interface TweenManager {
    val tweenRunner: TweenRunner

    /**
     * Starts the tween using the [tweenRunner].
     */
    fun TargetTween<*, *>.start() = start(tweenRunner)

    /**
     * Must be called for every frame of animation to advance all of the tweens.
     * @param deltaTime The time passed since the last step.
     */
    fun TweenManager.stepTweens(deltaTime: Float) {
        tweenRunner.step(deltaTime)
    }

    /**
     * Cancels all tweens immediately. No listener will be called.
     * @return Whether any tween was removed.
     */
    fun TweenManager.cancelAllTweens(): Boolean {
        return tweenRunner.cancelAllTweens()
    }

}

/**
 * Creates a tween using the passed function and starts it immediately.
 * @return The created and started tween.
 */
inline fun <T: Tween<*>> TweenManager.startTween(tweenSetup: TweenBuilder.() -> T): T {
    return tween(tweenSetup).also { it.start(tweenRunner) }
}

/** A basic implementation of [TweenManager] that can be used as a delegate. */
class DelegateTweenManager : TweenManager {
    override val tweenRunner = TweenRunner()
}