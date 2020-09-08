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
    private final Array<Tween<?, ?>> tmpCanceledTweens = new Array<>();
    private final Array<TargetTween<?, ?>> tmpInterrupterTweens = new Array<>();

    /**
     * Adds a tween or tween chain to the manager.
     *
     * @param tween The tween to start.
     */
    public <T, U> void start (@NotNull Tween<T, U> tween){
        if (tween.isAttached()) {
            throw new IllegalStateException("Tween was already started: " + tween);
        }
        tween.markAttached();

        // Handle interruptions
        tween.collectInterrupters(tmpInterrupterTweens);
        for (TargetTween<?, ?> interruptingTween: tmpInterrupterTweens) {
            float[] startWorldSpeeds = interruptingTween.prepareToInterrupt();
            for (Tween<?, ?> candidate : tweens){
                if (candidate.checkInterruption(interruptingTween, startWorldSpeeds)){
                    candidate.free();
                    tmpCanceledTweens.add(candidate);
                }
            }
            tweens.removeAll(tmpCanceledTweens, true);
            tmpCanceledTweens.clear();
        }
        tmpInterrupterTweens.clear();

        tweens.add(tween);

    }

    /**
     * Removes all tweens immediately. No listener will be called.
     * @return Whether any tweens existed and were removed.
     */
    public boolean cancelAllTweens() {
        if (tweens.isEmpty())
            return false;
        for (Tween<?, ?> tween : tweens){
            tween.free();
        }
        tweens.clear();
        return true;
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
            tween.goTo(tween.getTime() + delta);
            if (tween.isComplete()){
                iterator.remove();
            }
        }
    }

    //TODO XXX
    public String getStringContents() {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Tween<?, ?> tween : tweens) {
            if (first)
                first = false;
            else
                builder.append(", ");
            builder.append(tween.toString());
        }
        return builder.toString();
    }
}
