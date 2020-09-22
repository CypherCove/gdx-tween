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
@file:Suppress("NOTHING_TO_INLINE", "unused")

package com.cyphercove.gdxtween

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.math.GridPoint3
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.cyphercove.gdxtween.math.Scalar
import com.cyphercove.gdxtween.math.ScalarInt
import com.cyphercove.gdxtween.targettweens.*

/**
 * Create a [SequenceTween] and sets it up using the provided [block].
 */
inline fun inSequence(block: SequenceTween.() -> Unit): SequenceTween {
    return Tweens.inSequence().apply(block)
}

/**
 * Create a [ParallelTween] and sets it up using the provided [block].
 */
inline fun inParallel(block: ParallelTween.() -> Unit): ParallelTween {
    return Tweens.inParallel().apply(block)
}

/** @see Ease.linear */
inline val GroupTween<*>.linear: Ease get() = Ease.linear

/** @see Ease.smoothstep */
inline val GroupTween<*>.smoothstep: Ease get() = Ease.smoothstep

/** @see Ease.smootherstep */
inline val GroupTween<*>.smootherstep: Ease get() = Ease.smootherstep

/** @see Ease.cubic */
inline fun GroupTween<*>.cubic() = Ease.cubic()

/** @see Ease.quintic */
inline fun GroupTween<*>.quintic() = Ease.quintic()

/**
 * Creates a [SequenceTween], sets it up using the provided [block], and adds it to this [GroupTween].
 */
inline fun GroupTween<*>.inSequence(block: SequenceTween.() -> Unit) {
    run(Tweens.inSequence().apply(block))
}

/**
 * Creates a [ParallelTween], sets it up using the provided [block], and adds it to this [GroupTween].
 */
inline fun GroupTween<*>.inParallel(block: ParallelTween.() -> Unit) {
    run(Tweens.inParallel().apply(block))
}

/**
 * Creates an [AccessorTween] and adds it to this [GroupTween].
 */
inline fun GroupTween<*>.to(target: AccessorTween.Accessor): AccessorTween =
        Tweens.to(target).also { run(it) }

/**
 * Creates a [ScalarTween] and adds it to this [GroupTween].
 */
inline fun GroupTween<*>.to(target: Scalar, endX: Float): ScalarTween =
        Tweens.to(target, endX).also { run(it) }

/**
 * Creates a [ScalarTween] and adds it to this [GroupTween].
 */
inline fun GroupTween<*>.to(target: Scalar, end: Scalar): ScalarTween =
        Tweens.to(target, end).also { run(it) }

/**
 * Creates a [Vector2Tween] and adds it to this [GroupTween].
 */
inline fun GroupTween<*>.to(target: Vector2, endX: Float, endY: Float): Vector2Tween =
        Tweens.to(target, endX, endY).also { run(it) }

/**
 * Creates a [Vector2Tween] and adds it to this [GroupTween].
 */
inline fun GroupTween<*>.to(target: Vector2, end: Vector2): Vector2Tween =
        Tweens.to(target, end).also { run(it) }

/**
 * Creates a [Vector3Tween] and adds it to this [GroupTween].
 */
inline fun GroupTween<*>.to(target: Vector3, endX: Float, endY: Float, endZ: Float): Vector3Tween =
        Tweens.to(target, endX, endY, endZ).also { run(it) }

/**
 * Creates a [Vector3Tween] and adds it to this [GroupTween].
 */
inline fun GroupTween<*>.to(target: Vector3, end: Vector3): Vector3Tween =
        Tweens.to(target, end).also { run(it) }

/**
 * Creates a [ScalarIntTween] and adds it to this [GroupTween].
 */
inline fun GroupTween<*>.to(target: ScalarInt, endX: Int): ScalarIntTween =
        Tweens.to(target, endX).also { run(it) }

/**
 * Creates a [ScalarIntTween] and adds it to this [GroupTween].
 */
inline fun GroupTween<*>.to(target: ScalarInt, end: ScalarInt): ScalarIntTween =
        Tweens.to(target, end).also { run(it) }

/**
 * Creates a [GridPoint2Tween] and adds it to this [GroupTween].
 */
inline fun GroupTween<*>.to(target: GridPoint2, endX: Int, endY: Int): GridPoint2Tween =
        Tweens.to(target, endX, endY).also { run(it) }

/**
 * Creates a [GridPoint2Tween] and adds it to this [GroupTween].
 */
inline fun GroupTween<*>.to(target: GridPoint2, end: GridPoint2): GridPoint2Tween =
        Tweens.to(target, end).also { run(it) }

/**
 * Creates a [GridPoint3Tween] and adds it to this [GroupTween].
 */
inline fun GroupTween<*>.to(target: GridPoint3, endX: Int, endY: Int, endZ: Int): GridPoint3Tween =
        Tweens.to(target, endX, endY, endZ).also { run(it) }

/**
 * Creates a [GridPoint3Tween] and adds it to this [GroupTween].
 */
inline fun GroupTween<*>.to(target: GridPoint3, end: GridPoint3): GridPoint3Tween =
        Tweens.to(target, end).also { run(it) }

/**
 * Creates a [ColorTween] and adds it to this [GroupTween].
 * @see Tweens.toRgb
 */
inline fun GroupTween<*>.toRgb(target: Color, endR: Float, endG: Float, endB: Float): ColorTween =
        Tweens.toRgb(target, endR, endG, endB).also { run(it) }

/**
 * Creates a [ColorTween] and adds it to this [GroupTween].
 * @see Tweens.toRgb
 */
inline fun GroupTween<*>.toRgb(target: Color, end: Color): ColorTween =
        Tweens.toRgb(target, end).also { run(it) }

/**
 * Creates an [AlphaTween] and adds it to this [GroupTween].
 * @see Tweens.toAlpha
 */
inline fun GroupTween<*>.toAlpha(target: Color, endA: Float): AlphaTween =
        Tweens.toAlpha(target, endA).also { run(it) }