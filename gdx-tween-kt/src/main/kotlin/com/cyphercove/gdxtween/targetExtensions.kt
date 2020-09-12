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

fun Scalar.tweenTo(endX: Float) = Tweens.to(this, endX)
fun Scalar.tweenTo(end: Scalar) = Tweens.to(this, end)
fun Vector2.tweenTo(endX: Float, endY: Float) = Tweens.to(this, endX, endY)
fun Vector2.tweenTo(end: Vector2) = Tweens.to(this, end)
fun Vector3.tweenTo(endX: Float, endY: Float, endZ: Float) = Tweens.to(this, endX, endY, endZ)
fun Vector3.tweenTo(end: Vector3) = Tweens.to(this, end)
fun ScalarInt.tweenTo(endX: Int) = Tweens.to(this, endX)
fun ScalarInt.tweenTo(end: ScalarInt) = Tweens.to(this, end)
fun GridPoint2.tweenTo(endX: Int, endY: Int) = Tweens.to(this, endX, endY)
fun GridPoint2.tweenTo(end: GridPoint2) = Tweens.to(this, end)
fun GridPoint3.tweenTo(endX: Int, endY: Int, endZ: Int) = Tweens.to(this, endX, endY, endZ)
fun GridPoint3.tweenTo(end: GridPoint3) = Tweens.to(this, end)
/** @see Tweens.toRgb */
fun Color.tweenRgbTo(endR: Float, endG: Float, endB: Float) = Tweens.toRgb(this, endR, endG, endB)
/** @see Tweens.toRgb */
fun Color.tweenRgbTo(end: Color) = Tweens.toRgb(this, end)
/** @see Tweens.toAlpha */
fun Color.tweenAlphaTo(endA: Float) = Tweens.toAlpha(this, endA)