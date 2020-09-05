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
package com.cyphercove.gdxtween.tweens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.cyphercove.gdxtween.math.ScalarInt;
import com.cyphercove.gdxtween.Ease;
import com.cyphercove.gdxtween.TargetingTween;
import com.cyphercove.gdxtween.math.Scalar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The starting point for acquiring tween instances.
 */
public final class Tweens {
    private Tweens() {
    }

    static private final IntMap<Pool<AccessorTween>> accessorPools = new IntMap<>();

    @NotNull
    static public <T extends TargetingTween<?, T>> T tween(Class<T> type) {
        Pool<T> pool = Pools.get(type);
        T tween = pool.obtain();
        tween.pool(pool);
        return tween;
    }

    @NotNull
    static private Pool<AccessorTween> getAccessorPool(final int vectorSize) {
        Pool<AccessorTween> pool = accessorPools.get(vectorSize);
        if (pool == null) {
            pool = new Pool<AccessorTween>(100) {
                @Override
                protected AccessorTween newObject() {
                    return new AccessorTween(vectorSize);
                }
            };
            accessorPools.put(vectorSize, pool);
        }
        return pool;
    }

    @NotNull
    static private AccessorTween accessorTween(int vectorSize) {
        Pool<AccessorTween> pool = getAccessorPool(vectorSize);
        AccessorTween tween = pool.obtain();
        tween.pool(pool);
        return tween;
    }

    /**
     * An AccessorTween.
     *
     * @param target   The Accessor that will targeted.
     * @param duration Duration of the tween.
     * @param ease     The Ease to use.
     * @return An AccessorTween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public AccessorTween accessor(@NotNull AccessorTween.Accessor target, float duration, @Nullable Ease ease) {
        return accessorTween(target.getNumberOfValues())
                .target(target)
                .duration(duration)
                .ease(ease);
    }

    /**
     * A ScalarTween.
     *
     * @param target   The Scalar whose value will be modified.
     * @param endX     Final value.
     * @param duration Duration of the tween.
     * @param ease     The Ease to use.
     * @return A ScalarTween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public ScalarTween to(@NotNull Scalar target, float endX, float duration, @Nullable Ease ease) {
        return tween(ScalarTween.class)
                .target(target)
                .end(endX)
                .duration(duration)
                .ease(ease);
    }

    /**
     * A ScalarTween.
     *
     * @param target   The Scalar whose value will be modified.
     * @param end      A Scalar containing the target value. The reference is not retained by the tween.
     * @param duration Duration of the tween.
     * @param ease     The Ease to use.
     * @return A ScalarTween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public ScalarTween to(@NotNull Scalar target, @NotNull Scalar end, float duration, @Nullable Ease ease) {
        return to(target, end.x, duration, ease);
    }

    /**
     * A Vector2Tween.
     *
     * @param target   The Vector2 whose values will be modified.
     * @param endX     Final x value.
     * @param endY     Final y value.
     * @param duration Duration of the tween.
     * @param ease     The Ease to use.
     * @return A Vector2Tween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public Vector2Tween to(@NotNull Vector2 target, float endX, float endY, float duration, @Nullable Ease ease) {
        return tween(Vector2Tween.class)
                .target(target)
                .end(endX, endY)
                .duration(duration)
                .ease(ease);
    }

    /**
     * A Vector2Tween.
     *
     * @param target   The Vector2 whose values will be modified.
     * @param end      A Vector2 containing the target values. The reference is not retained by the tween.
     * @param duration Duration of the tween.
     * @param ease     The Ease to use.
     * @return A Vector2Tween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public Vector2Tween to(@NotNull Vector2 target, Vector2 end, float duration, @Nullable Ease ease) {
        return to(target, end.x, end.y, duration, ease);
    }

    /**
     * A Vector3Tween.
     *
     * @param target   The Vector3 whose values will be modified.
     * @param endX     Final x value.
     * @param endY     Final y value.
     * @param endZ     Final z value.
     * @param duration Duration of the tween.
     * @param ease     The Ease to use.
     * @return A Vector3Tween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public Vector3Tween to(@NotNull Vector3 target, float endX, float endY, float endZ, float duration,
                                  @Nullable Ease ease) {
        return tween(Vector3Tween.class)
                .target(target)
                .end(endX, endY, endZ)
                .duration(duration)
                .ease(ease);
    }

    /**
     * A Vector3Tween.
     *
     * @param target   The Vector3 whose values will be modified.
     * @param end      A Vector3 containing the target values. The reference is not retained by the tween.
     * @param duration Duration of the tween.
     * @param ease     The Ease to use.
     * @return A Vector3Tween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public Vector3Tween to(@NotNull Vector3 target, @NotNull Vector3 end, float duration, @Nullable Ease ease) {
        return to(target, end.x, end.y, end.z, duration, ease);
    }

    /**
     * A ScalarIntTween.
     *
     * @param target   The ScalarInt whose value will be modified.
     * @param endX     Final value.
     * @param duration Duration of the tween.
     * @param ease     The Ease to use.
     * @return A ScalarIntTween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public ScalarIntTween to(@NotNull ScalarInt target, int endX, float duration, @Nullable Ease ease) {
        return tween(ScalarIntTween.class)
                .target(target)
                .end(endX)
                .duration(duration)
                .ease(ease);
    }

    /**
     * A ScalarIntTween.
     *
     * @param target   The ScalarInt whose value will be modified.
     * @param end      A ScalarInt containing the target value. The reference is not retained by the tween.
     * @param duration Duration of the tween.
     * @param ease     The Ease to use.
     * @return A ScalarIntTween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public ScalarIntTween to(@NotNull ScalarInt target, @NotNull ScalarInt end, float duration,
                                    @Nullable Ease ease) {
        return to(target, end.x, duration, ease);
    }

    /**
     * A GridPoint2Tween.
     *
     * @param target   The GridPoint2 whose values will be modified.
     * @param endX     Final x value.
     * @param endY     Final y value.
     * @param duration Duration of the tween.
     * @param ease     The Ease to use.
     * @return A GridPoint2Tween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public GridPoint2Tween to(@NotNull GridPoint2 target, int endX, int endY, float duration,
                                     @Nullable Ease ease) {
        return tween(GridPoint2Tween.class)
                .target(target)
                .end(endX, endY)
                .duration(duration)
                .ease(ease);
    }

    /**
     * A GridPoint2Tween.
     *
     * @param target   The GridPoint2 whose values will be modified.
     * @param end      A GridPoint2 containing the target values. The reference is not retained by the tween.
     * @param duration Duration of the tween.
     * @param ease     The Ease to use.
     * @return A GridPoint2Tween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public GridPoint2Tween to(@NotNull GridPoint2 target, GridPoint2 end, float duration, @Nullable Ease ease) {
        return to(target, end.x, end.y, duration, ease);
    }

    /**
     * A GridPoint3Tween.
     *
     * @param target   The GridPoint3 whose values will be modified.
     * @param endX     Final x value.
     * @param endY     Final y value.
     * @param endZ     Final z value.
     * @param duration Duration of the tween.
     * @param ease     The Ease to use.
     * @return A GridPoint3Tween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public GridPoint3Tween to(@NotNull GridPoint3 target, float endX, float endY, float endZ, float duration,
                                  @Nullable Ease ease) {
        return tween(GridPoint3Tween.class)
                .target(target)
                .end(endX, endY, endZ)
                .duration(duration)
                .ease(ease);
    }

    /**
     * A GridPoint3Tween.
     *
     * @param target   The GridPoint3 whose values will be modified.
     * @param end      A GridPoint3 containing the target values. The reference is not retained by the tween.
     * @param duration Duration of the tween.
     * @param ease     The Ease to use.
     * @return A GridPoint3Tween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public GridPoint3Tween to(@NotNull GridPoint3 target, @NotNull GridPoint3 end, float duration,
                                     @Nullable Ease ease) {
        return to(target, end.x, end.y, end.z, duration, ease);
    }

    /**
     * A ColorTween which only modifies the RGB channels of a Color. Defaults to LinearRgb color space. A ColorTween can
     * run on the same target as an {@link AlphaTween} without them interrupting each other.
     *
     * @param target   The Color whose RGB will be modified.
     * @param endR     Final red value.
     * @param endG     Final green value.
     * @param endB     Final blue value.
     * @param duration Duration of the tween.
     * @param ease     The Ease to use.
     * @return A ColorTween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public ColorTween toRgb(@NotNull Color target, float endR, float endG, float endB, float duration,
                                   @Nullable Ease ease) {
        return tween(ColorTween.class)
                .target(target)
                .end(endR, endG, endB)
                .duration(duration)
                .ease(ease);
    }

    /**
     * A ColorTween which only modifies the RGB channels of a Color. Defaults to LinearRgb color space. A ColorTween can
     * run on the same target as an {@link AlphaTween} without them interrupting each other.
     *
     * @param target   The Color whose RGB will be modified.
     * @param end      A Color containing the the target values. The reference is not retained by the tween.
     * @param duration Duration of the tween.
     * @param ease     The Ease to use.
     * @return A ColorTween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public ColorTween toRgb(@NotNull Color target, @NotNull Color end, float duration,
                                         @Nullable Ease ease) {
        return tween(ColorTween.class)
                .target(target)
                .end(end.r, end.g, end.b)
                .duration(duration)
                .ease(ease);
    }

    /**
     * An AlphaTween that modifies the alpha channel of the Color target only. It can run on the same target as a
     * {@link ColorTween} without them interrupting each other.
     *
     * @param target   The Color whose alpha will be modified.
     * @param endA     Final alpha value.
     * @param duration Duration of the tween.
     * @param ease     The Ease to use.
     * @return An AlphaTween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public AlphaTween toAlpha(@NotNull Color target, float endA, float duration, @Nullable Ease ease) {
        return tween(AlphaTween.class)
                .target(target)
                .end(endA)
                .duration(duration)
                .ease(ease);
    }
}
