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
package com.cyphercove.gdxtween;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Base class for tweens that target and interpolate a specific single object. An Ease function can be set to affect the
 * speed of the change over time.
 *
 * @param <T> The type of target the tween operates on.
 * @param <U> The type of this tween. A non-abstract subclass must specify itself as this type. This is not checked.
 */
public abstract class TargetTween<T, U> extends Tween<U> {
    private @NotNull Ease ease = Ease.linear;
    /**
     * If true, the tween is using a BlendableEase and also interrupted another Tween, so the Ease's start speeds are
     * to be overwritten the first time the tween begins.
     */
    private boolean isBlended;
    /**
     * The start speed that was on the interpolation before blending.
     */
    private float originalStartSpeed; // TODO Will be used when repeat is used.
    /**
     * The start speeds in /s units. Only filled if isBlended.
     */
    private final float[] startWorldSpeeds;
    /**
     * The start speeds used in /duration units. Only filled if isBlended.
     */
    private final float[] startSpeeds;
    /**
     * The start values, copied from the target object when the interpolation begins. Subclasses fill this in the begin() method.
     */
    private final float[] startValues;
    /**
     * The end values, filled in indirectly by the user before the tween is started.
     */
    private final float[] endValues;
    protected final int vectorSize;
    protected T target;
    private TargetTweenInterruptionListener<T> interruptionListener;
    private float duration = 1f;

    /**
     * @param vectorSize The number of elements in the vector being modified, or 1 for a scalar.
     */
    protected TargetTween(int vectorSize) {
        this.vectorSize = vectorSize;
        startSpeeds = new float[vectorSize];
        startWorldSpeeds = new float[vectorSize];
        startValues = new float[vectorSize];
        endValues = new float[vectorSize];
    }

    @Override
    protected void begin() {
        if (getTarget() == null)
            throw new IllegalStateException("Tween was started without setting a target: " + this);
        for (int i = 0; i < vectorSize; i++) {
            startSpeeds[i] = startWorldSpeeds[i] / getDuration();
        }
    }

    @Override
    protected void update() {
        if (isComplete()) {
            for (int i = 0; i < vectorSize; i++) {
                apply(i, endValues[i]);
            }
            applyAfter();
            applyAfterComplete();
            return;
        }
        float progress = getTime() / getDuration();
        if (isBlended) {
            Ease.BlendableEase blendable = (Ease.BlendableEase) ease;
            for (int i = 0; i < vectorSize; i++) {
                blendable.startSpeed(startSpeeds[i]);
                apply(i, ease.apply(progress, startValues[i], endValues[i]));
            }
        } else {
            for (int i = 0; i < vectorSize; i++) {
                apply(i, ease.apply(progress, startValues[i], endValues[i]));
            }
        }
        applyAfter();
    }

    /**
     * Called each frame. The new value is given and this method is responsible for applying it to the target.
     *
     * @param vectorIndex The element of the target to apply
     * @param value       The value to apply.
     */
    abstract protected void apply(int vectorIndex, float value);

    /**
     * Called each frame after {@link #apply(int, float)} has been called for all indices of the vector. This can be
     * used optionally to do any additional work to apply the values to the target object.
     */
    protected void applyAfter() {
    }

    /**
     * Called on the last frame of the tween after {@link #applyAfter()} has been called. This can be
     * used optionally to to ensure the final target values exactly match the originally set final values if the
     * calculations used for #{@link #apply(int, float)} having rounding error.
     */
    protected void applyAfterComplete() {
    }

    @Override
    public final float getDuration() {
        return duration;
    }

    /**
     * Set the length of this tween, not accounting for repeats.
     *
     * @param duration The tween's length.
     * @return This tween for building.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public final U duration(float duration) {
        this.duration = duration;
        return (U) this;
    }

    /**
     * Return this tween to its pool. Drops all external references. Frees any configurable eases.
     */
    @Override
    public void free() {
        super.free();
        ease.free();
        ease = Ease.linear;
        isBlended = false;
        duration = 1f;
        Arrays.fill(endValues, 0f);
        interruptionListener = null;
    }

    /**
     * Gets the reified type of the Tween's target.
     *
     * @return The type of target the tween operates on.
     */
    public abstract @NotNull Class<T> getTargetType();

    /**
     * Gets the target of this tween. Is null if the Tween has not been started.
     *
     * @return Twe tween target if it has been set.
     */
    @Nullable
    public final T getTarget() {
        return target;
    }

    /**
     * Sets the target object that will be transitioned by the tween. The {@linkplain TweenRunner}
     * only allows a single tween or tween chain per target object. Adding a new tween to the manager
     * with the same target will interrupt any ongoing tween with the same target.
     *
     * @param target The target object to be transitioned.
     * @return This tween for building.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public final U target(@NotNull T target) {
        this.target = target;
        return (U) this;
    }

    /**
     * Gets the currently set Ease function. The Ease instance should not be reused for other tweens because it might be
     * released to a Pool.
     *
     * @return The Ease function.
     */
    @NotNull
    public Ease getEase() {
        return ease;
    }

    /**
     * Sets the ease function to use for the transition. If the Ease is {@linkplain Ease.BlendableEase blendable}, and
     * this tween interrupts an ongoing tween, then the tween will begin at the speed of the tween it is interrupting,
     * ignoring any start speed that is set on the ease. If the tween repeats, the original start speed of the ease will
     * be used when it repeats.
     *
     * @param ease The ease function to use.
     * @return This tween for building.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public U ease(@NotNull Ease ease) {
        this.ease = ease;
        return (U) this;
    }

    /**
     * Sets the ease by wrapping an Interpolation as an Ease.
     *
     * @param interpolation Interpolation functino to use.
     * @return This tween for building.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public U ease(@NotNull Interpolation interpolation) {
        this.ease = Ease.wrap(interpolation);
        return (U) this;
    }

    /**
     * Called by subclasses in {@link #begin()} to set up the starting values.
     *
     * @param vectorIndex The index of the vector of values to set the start value of.
     * @param value       The value to set the given element of the vector to.
     */
    protected void setStartValue(int vectorIndex, float value) {
        startValues[vectorIndex] = value;
    }

    /**
     * Called by subclasses to set the end values. Protected so more intuitive methods can be exposed.
     *
     * @param vectorIndex The index of the vector of values to set the end value of.
     * @param value       The value to set the given element of the vector to.
     */
    protected void setEndValue(int vectorIndex, float value) {
        endValues[vectorIndex] = value;
    }

    /**
     * Called by subclasses to get end values. Protected so more intuitive methods can be exposed.
     *
     * @param vectorIndex The index of the vector of values to get the end value of.
     * @return The end value set for the given index.
     */
    protected float getEndValue(int vectorIndex) {
        return endValues[vectorIndex];
    }

    /**
     * Sets a listener to be called when the tween is interrupted by another tween. The interruption listener is only
     * called if this tween is currently running when interrupted. Queued tweens that haven't started will be cancelled
     * instead.
     * <p>
     * This listener is not called when the tween is interrupted by a call to {@link #cancel()}.
     *
     * @param listener The listener to call when the tween is interrupted, or null to clear any existing listener.
     * @return This tween for building.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public U interruptionListener(@Nullable TargetTweenInterruptionListener<T> listener) {
        this.interruptionListener = listener;
        return (U) this;
    }

    /**
     * Called by TweenRunner when this tween is first submitted before it is started.
     *
     * @return Starting world speeds array if this tween expects to be started at the speed of the tween it interrupts.
     * Otherwise null.
     */
    @Nullable
    protected float[] prepareToInterrupt() {
        if (getDuration() == 0 || !(ease instanceof Ease.BlendableEase))
            return null;
        isBlended = true;
        originalStartSpeed = ((Ease.BlendableEase) ease).getStartSpeed();
        float originalWorldStartSpeed = originalStartSpeed * getDuration();
        Arrays.fill(startWorldSpeeds, 0, vectorSize, originalWorldStartSpeed);
        return startWorldSpeeds;
    }

    @Override
    protected void collectInterrupters(Array<? super TargetTween<?, ?>> collection) {
        collection.add(this);
    }

    @Override
    protected boolean checkInterruption(TargetTween<?, ?> sourceTween, @Nullable float[] requestedWorldSpeeds) {
        if (isComplete()) {
            throw new IllegalStateException("Interruption checked on a complete tween: " + this); // TODO remove check
        }
        if (isCanceled()) {
            return false;
        }
        if (sourceTween.getClass() == getClass() && sourceTween.getTarget() == getTarget()) {
            cancel();
            if (requestedWorldSpeeds != null && isStarted()) {
                getWorldSpeeds(requestedWorldSpeeds);
            }
            if (interruptionListener != null) {
                interruptionListener.onTweenInterrupted(this);
            }
            return true;
        }
        return false;
    }

    protected void getWorldSpeeds(@NotNull float[] output) {
        if (isComplete()) {
            Arrays.fill(output, 0, vectorSize, 0f);
            return;
        }
        if (!isStarted() || getDuration() == 0f) {
            if (isBlended) {
                System.arraycopy(startWorldSpeeds, 0, output, 0, vectorSize);
            }
            Arrays.fill(output, 0, vectorSize, 0f);
            return;
        }
        float progress = getTime() / getDuration();
        for (int i = 0; i < vectorSize; i++) {
            if (isBlended) {
                ((Ease.BlendableEase) ease).startSpeed(startSpeeds[i]);
            }
            output[i] = ease.speed(progress, startValues[i], endValues[i]) * getDuration();
        }
    }
}
