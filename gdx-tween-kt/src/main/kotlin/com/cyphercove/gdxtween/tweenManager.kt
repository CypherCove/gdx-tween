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

    /** Must be called for every frame of animation to advance all of the tweens in both runners. When using this function,
     * the step functions of the two runners should not be called directly.
     * @param delta The time passed since the last step. */
    fun TweenManager.stepTweens(dt: Float) {
        tweenRunner.step(dt)
    }

    /**
     * Removes any running or pending (delayed) tweens for the target object immediately, from both runners. No listener
     * will be called.
     * @param target The target object whose tween or tween chain is to be removed.
     * @return Whether a tween or tween chain existed and was removed.
     */
    fun TweenManager.clearTweens(target: Any): Boolean {
        return tweenRunner.clearTweens(target)
    }
}

/** A basic implementation of [TweenManager] that can be used as a delegate. */
class DelegateTweenManager : TweenManager {
    override val tweenRunner = TweenRunner()
}