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

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SequenceTween extends GroupTween<SequenceTween> {

    private static final Pool<SequenceTween> POOL = new Pool<SequenceTween>() {
        @Override
        protected SequenceTween newObject() {
            return new SequenceTween(10);
        }
    };

    @NotNull
    public static SequenceTween newInstance() {
        return POOL.obtain();
    }

    private int index = 0;
    private float timeAtIndex = 0f;

    public SequenceTween(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    protected float calculateDuration() {
        float totalDuration = 0f;
        for (Tween<?> tween : children)
            totalDuration += tween.getDuration();
        return totalDuration;
    }

    @Override
    protected void update() {
        //TODO if in a yo-yo even repeat, work backwards
        float time = getTime();
        boolean isComplete = isComplete();
        for (int i = index; i < children.size; i++) {
            Tween<?> tween = children.get(i);
            if (!tween.isCanceled())
                tween.goTo(isComplete ? tween.getDuration() : time - timeAtIndex); // If sequence complete, ensure every child reaches completion, regardless of rounding error.
            if (tween.isComplete() || (tween.isCanceled() && time > timeAtIndex + tween.getDuration())){
                index++;
                timeAtIndex = Math.min(timeAtIndex + tween.getDuration(), time); // Can't allow timeAtIndex to pass time from rounding error.
            } else {
                break;
            }
        }
    }

    /**
     * Add a new {@link ParallelTween} to this sequence and return it.
     * @return A new ParallelTween that has been added to the end of this sequence.
     */
    @NotNull
    public ParallelTween inParallel() {
        ParallelTween parallelTween = ParallelTween.newInstance();
        run(parallelTween);
        return parallelTween;
    }

    /**
     * Adds a DelayTween to this sequence.
     * @param duration Length of the delay.
     * @return this SequenceTween for building.
     */
    @NotNull
    public SequenceTween delay(float duration) {
        run(DelayTween.newInstance().duration(duration));
        return this;
    }

    @Override
    protected void collectInterrupters(Array<? super TargetTween<?, ?>> collection) {
        if (!children.isEmpty())
            children.first().collectInterrupters(collection);
    }

    @Override
    protected boolean checkInterruption(TargetTween<?, ?> sourceTween, @Nullable float[] requestedWorldSpeeds) {
        // Even if canceled, children should be checked. There might be parallel tweens started that both interrupt
        // members of this tween, so they will need to get world speeds.
        boolean wasCanceled = isCanceled();
        boolean foundInterruption = false;
        for (int i = index; i < children.size; i++) {
            Tween<?> tween = children.get(i);
            boolean interrupted = tween.checkInterruption(sourceTween, requestedWorldSpeeds);
            foundInterruption |= interrupted;
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
        index = 0;
        timeAtIndex = 0f;
        POOL.free(this);
    }

    @Override
    public String toString() {
        return getName() + "(Sequence with " + children.size + " children)";
    }
}
