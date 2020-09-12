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

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.math.GridPoint3
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.cyphercove.gdxtween.math.Scalar
import com.cyphercove.gdxtween.math.ScalarInt
import com.cyphercove.gdxtween.targettweens.AccessorTween

/**
 * Allows setting up a tween using a lambda with a [TweenBuilder] receiver, so the static references [Tweens] and [Ease]
 *  can be omitted.
 */
inline fun <T : Tween<*>> tween(tweenSetup: TweenBuilder.() -> T): T {
    return tweenSetup(TweenBuilder)
}

/**
 * Pass-through functions and properties for [Tweens] and [Ease]. Can be subclassed to add additional members.
 */
open class TweenBuilder {

    companion object : TweenBuilder()

    /** @see Ease.linear */
    inline val linear: Ease get() = Ease.linear
    /** @see Ease.smoothstep */
    inline val smoothstep: Ease get() = Ease.smoothstep
    /** @see Ease.smootherstep */
    inline val smootherstep: Ease get() = Ease.smootherstep
    /** @see Ease.cubic */
    fun cubic() = Ease.cubic()
    /** @see Ease.quintic */
    fun quintic() = Ease.quintic()

    /** @see Tweens.inSequence */
    fun inSequence() = Tweens.inSequence()
    /** @see Tweens.inParallel */
    fun inParallel() = Tweens.inParallel()
    fun to(target: AccessorTween.Accessor) = Tweens.to(target)
    fun to(target: Scalar, endX: Float) = Tweens.to(target, endX)
    fun to(target: Scalar, end: Scalar) = Tweens.to(target, end)
    fun to(target: Vector2, endX: Float, endY: Float) = Tweens.to(target, endX, endY)
    fun to(target: Vector2, end: Vector2) = Tweens.to(target, end)
    fun to(target: Vector3, endX: Float, endY: Float, endZ: Float) = Tweens.to(target, endX, endY, endZ)
    fun to(target: Vector3, end: Vector3) = Tweens.to(target, end)
    fun to(target: ScalarInt, endX: Int) = Tweens.to(target, endX)
    fun to(target: ScalarInt, end: ScalarInt) = Tweens.to(target, end)
    fun to(target: GridPoint2, endX: Int, endY: Int) = Tweens.to(target, endX, endY)
    fun to(target: GridPoint2, end: GridPoint2) = Tweens.to(target, end)
    fun to(target: GridPoint3, endX: Int, endY: Int, endZ: Int) = Tweens.to(target, endX, endY, endZ)
    fun to(target: GridPoint3, end: GridPoint3) = Tweens.to(target, end)
    /** @see Tweens.toRgb */
    fun toRgb(target: Color, endR: Float, endG: Float, endB: Float) = Tweens.toRgb(target, endR, endG, endB)
    /** @see Tweens.toRgb */
    fun toRgb(target: Color, end: Color) = Tweens.toRgb(target, end)
    /** @see Tweens.toAlpha */
    fun toAlpha(target: Color, endA: Float) = Tweens.toAlpha(target, endA)

}