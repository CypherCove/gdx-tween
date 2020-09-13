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

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

import org.jetbrains.annotations.NotNull;

/**
 * Easing functions that can be used with {@linkplain TargetTween Tweens}. Non-configurable eases are provided
 * as static immutable members. Configurable eases are provided via function calls. These configurable
 * eases are pulled from {@linkplain Pool Pools} and are automatically returned to their pools when their associated
 * tweens are completed. Do not assign a single configurable ease to multiple Tweens.
 */
public abstract class Ease {
    /**
     * @param a     The progress in the interpolation (for example, elapsed time over total duration).
     *              The value is clamped between 0 and 1.
     * @param start Where the value should be when {@code a} is 0
     * @param end   Where the value should be when {@code a} is 1
     * @return the interpolated value between the given start and end values
     */
    public abstract float apply(float a, float start, float end);

    /**
     * Provides the derivative of the ease function at a given amount of progress.
     *
     * @param a     The progress in the interpolation (for example, elapsed time over total duration).
     *              The value is clamped between 0 and 1.
     * @param start Where the eased value should be when {@code a} is 0
     * @param end   Where the eased value should be when {@code a} is 1
     * @return the speed of the easing at the current amount of progress {@code a} in units of
     * value change over fraction of total duration. Multiplying this value by the total duration of
     * the ease gives the world speed of the ease.
     */
    public abstract float speed(float a, float start, float end);

    /**
     * If the ease is poolable, this returns it to its pool. The provided immutable eases are not poolable. This is
     * called automatically when an Ease is assigned to a {@link TargetTween} and that tween is freed (which in turn
     * happens automatically when the Tween is completed, cancelled or interrupted).
     */
    public void free() {
    }

    /**
     * If this Ease is mutable, returns another instance from a pool with duplicate settings. Otherwise, returns
     * itself.
     * @return An Ease of the same type as this one that is safe to pass to a different tween than the original.
     */
    @NotNull
    public Ease copyOrSelf() {
        return this;
    }

    /**
     * A mutable Ease that supports setting the starting speed of the function.
     */
    public abstract static class BlendInEase extends Ease {
        /**
         * Set the start speed of the function, in units of value change over total duration. World
         * speed should be divided by total ease duration before passing it in.
         *
         * @param startSpeed The beginning speed for the transition.
         * @return The Ease for building.
         */
        @NotNull
        public abstract BlendInEase startSpeed(float startSpeed);

        /**
         * @return the start speed of the function.
         */
        public abstract float getStartSpeed();

        @Override
        @NotNull
        public abstract BlendInEase copyOrSelf();
    }

    /**
     * A BlendInEase that also supports setting the ending speed of the function.
     */
    public abstract static class BlendInOutEase extends BlendInEase {

        /**
         * Set the end speed of the function, in units of value change over total duration. World
         * speed should be divided by total ease duration before passing it in.
         *
         * @param endSpeed The final speed for the transition.
         * @return The Ease for building.
         */
        @NotNull
        public abstract BlendInOutEase endSpeed(float endSpeed);

        /**
         * @return the end speed of the function.
         */
        public abstract float getEndSpeed();
    }

    /* Immutable static eases --------------------------------------------------------------------*/

    private static Ease createLinear() {
        return new Ease() {

            @Override
            public float apply(float a, float start, float end) {
                if (a <= 0)
                    return start;
                if (a >= 1)
                    return end;
                return a * (end - start) + start;
            }

            @Override
            public float speed(float a, float start, float end) {
                return end - start;
            }

        };
    }

    /**
     * The default Ease used by TargetTweens if none is set. This is checked by identity to see if one has been
     * explicitly set on the tween.
     */
    @NotNull
    static Ease DEFAULT = createLinear();

    @NotNull
    public static Ease linear = createLinear();

    /**
     * A cubic Hermite polynomial that starts and ends with zero speed.
     */
    @NotNull
    public static Ease smoothstep = new Ease() {
        @Override
        public float apply(float a, float start, float end) {
            if (a <= 0)
                return start;
            if (a >= 1)
                return end;
            return a * a * (3 - 2 * a) * (end - start) + start;
        }

        @Override
        public float speed(float a, float start, float end) {
            if (a <= 0 || a >= 1)
                return 0;
            return a * (6 - 6 * a) * (end - start);
        }

    };

    /**
     * A quintic Hermite polynomial that starts and ends with zero speed and zero acceleration. By Ken Perlin.
     */
    @NotNull
    public static Ease smootherstep = new Ease() {
        @Override
        public float apply(float a, float start, float end) {
            if (a <= 0)
                return start;
            if (a >= 1)
                return end;
            return a * a * a * (a * (a * 6 - 15) + 10) * (end - start) + start;
        }

        @Override
        public float speed(float a, float start, float end) {
            if (a <= 0 || a >= 1)
                return 0;
            return a * a * (a * (a * 30 - 60) + 30) * (end - start);
        }

    };

    /* Mutable eases --------------------------------------------------------------------------*/

    /**
     * A cubic Hermite polynomial function whose starting and ending speeds can be specified.  The
     * function is equivalent to {@link #smoothstep} if the starting and ending speeds are 0. This ease can
     * be freed to a pool. Do not assign a single instance to multiple {@linkplain Tween Tweens}.
     */
    public static class CubicHermite extends BlendInOutEase {
        float startSpeed, endSpeed;

        @Override
        @NotNull
        public CubicHermite startSpeed(float startSpeed) {
            this.startSpeed = startSpeed;
            return this;
        }

        @Override
        @NotNull
        public CubicHermite endSpeed(float endSpeed) {
            this.endSpeed = endSpeed;
            return this;
        }

        @Override
        public float getStartSpeed() {
            return startSpeed;
        }

        @Override
        public float getEndSpeed() {
            return endSpeed;
        }

        @Override
        public @NotNull CubicHermite copyOrSelf() {
            return cubicPool.obtain().startSpeed(startSpeed).endSpeed(endSpeed);
        }

        @Override
        public void free() {
            startSpeed = 0;
            endSpeed = 0;
            cubicPool.free(this);
        }

        @Override
        public float apply(float a, float start, float end) {
            if (startSpeed == 0 && endSpeed == 0)
                return smoothstep.apply(a, start, end);
            if (a <= 0)
                return start;
            if (a >= 1)
                return end;

            float a2 = a * a;
            float a3 = a2 * a;
            return start + (end - start) * (-2 * a3 + 3 * a2) +
                    startSpeed * (a3 - 2 * a2 + a) +
                    endSpeed * (a3 - a2);
        }

        @Override
        public float speed(float a, float start, float end) {
            if (a <= 0)
                return startSpeed;
            if (a >= 1)
                return endSpeed;

            float a2 = a * a;
            float a2x3 = a2 * 3;
            return 6 * (end - start) * (a - a2) +
                    startSpeed * (a2x3 - 4 * a + 1) +
                    endSpeed * (a2x3 - 2 * a);
        }
    }

    private static final Pool<CubicHermite> cubicPool = new Pool<CubicHermite>() {
        @Override
        protected CubicHermite newObject() {
            return new CubicHermite();
        }
    };

    /**
     * @return A mutable cubic Hermite polynomial ease. Do not assign to multiple tweens.
     */
    @NotNull
    public static CubicHermite cubic() {
        return cubicPool.obtain();
    }

    /**
     * A quintic Hermite polynomial function whose starting and ending speeds can be specified. The starting and
     * ending acceleration will be zero. The function is equivalent to {@link #smootherstep} if the starting
     * and ending speeds are 0. This ease can be freed to a pool. Do not assign a single instance to
     * multiple {@linkplain TargetTween Tweens}.
     */
    public static class QuinticHermite extends BlendInOutEase {
        float startSpeed, endSpeed;

        @Override
        @NotNull
        public QuinticHermite startSpeed(float startSpeed) {
            this.startSpeed = startSpeed;
            return this;
        }

        @Override
        public float getStartSpeed() {
            return startSpeed;
        }

        @Override
        @NotNull
        public QuinticHermite endSpeed(float endSpeed) {
            this.endSpeed = endSpeed;
            return this;
        }

        @Override
        public float getEndSpeed() {
            return endSpeed;
        }

        @Override
        public @NotNull QuinticHermite copyOrSelf() {
            return quinticPool.obtain().startSpeed(startSpeed).endSpeed(endSpeed);
        }

        @Override
        public void free() {
            startSpeed = 0f;
            endSpeed = 0f;
            quinticPool.free(this);
        }

        @Override
        public float apply(float a, float start, float end) {
            if (startSpeed == 0 && endSpeed == 0)
                return smootherstep.apply(a, start, end);
            if (a <= 0)
                return start;
            if (a >= 1)
                return end;

            float a3 = a * a * a;
            float a4 = a3 * a;
            float a5x3 = a4 * a * 3;
            return start + (end - start) * (2 * a5x3 - 15 * a4 + 10 * a3) +
                    startSpeed * (-a5x3 + 8 * a4 - 6 * a3 + a) +
                    endSpeed * (-a5x3 + 7 * a4 - 4 * a3);
        }

        @Override
        public float speed(float a, float start, float end) {
            if (a <= 0)
                return startSpeed;
            if (a >= 1)
                return endSpeed;

            float a2 = a * a;
            float a3 = a2 * a;
            float a4 = a2 * a2;
            float minusA4x15 = -15 * a4;
            return 30 * (end - start) * (a4 - 2 * a3 + a2) +
                    startSpeed * (minusA4x15 + 32 * a3 - 18 * a2 + 1) +
                    endSpeed * (minusA4x15 + 28 * a3 - 12 * a2);
        }
    }

    private static final Pool<QuinticHermite> quinticPool = new Pool<QuinticHermite>() {
        @Override
        protected QuinticHermite newObject() {
            return new QuinticHermite();
        }
    };

    /**
     * @return A mutable quintic Hermite polynomial ease. Do not assign to multiple tweens.
     */
    @NotNull
    public static QuinticHermite quintic() {
        return quinticPool.obtain();
    }

    public static class InterpolationWrapper extends Ease {
        Interpolation interpolation = Interpolation.linear;
        float precision = 0.001f;
        float halfPrecision = 0.0005f;

        @NotNull
        public Interpolation getInterpolation() {
            return interpolation;
        }

        public void setInterpolation(@NotNull Interpolation interpolation) {
            this.interpolation = interpolation;
        }

        @Override
        public void free() {
            interpolation = Interpolation.linear;
            precision = 0.001f;
            halfPrecision = 0.0005f;
            Pools.free(this);
        }

        /**
         * Sets the precision for calculating this interpolation's speed. This is the fraction of total
         * duration used for calculating the speed.
         *
         * @param precision The new precision.
         */
        public void setSpeedPrecision(float precision) {
            this.precision = precision;
            halfPrecision = 0.5f * precision;
        }

        @Override
        public float apply(float a, float start, float end) {
            return interpolation.apply(start, end, MathUtils.clamp(a, 0f, 1f));
        }

        @Override
        public float speed(float a, float start, float end) {
            if (a <= halfPrecision)
                return interpolation.apply(precision) * (end - start) / precision;

            if (a >= 1f - halfPrecision)
                return interpolation.apply(1f - precision) * (end - start) / precision;

            return (interpolation.apply(a + halfPrecision) - interpolation.apply(a - halfPrecision))
                    * (end - start) / precision;
        }
    }

    /**
     * A wrapper for {@linkplain Interpolation Interpolations} so they can be used as Eases. This ease can
     * be freed to a pool. Do not assign a single instance to multiple {@linkplain TargetTween Tweens}.
     *
     * @param interpolation The Interpolation to wrap.
     * @return An Ease that uses the function of an Interpolation.
     */
    @NotNull
    public static InterpolationWrapper wrap(@NotNull Interpolation interpolation) {
        InterpolationWrapper ease = Pools.obtain(InterpolationWrapper.class);
        ease.setInterpolation(interpolation);
        return ease;
    }

    /**
     * A wrapper for {@linkplain Interpolation Interpolations} so they can be used as Eases. This ease can
     * be freed to a pool. Do not assign a single instance to multiple {@linkplain TargetTween Tweens}.
     *
     * @param interpolation  The Interpolation to wrap.
     * @param speedPrecision The precision to use when calculating the speed of this ease. This is the
     *                       step size to use as a fraction of total duration.
     * @return An Ease that uses the function of an Interpolation.
     */
    @NotNull
    public static InterpolationWrapper wrap(@NotNull Interpolation interpolation, float speedPrecision) {
        InterpolationWrapper ease = Pools.obtain(InterpolationWrapper.class);
        ease.setInterpolation(interpolation);
        ease.setSpeedPrecision(speedPrecision);
        return ease;
    }
}
