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

import java.util.Arrays;

/**
 * Base class for tweens that target and interpolate a specific single object. An Ease function can be set to affect the
 * speed of the change over time.
 *
 * @param <T>  The type of this tween. A non-abstract subclass must specify itself as this type. This is not checked.
 * @param <TG> The type of target the tween operates on.
 */
public abstract class TargetTween<T, TG> extends Tween<T> {
    private Ease ease = Ease.DEFAULT;
    /**
     * If true, the tween is using a {@link com.cyphercove.gdxtween.Ease.BlendInEase} and also interrupted another tween,
     * so the Ease's start speeds are to be overwritten the first time the tween begins.
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
    protected TG target;
    private TweenInterruptionListener<T> interruptionListener;
    private float duration = -1f;

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
        duration = getDuration(); // Ensure duration is set (Default 0).
        for (int i = 0; i < vectorSize; i++) {
            startSpeeds[i] = startWorldSpeeds[i] * duration;
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
        float progress = getTime() / duration;
        if (isBlended) {
            Ease.BlendInEase blendInEase = (Ease.BlendInEase) ease;
            for (int i = 0; i < vectorSize; i++) {
                blendInEase.startSpeed(startSpeeds[i]);
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

    /**
     * Gets the reified type of the Tween's target.
     *
     * @return The type of target the tween operates on.
     */
    public abstract Class<TG> getTargetType();

    /**
     * Gets the target of this tween. Is null if the Tween has not been started.
     *
     * @return Twe tween target if it has been set.
     */
    public final TG getTarget() {
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
    public final T target(TG target) {
        if (isAttached())
            logMutationAfterAttachment();
        else
            this.target = target;
        return (T) this;
    }

    @Override
    public final float getDuration() {
        return Math.max(duration, 0f);
    }

    /**
     * Set the length of this tween.
     *
     * @param duration The tween's length.
     * @return This tween for building.
     */
    @SuppressWarnings("unchecked")
    public final T duration(float duration) {
        if (isAttached())
            logMutationAfterAttachment();
        else
            this.duration = duration;
        return (T) this;
    }

    /**
     * Gets the currently set Ease function. The Ease instance should not be reused for other tweens because it might be
     * released to a Pool. If none is set and this tween is a child of a GroupTween with a default Ease, calling this
     * getter will automatically set the ease to a copy of its parent's default.
     *
     * @return The Ease function.
     */
    public Ease getEase() {
        return ease;
    }

    /**
     * Sets the ease function to use for the transition. If the Ease is {@linkplain Ease.BlendInEase blendable}, and
     * this tween interrupts an ongoing tween, then the tween will begin at the speed of the tween it is interrupting,
     * ignoring any start speed that is set on the ease.
     * <p>
     * If the Ease is mutable, it should not be shared with other tweens. Its start speed will be modified if it this
     * tween interrupts another.
     *
     * @param ease The ease function to use.
     * @return This tween for building.
     */
    @SuppressWarnings("unchecked")
    public T ease(Ease ease) {
        if (isAttached())
            logMutationAfterAttachment();
        else
            this.ease = ease;
        return (T) this;
    }

    /**
     * Sets the ease by wrapping an Interpolation as an Ease.
     *
     * @param interpolation Interpolation function to use.
     * @return This tween for building.
     */
    @SuppressWarnings("unchecked")
    public T ease(Interpolation interpolation) {
        if (isAttached())
            logMutationAfterAttachment();
        else
            this.ease = Ease.wrap(interpolation);
        return (T) this;
    }

    /**
     * Sets the duration and the ease function to use for the transition.
     *
     * @param duration The tween's length.
     * @param ease     The ease function to use.
     * @return This tween for building.
     * @see #duration(float)
     * @see #ease(Ease)
     */
    @SuppressWarnings("unchecked")
    public T using(float duration, Ease ease) {
        if (isAttached())
            logMutationAfterAttachment();
        else {
            this.duration = duration;
            this.ease = ease;
        }
        return (T) this;
    }

    /**
     * Sets the duration and the ease function to use for the transition. The ease function is set by wrapping the
     * provided Interpolation.
     *
     * @param duration      The tween's length.
     * @param interpolation Interpolation function to use.
     * @return This tween for building.
     * @see #duration(float)
     * @see #ease(Interpolation)
     */
    @SuppressWarnings("unchecked")
    public T using(float duration, Interpolation interpolation) {
        if (isAttached())
            logMutationAfterAttachment();
        else {
            this.duration = duration;
            this.ease = Ease.wrap(interpolation);
        }
        return (T) this;
    }

    @Override
    void setParent(GroupTween<?> parent) {
        super.setParent(parent);
        if (ease == Ease.DEFAULT)
            ease = parent.getDefaultEase().copyOrSelf();
        if (duration < 0f)
            duration = parent.getDefaultDuration();
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
    public T interruptionListener(TweenInterruptionListener<T> listener) {
        if (isAttached())
            logMutationAfterAttachment();
        else
            this.interruptionListener = listener;
        return (T) this;
    }

    /**
     * Fills the current world speeds into the provided array.
     *
     * @param output The array to fill the world speeds into.
     */
    protected final void getWorldSpeeds(float[] output) {
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
            Ease localEase = getEase();
            if (isBlended) {
                ((Ease.BlendInEase) localEase).startSpeed(startSpeeds[i]);
            }
            output[i] = localEase.speed(progress, startValues[i], endValues[i]) / getDuration();
        }
    }

    /**
     * Called by TweenRunner when this tween is first submitted before it is started.
     *
     * @return Starting world speeds array if this tween expects to be started at the speed of the tween it interrupts.
     * Otherwise null.
     */
    protected float[] prepareToInterrupt() {
        float localDuration = getDuration();
        Ease localEase = getEase();
        if (localDuration == 0 || !(localEase instanceof Ease.BlendInEase))
            return null;
        isBlended = true;
        originalStartSpeed = ((Ease.BlendInEase) localEase).getStartSpeed();
        float originalWorldStartSpeed = originalStartSpeed * localDuration;
        Arrays.fill(startWorldSpeeds, 0, vectorSize, originalWorldStartSpeed);
        return startWorldSpeeds;
    }

    @Override
    protected void collectInterrupters(Array<? super TargetTween<?, ?>> collection) {
        collection.add(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean checkInterruption(Class<? extends TargetTween<?, ?>> tweenType, Object target, float[] requestedWorldSpeeds) {
        if (isComplete()) {
            throw new IllegalStateException("Interruption checked on a complete tween: " + this); // TODO remove check
        }
        if (isCanceled()) {
            return false;
        }
        if (tweenType == getClass() && target == getTarget()) {
            cancel();
            if (requestedWorldSpeeds != null && isStarted()) {
                getWorldSpeeds(requestedWorldSpeeds);
            }
            if (interruptionListener != null) {
                interruptionListener.onTweenInterrupted((T) this);
            }
            return true;
        }
        return false;
    }

    /**
     * Return this tween to its pool. Drops all external references. Frees any configurable eases.
     */
    @Override
    public void free() {
        super.free();
        ease.free();
        ease = Ease.DEFAULT;
        isBlended = false;
        duration = -1f;
        Arrays.fill(endValues, 0f);
        interruptionListener = null;
    }

}
