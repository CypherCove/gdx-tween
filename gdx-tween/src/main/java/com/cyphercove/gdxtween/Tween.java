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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the basic functionality of all Tweens. They can be submitted to a TweenRunner, freed to a pool, interrupted,
 * cancelled, and their events can be listened to.
 * <p>
 * A tween should not be reused or submitted to multiple TweenRunners.
 * @param <T> The type of target the tween operates on. For Tweens with no target, the type {@link Targetless} is used.
 * @param <U> The type of this tween. A non-abstract subclass must specify itself as this type. This is not checked.
 */
public abstract class Tween<T, U> {

    private static final String DEFAULT_NAME = "Unnamed";

    private boolean isAttached, isStarted, isComplete;
    private float duration, time;
    private String name = DEFAULT_NAME;
    private TweenCompletionListener<U> completionListener;

    /**
     * Whether the tween has been attached to a TweenRunner or to a parent. This is used to check for the error of
     * starting a tween more than once.
     */
    boolean isAttached() {
        return isAttached;
    }

    void setAttached(boolean attached) {
        isAttached = attached;
    }

    /**
     * Submits the tween to the TweenRunner to start it. A Tween can only be submitted one time to one TweenRunner.
     *
     * @param tweenRunner The {@link TweenRunner} to run this Tween.
     */
    public final void start (@NotNull TweenRunner tweenRunner){
        tweenRunner.start(this);
    }

    /**
     * Gets the reified type of the Tween's target.
     * @return The type of target the tween operates on, or if it has no target, {@link Targetless}.
     */
    public abstract @NotNull Class<T> getTargetType();

    /**
     * Gets the target of this tween, or {@link Targetless} if it has none. Is null if the Tween has not been started.
     * @return Twe tween target if it has been set.
     */
    public abstract @Nullable T getTarget();

    /**
     * Advances the Tween's time.
     *
     * @param deltaTime The time passed.
     * @return Any leftover time if tween was completed, or 0.
     */
    @SuppressWarnings("unchecked")
    protected float step(float deltaTime) {
        if (isComplete) {
            throw new IllegalStateException("Should no longer be stepping completed Tween."); // TODO remove after testing.
        }
        if (!isStarted) {
            if (getTarget() == null)
                throw new IllegalStateException("Tween was started without setting a target: " + this);
            begin();
            isStarted = true;
        }
        time += deltaTime;
        float remaining = 0f;
        if (time >= duration) { //TODO after adding repeat modes, criteria for completion will change.
            isComplete = true;
            remaining = time - duration;
            time = duration; // Clamp interpolation to hit end values exactly.
            //TODO yo yo with even number of repeats will end at 0 time.
        }
        update();
        if (isComplete && completionListener != null) {
            completionListener.onTweenComplete((U)this);
        }
        return remaining;
    }

    /**
     * Called after each time step. If {@link #isComplete()}, this is the last time this method will be called for this
     * tween. The completion listener is called after this method.
     */
    protected void update(){
    }

    /**
     * Called the first time {@link #step(float)} is called. This is a good place to set up parameters for calculating
     * interpolation.
     * */
    abstract protected void begin ();

    /**
     * The tagged name of this String.
     * @return The name
     */
    public final String getName() {
        return name;
    }

    /**
     * Set a name for this tween.
     *
     * @param name The name to set.
     * @return This tween for chaining.
     */
    @SuppressWarnings("unchecked")
    public final U name(String name) {
        this.name = name;
        return (U)this;
    }

    /**
     * The set length of this tween. This does not include repeats.
     * @return This tween's length without accounting for any repeats.
     */
    public final float getDuration() {
        return duration;
    }

    /**
     * Set the length of this tween, not accounting for repeats.
     * @param duration The tween's length.
     * @return This tween for chaining.
     */
    @SuppressWarnings("unchecked")
    public final U duration(float duration) {
        this.duration = duration;
        return (U)this;
    }

    /**
     * The currrent time in the tween's life.
     * @return The time.
     */
    public final float getTime() {
        return time;
    }

    /**
     * Whether the tween has started running.
     * @return True if the tween has started running (has stepped once).
     */
    public final boolean isStarted() {
        return isStarted;
    }

    /**
     * Whether the tween is complete.
     * @return True if the tween is complete (has reached its duration).
     */
    public final boolean isComplete() {
        return isComplete;
    }

    /**
     * Sets a listener to be called when the tween is completed.
     * @param listener The listener to call when the tween completes, or null to clear any existing listener.
     * @return This tween for building.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public U completionListener (@Nullable TweenCompletionListener<U> listener) {
        this.completionListener = listener;
        return (U)this;
    }

    /**
     * Called when starting a {@link TargetTween} to check whether it should interrupt this tween.
     *
     * @param sourceTweenClass  The type of tween that is being started.
     * @param requestedWorldSpeeds If not null and the source target is currently being manipulated by this tween and is being
     *                     interpolated by a TargetingTween of the same type, then the current speed should be filled
     *                     into the array. Otherwise, the array should not be modified.
     * @return True if this tween was interrupted.
     */
    protected abstract boolean checkInterruption(Class<?> sourceTweenClass, @Nullable float[] requestedWorldSpeeds);

    /**
     * Returns this Tween to its pool when it is no longer used to avoid releasing it to the garbage collector. This is
     * called automatically by TweenRunner when tweens are completed or interrupted. Only call it directly if the tween
     * was never submitted to a TweenRunner.
     * <p>
     * Subclasses are expected to reset their own state from this method and pass themselves to a pool. Must call through
     * to super.
     */
    public void free() {
        isAttached = false;
        isComplete = false;
        isStarted = false;
        name = DEFAULT_NAME;
        time = 0f;
        duration = 1f;
        completionListener = null;
    }
}
