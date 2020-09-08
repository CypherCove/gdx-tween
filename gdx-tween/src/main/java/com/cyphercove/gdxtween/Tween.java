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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.cyphercove.gdxtween.math.Scalar;
import com.cyphercove.gdxtween.math.ScalarInt;
import com.cyphercove.gdxtween.targettweens.*;
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

    private boolean isAttached, isStarted, isComplete, isInterrupted;
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
     * @return Any excess time left over by setting a time that is past the tween's duration, or 0.
     */
    @SuppressWarnings("unchecked")
    final float goTo(float newTime) {
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
        float remaining = 0f;
        if (time >= getDuration()) { //TODO after adding repeat modes, criteria for completion will change.
            isComplete = true;
            remaining = time - getDuration();
            time = getDuration(); // Clamp interpolation to hit end values exactly.
            //TODO yo yo with even number of repeats will end at 0 time.
        }
        if (!isInterrupted)
            update();
        if (isComplete && completionListener != null) {
            completionListener.onTweenComplete((U) this);
        }
        return remaining;
    }

    /**
     * Called after each time step if not muted. This is where the new {@link #getTime()} time} should be applied. If
     * {@link #isComplete()}, this is the last time this method will be called for this tween. The completion listener
     * is called after this method.
     */
    protected void update() {
    }

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
     * Whether the tween is interrupted. When a tween is muted, it does not modify its target or children. It behaves as a
     * delay. It will not fire its listeners.
     *
     * @return Whether the tween is currently interrupted.
     */
    public boolean isInterrupted() {
        return isInterrupted;
    }

    /**
     * Sets this tween as interrupted, which will mute it if it is a member of a parent that is still running.
     */
    protected void interrupt() {
        isInterrupted = true;
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
     * {@link #interrupt()} and return true if it is interrupted.
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
        isInterrupted = false;
        name = DEFAULT_NAME;
        time = 0f;
        completionListener = null;
        parent = null;
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Create a ParallelTween.
     * @return A ParallelTween that will automatically be returned to a pool when complete.
     */
    @NotNull
    static public ParallelTween inParallel() {
        return ParallelTween.newInstance();
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
