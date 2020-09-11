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
import com.badlogic.gdx.utils.Array;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A tween that contains and directs other tweens.
 *
 * @param <T> The type of this tween. A non-abstract subclass must specify itself as this type. This is not checked.
 */
public abstract class GroupTween<T> extends Tween<T> {

    protected final Array<Tween<?>> children;
    private ChildInterruptionBehavior childInterruptionBehavior = ChildInterruptionBehavior.CancelHierarchy;
    private float duration = 0f;
    private Ease defaultEase = null;
    private float defaultDuration = -1f;

    protected GroupTween (int initialCapacity) {
        children = new Array<>(initialCapacity);
    }

    @Override
    protected void begin() {
        duration = calculateDuration();
    }

    @Override
    public float getDuration() {
        if (duration != 0f) // Tween is started, no need to recalculate on each call.
            return duration;
        if (children.size == 0)
            return 0f;
        return calculateDuration();
    }

    protected abstract float calculateDuration ();

    /**
     * Adds the specified child to this group.
     *
     * @param childTween The child to add.
     * @return This group for building.
     */
    @SuppressWarnings("unchecked")
    public T run (Tween<?> childTween) {
        if (childTween.isAttached()) {
            throw new IllegalArgumentException("Cannot add child tween " + childTween.getName() + " to "
                    + getName() + " because it has already been started.");
        }
        childTween.setParent(this);
        children.add(childTween);
        return (T)this;
    }

    /**
     * Gets the default duration value used for any child TargetTween that doesn't have one set.
     * @return The duration override value.
     */
    public final float getDefaultDuration() {
        if (defaultDuration >= 0f)
            return defaultDuration;
        GroupTween<?> parent = getParent();
        if (parent != null)
            return parent.getDefaultDuration();
        return 0f;
    }

    /**
     * Set a default duration value used for any child TargetTween that doesn't have one set.
     *
     * @param defaultDuration The tween's length.
     * @return This tween for building.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public final T duration(float defaultDuration) {
        this.defaultDuration = defaultDuration;
        return (T)this;
    }

    /**
     * Gets the default Ease used for any child TargetTween that doesn't have one set.
     *
     * @return The Ease function.
     */
    @NotNull
    public final Ease getDefaultEase() {
        if (defaultEase != null)
            return defaultEase;
        GroupTween<?> parent = getParent();
        if (parent != null)
            return parent.getDefaultEase();
        return Ease.linear;
    }

    /**
     * Set a default Ease used for any child TargetTween that doesn't have one set.
     *
     * @param ease The ease function to use.
     * @return This tween for building.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public final T ease(@NotNull Ease ease) {
        this.defaultEase = ease;
        return (T)this;
    }

    /**
     * Set a default Ease used for any child TargetTween that doesn't have one set. The ease is set by wrapping the
     * given interpolation.
     *
     * @param interpolation Interpolation function to use.
     * @return This tween for building.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public T ease(@NotNull Interpolation interpolation) {
        this.defaultEase = Ease.wrap(interpolation);
        return (T)this;
    }

    /**
     * Sets a default duration and default ease function used for any child TargetTween that doesn't have them set.
     *
     * @param duration The tween's length.
     * @param ease The ease function to use.
     * @return This tween for building.
     * @see #duration(float)
     * @see #ease(Ease)
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public T using(float duration, @NotNull Ease ease) {
        this.defaultDuration = duration;
        this.defaultEase = ease;
        return (T)this;
    }

    /**
     * Sets a default duration and default ease function used for any child TargetTween that doesn't have them set. The
     * ease is set by wrapping the given interpolation.
     *
     * @param duration The tween's length.
     * @param interpolation Interpolation function to use.
     * @return This tween for building.
     * @see #duration(float)
     * @see #ease(Interpolation)
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public T using(float duration, @NotNull Interpolation interpolation) {
        this.defaultDuration = duration;
        this.defaultEase = Ease.wrap(interpolation);
        return (T)this;
    }

    /**
     * Gets the set behavior for reacting to children that are interrupted. If this tween has a parent, it inherits the
     * setting from the parent.
     * @return the behavior
     */
    public @NotNull ChildInterruptionBehavior getChildInterruptionBehavior() {
        if (getParent() != null)
            return getParent().getChildInterruptionBehavior();
        return childInterruptionBehavior;
    }

    /**
     * Sets the behavior for reacting to children that are interrupted. This setting is ignored if this tween is not
     * the top level parent in the hierarchy.
     * @param childInterruptionBehavior The behavior to set.
     */
    @SuppressWarnings("unchecked")
    public T childInterruptionBehavior(@NotNull ChildInterruptionBehavior childInterruptionBehavior) {
        this.childInterruptionBehavior = childInterruptionBehavior;
        return (T)this;
    }

    @Override
    public void free() {
        super.free();
        duration = 0f;
        defaultDuration = -1f;
        if (defaultEase != null) {
            defaultEase.free();
            defaultEase = null;
        }
        for (Tween<?> tween : children)
            tween.free();
        children.clear();
    }

}
