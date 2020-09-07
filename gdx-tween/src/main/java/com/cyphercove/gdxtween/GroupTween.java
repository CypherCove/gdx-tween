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
import com.badlogic.gdx.utils.Pool;
import com.cyphercove.gdxtween.ChildInterruptionBehavior;
import com.cyphercove.gdxtween.Targetless;
import com.cyphercove.gdxtween.Tween;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A tween that contains and directs other tweens.
 *
 * @param <U> The type of this tween. A non-abstract subclass must specify itself as this type. This is not checked.
 */
public abstract class GroupTween<U> extends Tween<Targetless, U> {

    protected final Array<Tween<?, ?>> children;
    private ChildInterruptionBehavior childInterruptionBehavior = ChildInterruptionBehavior.CancelHierarchy;
    private float duration = 0f;

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
    public U run (Tween<?, ?> childTween) {
        childTween.setParent(this);
        children.add(childTween);
        return (U)this;
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
    public void setChildInterruptionBehavior(@NotNull ChildInterruptionBehavior childInterruptionBehavior) {
        this.childInterruptionBehavior = childInterruptionBehavior;
    }

    @Override
    public void free() {
        super.free();
        duration = 0f;
        for (Tween<?, ?> tween : children)
            tween.free();
        children.clear();
    }

    @Override
    public @NotNull Class<Targetless> getTargetType() {
        return Targetless.class;
    }

    @Override
    public @Nullable Targetless getTarget() {
        return Targetless.INSTANCE;
    }
}
