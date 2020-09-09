/* ******************************************************************************
 * Copyright 2019 See AUTHORS file.
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
package com.cyphercove.gdxtween;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.cyphercove.gdxtween.math.Scalar;
import com.cyphercove.gdxtween.math.ScalarInt;
import com.cyphercove.gdxtween.targettweens.*;
import org.jetbrains.annotations.NotNull;

/**
 * A utility class containing entry-points for creating tweens.
 */
public final class Tweens {

    /**
     * Create a SequenceTween.
     * @return A SequenceTween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public SequenceTween inSequence() {
        return SequenceTween.newInstance();
    }

    /**
     * Create a ParallelTween.
     * @return A ParallelTween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public ParallelTween inParallel() {
        return ParallelTween.newInstance();
    }

    @NotNull
    static public DelayTween delay(float duration) {
        return DelayTween.newInstance().duration(duration);
    }

    /**
     * Create an AccessorTween for the given target.
     *
     * @param target   The Accessor that will targeted.
     * @param duration Duration of the tween.
     * @return An AccessorTween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public AccessorTween to(@NotNull AccessorTween.Accessor target, float duration) {
        return AccessorTween.newInstance(target.getNumberOfValues())
                .target(target)
                .duration(duration);
    }

    /**
     * Create a ScalarTween for the given target.
     *
     * @param target   The Scalar whose value will be modified.
     * @param endX     Final value.
     * @param duration Duration of the tween.
     * @return A ScalarTween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public ScalarTween to(@NotNull Scalar target, float endX, float duration) {
        return ScalarTween.newInstance()
                .target(target)
                .end(endX)
                .duration(duration);
    }

    /**
     * Create a ScalarTween for the given target.
     *
     * @param target   The Scalar whose value will be modified.
     * @param end      A Scalar containing the target value. The reference is not retained by the tween.
     * @param duration Duration of the tween.
     * @return A ScalarTween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public ScalarTween to(@NotNull Scalar target, @NotNull Scalar end, float duration) {
        return to(target, end.x, duration);
    }

    /**
     * Create a Vector2Tween for the given target.
     *
     * @param target   The Vector2 whose values will be modified.
     * @param endX     Final x value.
     * @param endY     Final y value.
     * @param duration Duration of the tween.
     * @return A Vector2Tween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public Vector2Tween to(@NotNull Vector2 target, float endX, float endY, float duration) {
        return Vector2Tween.newInstance()
                .target(target)
                .end(endX, endY)
                .duration(duration);
    }

    /**
     * Create a Vector2Tween for the given target.
     *
     * @param target   The Vector2 whose values will be modified.
     * @param end      A Vector2 containing the target values. The reference is not retained by the tween.
     * @param duration Duration of the tween.
     * @return A Vector2Tween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public Vector2Tween to(@NotNull Vector2 target, Vector2 end, float duration) {
        return to(target, end.x, end.y, duration);
    }

    /**
     * Create a Vector3Tween for the given target.
     *
     * @param target   The Vector3 whose values will be modified.
     * @param endX     Final x value.
     * @param endY     Final y value.
     * @param endZ     Final z value.
     * @param duration Duration of the tween.
     * @return A Vector3Tween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public Vector3Tween to(@NotNull Vector3 target, float endX, float endY, float endZ, float duration) {
        return Vector3Tween.newInstance()
                .target(target)
                .end(endX, endY, endZ)
                .duration(duration);
    }

    /**
     * Create a Vector3Tween for the given target.
     *
     * @param target   The Vector3 whose values will be modified.
     * @param end      A Vector3 containing the target values. The reference is not retained by the tween.
     * @param duration Duration of the tween.
     * @return A Vector3Tween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public Vector3Tween to(@NotNull Vector3 target, @NotNull Vector3 end, float duration) {
        return to(target, end.x, end.y, end.z, duration);
    }

    /**
     * Create a ScalarIntTween for the given target.
     *
     * @param target   The ScalarInt whose value will be modified.
     * @param endX     Final value.
     * @param duration Duration of the tween.
     * @return A ScalarIntTween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public ScalarIntTween to(@NotNull ScalarInt target, int endX, float duration) {
        return ScalarIntTween.newInstance()
                .target(target)
                .end(endX)
                .duration(duration);
    }

    /**
     * Create a ScalarIntTween for the given target.
     *
     * @param target   The ScalarInt whose value will be modified.
     * @param end      A ScalarInt containing the target value. The reference is not retained by the tween.
     * @param duration Duration of the tween.
     * @return A ScalarIntTween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public ScalarIntTween to(@NotNull ScalarInt target, @NotNull ScalarInt end, float duration) {
        return to(target, end.x, duration);
    }

    /**
     * Create a GridPoint2Tween for the given target.
     *
     * @param target   The GridPoint2 whose values will be modified.
     * @param endX     Final x value.
     * @param endY     Final y value.
     * @param duration Duration of the tween.
     * @return A GridPoint2Tween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public GridPoint2Tween to(@NotNull GridPoint2 target, int endX, int endY, float duration) {
        return GridPoint2Tween.newInstance()
                .target(target)
                .end(endX, endY)
                .duration(duration);
    }

    /**
     * Create a GridPoint2Tween for the given target.
     *
     * @param target   The GridPoint2 whose values will be modified.
     * @param end      A GridPoint2 containing the target values. The reference is not retained by the tween.
     * @param duration Duration of the tween.
     * @return A GridPoint2Tween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public GridPoint2Tween to(@NotNull GridPoint2 target, GridPoint2 end, float duration) {
        return to(target, end.x, end.y, duration);
    }

    /**
     * Create a GridPoint3Tween for the given target.
     *
     * @param target   The GridPoint3 whose values will be modified.
     * @param endX     Final x value.
     * @param endY     Final y value.
     * @param endZ     Final z value.
     * @param duration Duration of the tween.
     * @return A GridPoint3Tween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public GridPoint3Tween to(@NotNull GridPoint3 target, float endX, float endY, float endZ, float duration) {
        return GridPoint3Tween.newInstance()
                .target(target)
                .end(endX, endY, endZ)
                .duration(duration);
    }

    /**
     * Create a GridPoint3Tween for the given target.
     *
     * @param target   The GridPoint3 whose values will be modified.
     * @param end      A GridPoint3 containing the target values. The reference is not retained by the tween.
     * @param duration Duration of the tween.
     * @return A GridPoint3Tween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public GridPoint3Tween to(@NotNull GridPoint3 target, @NotNull GridPoint3 end, float duration) {
        return to(target, end.x, end.y, end.z, duration);
    }

    /**
     * Create a ColorTween for the given target, which only modifies the RGB channels of a Color. Defaults to LinearRgb
     * color space. A ColorTween can run on the same target as an {@link AlphaTween} without them interrupting each other.
     *
     * @param target   The Color whose RGB will be modified.
     * @param endR     Final red value.
     * @param endG     Final green value.
     * @param endB     Final blue value.
     * @param duration Duration of the tween.
     * @return A ColorTween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public ColorTween toRgb(@NotNull Color target, float endR, float endG, float endB, float duration) {
        return ColorTween.newInstance()
                .target(target)
                .end(endR, endG, endB)
                .duration(duration);
    }

    /**
     * Create a ColorTween for the given target, which only modifies the RGB channels of a Color. Defaults to LinearRgb
     * color space. A ColorTween can run on the same target as an {@link AlphaTween} without them interrupting each other.
     *
     * @param target   The Color whose RGB will be modified.
     * @param end      A Color containing the the target values. The alpha value is ignored. The reference is not
     *                 retained by the tween.
     * @param duration Duration of the tween.
     * @return A ColorTween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public ColorTween toRgb(@NotNull Color target, @NotNull Color end, float duration) {
        return ColorTween.newInstance()
                .target(target)
                .end(end.r, end.g, end.b)
                .duration(duration);
    }

    /**
     * Create an AlphaTween for the givenTarget, which modifies the alpha channel of the Color target only. An AlphaTween
     * can run on the same target as a {@link ColorTween} without them interrupting each other.
     *
     * @param target   The Color whose alpha will be modified.
     * @param endA     Final alpha value.
     * @param duration Duration of the tween.
     * @return An AlphaTween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public AlphaTween toAlpha(@NotNull Color target, float endA, float duration) {
        return AlphaTween.newInstance()
                .target(target)
                .end(endA)
                .duration(duration);
    }
}
