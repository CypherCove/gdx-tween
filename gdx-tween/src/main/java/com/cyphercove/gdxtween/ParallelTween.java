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
import com.badlogic.gdx.utils.Pool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A tween that runs its children at the same time. It has the duration of its longest child.
 */
public final class ParallelTween extends GroupTween<ParallelTween> {

    private static final Pool<ParallelTween> POOL = new Pool<ParallelTween>() {
        @Override
        protected ParallelTween newObject() {
            return new ParallelTween(6);
        }
    };

    public static ParallelTween newInstance() {
        return POOL.obtain();
    }

    public ParallelTween(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    protected void update() {
        for (Tween<?> tween : children) {
            if (!tween.isComplete() && !tween.isCanceled()) {
                tween.goTo(getTime());
            }
        }
    }

    @Override
    protected float calculateDuration () {
        float maxDuration = 0f;
        for (Tween<?> tween : children)
            maxDuration = Math.max(maxDuration, tween.getDuration());
        return maxDuration;
    }

    @Override
    protected void collectInterrupters(Array<? super TargetTween<?, ?>> collection) {
        for (Tween<?> tween : children) {
            tween.collectInterrupters(collection);
        }
    }

    @Override
    protected boolean checkInterruption(TargetTween<?, ?> sourceTween, @Nullable float[] requestedWorldSpeeds) {
        // Even if canceled, children should be checked. There might be parallel tweens started that both interrupt
        // members of this tween, so they will need to get world speeds.
        boolean wasCanceled = isCanceled();
        boolean foundInterruption = false;
        for (Tween<?> tween : children) {
            if (!tween.isComplete()) {
                boolean interrupted = tween.checkInterruption(sourceTween, requestedWorldSpeeds);
                foundInterruption |= interrupted;
            }
        }
        if (foundInterruption && getChildInterruptionBehavior() == ChildInterruptionBehavior.CancelHierarchy){
            cancel();
            return !wasCanceled;
        }
        return false;
    }

    @Override
    public void free() {
        super.free();
        POOL.free(this);
    }

    @Override
    public String toString() {
        return getName() + "(Parallel with " + children.size + " children)";
    }
}
