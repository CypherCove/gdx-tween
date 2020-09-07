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

import java.util.Iterator;

/**
 * Runs submitted {@linkplain Tween Tweens}.
 * <p>
 * {@link #step(float)} should be called on the {@link TweenRunner} once per frame to update all Tweens.
 * <p>
 * Behaviors:
 * <ul>
 *     <li>An added tween interrupts any tween that is currently running on the same target.</li>
 *     <li>For the purposes of interruption, a tween that has a sequence chain under it is treated as a single tween
 *     on that target.</li>
 *     <li>Tweens with pools are automatically freed upon completion.</li>
 * </ul>
 */
public class TweenRunner {

    private final Array<Tween<?, ?>> tweens = new Array<>();
    private final Array<Tween<?, ?>> tmpTweens = new Array<>();

    /**
     * Adds a tween or tween chain to the manager.
     *
     * @param tween The tween to start.
     */
    public <T, U> void start (@NotNull Tween<T, U> tween){
        if (tween.isAttached()) {
            throw new IllegalStateException("Tween was already started: " + tween);
        }
        tween.setAttached(true);

        // Handle interruption by TargetTweens.
        if (tween instanceof TargetTween) {
            //TODO if tween is a sequence whose first member is a TargetTween, still need to do this. And if it's a parallel tween, need to check all children.
            TargetTween<T, U> targetTween = (TargetTween<T, U>)tween;
            float[] startWorldSpeeds = targetTween.prepare();
            for (Tween<?, ?> candidate : tweens){
                if (candidate.checkInterruption(targetTween.getClass(), startWorldSpeeds)){
                    candidate.free();
                    tmpTweens.add(candidate);
                }
            }
        }
        tweens.removeAll(tmpTweens, true);
        tmpTweens.clear();

        tweens.add(tween);

    }

    public boolean cancelAllTweens() {
        // TODO
        return false;
    }

    /**
     * Removes any running or pending (delayed) tweens for the target object immediately. No listener will be called.
     * @param target The target object whose associated tweens are to be removed.
     * @return Whether a tween or tween chain existed and was removed.
     */
    public boolean cancelTweens (@Nullable Object target){ //todo behavior choice to either mute tween or cancel tween's parents when tween is a child.
        boolean removed = false;
        Iterator<Tween<?, ?>> iterator = tweens.iterator();
        while (iterator.hasNext()){
            Tween<?, ?> tween = iterator.next();
            if (tween.getTarget() == target) {
                iterator.remove();
                tween.free();
                removed = true;
                break;
            }
        }
        return removed;
    }

    /**
     * Must be called for every frame of animation to advance all of the tweens.
     * @param delta The time passed since the last step.
     * */
    public void step (float delta){
        Iterator<Tween<?, ?>> iterator = tweens.iterator();
        while (iterator.hasNext()){
            Tween<?, ?> tween = iterator.next();
            //TODO sequence version of this method should collect the left over time to pass to next item.
            tween.step(delta);
            if (tween.isComplete()){
                iterator.remove();
            }
        }
    }
}
