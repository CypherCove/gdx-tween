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

import com.badlogic.gdx.utils.Array;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the basic functionality of all Tweens. They can be submitted to a TweenRunner, freed to a pool, interrupted,
 * cancelled, and their events can be listened to.
 * <p>
 * A tween should not be reused or submitted to multiple TweenRunners.
 *
 * @param <T> The type of target the tween operates on. For Tweens with no target, the type {@link Targetless} is used.
 * @param <U> The type of this tween. A non-abstract subclass must specify itself as this type. This is not checked.
 */
public abstract class Tween<T, U> {

    private static final String DEFAULT_NAME = "Unnamed";

    private boolean isAttached, isStarted, isComplete, isCanceled;
    private float time;
    private String name = DEFAULT_NAME;
    private TweenCompletionListener<U> completionListener;
    private GroupTween<?> parent = null;

    /**
     * Whether the tween has been attached to a TweenRunner or to a parent. This is used to check for the error of
     * starting a tween more than once.
     */
    boolean isAttached() {
        return isAttached;
    }

    void markAttached() {
        isAttached = true;
    }

    /**
     * Submits the tween to the TweenRunner to start it. A Tween can only be submitted one time to one TweenRunner.
     *
     * @param tweenRunner The {@link TweenRunner} to run this Tween.
     */
    public final void start(@NotNull TweenRunner tweenRunner) {
        tweenRunner.start(this);
    }

    /**
     * Gets the reified type of the Tween's target.
     *
     * @return The type of target the tween operates on, or if it has no target, {@link Targetless}.
     */
    public abstract @NotNull Class<T> getTargetType();

    /**
     * Gets the target of this tween, or {@link Targetless} if it has none. Is null if the Tween has not been started.
     *
     * @return Twe tween target if it has been set.
     */
    public abstract @Nullable T getTarget();

    /**
     * Gets the parent of this tween, or null if there is none.
     *
     * @return The parent tween or null.
     */
    public @Nullable GroupTween<?> getParent() {
        return parent;
    }


    /**
     * @return The top level parent of this tween, or itself if it has none.
     */
    @NotNull Tween<?, ?> getTopLevelParent() {
        if (parent != null)
            return parent.getTopLevelParent();
        return this;
    }

    /**
     * Sets the parent of this tween.
     *
     * @param parent The parent.
     */
    void setParent(GroupTween<?> parent) {
        this.parent = parent;
    }

    /**
     * Sets the tween's time to a specific value.
     *
     * @param newTime The target time to set.
     */
    @SuppressWarnings("unchecked")
    final void goTo(float newTime) {
        if (isComplete) {
            throw new IllegalStateException("Should no longer be modifying completed Tween."); // TODO remove after testing.
        }
        if (!isStarted) {
            if (getTarget() == null)
                throw new IllegalStateException("Tween was started without setting a target: " + this);
            begin();
            isStarted = true;
        }
        time = Math.min(getDuration(), newTime); //TODO with repeat behavior, the time can loop. If it yo-yos, the time can be as much as twice the duration.
        if (time >= getDuration()) { //TODO after adding repeat modes, criteria for completion will change.
            isComplete = true;
            time = getDuration(); // Clamp interpolation to hit end values exactly.
            //TODO yo yo with even number of repeats will end at 0 time.
        }
        if (!isCanceled)
            update();
        if (isComplete && completionListener != null) {
            completionListener.onTweenComplete((U) this);
        }
    }

    /**
     * Called after each time step if not muted. This is where the new {@link #getTime()} time} should be applied. If
     * {@link #isComplete()}, this is the last time this method will be called for this tween. The completion listener
     * is called after this method.
     */
    protected abstract void update();

    /**
     * Called the first time {@link #goTo(float)} is called. This is a good place to set up parameters for calculating
     * interpolation.
     */
    abstract protected void begin();

    /**
     * The tagged name of this String.
     *
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
        return (U) this;
    }

    /**
     * The set length of this tween. This does not include repeats.
     *
     * @return This tween's length without accounting for any repeats.
     */
    public abstract float getDuration();

    /**
     * The currrent time in the tween's life.
     *
     * @return The time.
     */
    public final float getTime() {
        return time;
    }

    /**
     * Whether the tween has started running.
     *
     * @return True if the tween has started running (has stepped once).
     */
    public final boolean isStarted() {
        return isStarted;
    }

    /**
     * Whether the tween is complete.
     *
     * @return True if the tween is complete (has reached its duration).
     */
    public final boolean isComplete() {
        return isComplete;
    }

    /**
     * Whether the tween is canceled, either by interruption or a direct call to {@link #cancel}. When a tween is muted, it does not modify its target or children. It behaves as a
     * delay. It will not fire its listeners.
     *
     * @return Whether the tween is currently interrupted.
     */
    public boolean isCanceled() {
        return isCanceled;
    }

    /**
     * Sets this tween as canceled, which will mute it if it is a member of a parent that is still running. If it has
     * no parent, it will be removed by the TweenRunner on the next step.
     */
    protected void cancel() {
        isCanceled = true;
    }

    /**
     * Sets a listener to be called when the tween is completed.
     *
     * @param listener The listener to call when the tween completes, or null to clear any existing listener.
     * @return This tween for building.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public U completionListener(@Nullable TweenCompletionListener<U> listener) {
        this.completionListener = listener;
        return (U) this;
    }

    /**
     * Add all interruption-eligible TargetTweens to the collection, including self if it is one.
     * @param collection The collection to add the TargetTween(s) to.
     */
    protected abstract void collectInterrupters(Array<? super TargetTween<?, ?>> collection);

    /**
     * Called when starting a {@link TargetTween} to check whether this tween should be interrupted. It should call
     * {@link #cancel()} and return true if it is interrupted.
     *
     * @param sourceTween    The tween that that is being started.
     * @param requestedWorldSpeeds If not null and the source target is currently being manipulated by this tween and is being
     *                             interpolated by a TargetingTween of the same type, then the current speed should be filled
     *                             into the array. Otherwise, the array should not be modified.
     * @return True if this tween was interrupted.
     */
    protected abstract boolean checkInterruption(TargetTween<?, ?> sourceTween, @Nullable float[] requestedWorldSpeeds);

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
        isCanceled = false;
        name = DEFAULT_NAME;
        time = 0f;
        completionListener = null;
        parent = null;
    }

    /**
     * Returns a SequenceTween that contains this tween at the end. If this tween is already a child of a SequenceTween,
     * then that parent is returned. Otherwise, a new SequenceTween is obtained and this tween is added to it.
     * @return A SequenceTween containing this tween at the end.
     */
    @NotNull
    public SequenceTween then(){
        Tween<?, ?> parent = getParent();
        if (parent instanceof SequenceTween)
            return (SequenceTween)parent;
        return Tweens.inSequence().run(this);
    }

    @Override
    public String toString() {
        return getName();
    }

}
