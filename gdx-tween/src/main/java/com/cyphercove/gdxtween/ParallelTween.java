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

import com.badlogic.gdx.utils.Pool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A tween that runs its children at the same time. It has the duration of its longest child.
 */
public class ParallelTween extends GroupTween<ParallelTween> {

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
    protected void begin() {
        super.begin();

    }

    @Override
    protected void update() {
        super.update();
        for (Tween<?, ?> tween : children) {
            if (!tween.isComplete()) {
                tween.goTo(getTime());
            }
        }
    }

    @Override
    protected float calculateDuration () {
        float maxDuration = 0f;
        for (Tween<?, ?> tween : children)
            maxDuration = Math.max(maxDuration, tween.getDuration());
        return maxDuration;
    }

    @Override
    protected boolean checkInterruption(Class<?> sourceTweenClass, @Nullable float[] requestedWorldSpeeds) {
        if (isInterrupted()){
            return false;
        }
        boolean foundInterruption = false;
        for (Tween<?, ?> tween : children) {
            boolean interrupted = tween.checkInterruption(sourceTweenClass, requestedWorldSpeeds);
            foundInterruption |= interrupted;
        }
        if (foundInterruption && getChildInterruptionBehavior() == ChildInterruptionBehavior.CancelHierarchy){
            interrupt();
            return true;
        }
        return false;
    }

    @Override
    public void free() {
        super.free();
        POOL.free(this);
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
