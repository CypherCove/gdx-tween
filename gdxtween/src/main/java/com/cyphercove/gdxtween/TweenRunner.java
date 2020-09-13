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

import com.badlogic.gdx.utils.SnapshotArray;

import java.util.Iterator;

/**
 * Runs submitted {@linkplain Tweens Tweens}.
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

    private final SnapshotArray<Tween<?>> tweens = new SnapshotArray<Tween<?>>(true, 64, Tween.class);
    private final Array<TargetTween<?, ?>> interrupterTweens = new Array<TargetTween<?, ?>>();

    /**
     * Adds a tween or tween chain to the manager.
     *
     * @param tween The tween to start.
     */
    public void start (Tween<?> tween){
        tween = tween.getTopLevelParent();
        if (tween.isAttached()) {
            throw new IllegalStateException("Tween was already started: " + tween);
        }
        tween.markAttached();

        // Snapshot is used to check interruptions so it is safe for callbacks to start new tweens.
        tween.collectInterrupters(interrupterTweens);
        Tween<?>[] snapshotTweens = tweens.begin();
        int snapshotTweensCount = tweens.size;
        for (TargetTween<?, ?> interruptingTween: interrupterTweens) {
            float[] startWorldSpeeds = interruptingTween.prepareToInterrupt();
            for (int i = 0; i < snapshotTweensCount; i++) {
                snapshotTweens[i].checkInterruption(interruptingTween, startWorldSpeeds);
            }
        }
        tweens.end();
        interrupterTweens.clear();

        tweens.add(tween);
    }

    /**
     * Cancels all tweens immediately. No listener will be called.
     * @return Whether any tweens existed and were removed.
     */
    public boolean cancelAllTweens() {
        if (tweens.isEmpty())
            return false;
        for (Tween<?> tween : tweens){
            tween.cancel();
        }
        return true;
    }

    /**
     * Must be called for every frame of animation to advance all of the tweens.
     * @param deltaTime The time passed since the last step.
     * */
    public void step (float deltaTime){
        Iterator<Tween<?>> iterator = tweens.iterator();
        while (iterator.hasNext()) {
            Tween<?> tween = iterator.next();
            if (tween.isCanceled() || tween.isComplete()) {
                tween.free();
                iterator.remove();
            }
        }

        // SnapshotArray is used so callbacks can safely start new tweens.
        Tween<?>[] snapshotTweens = tweens.begin();
        for (int i = 0, n = tweens.size; i < n; i++) {
            Tween<?> tween = snapshotTweens[i];
            tween.goTo(tween.getTime() + deltaTime);
        }
        tweens.end();
    }

    //TODO XXX
    public String getStringContents() {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Tween<?> tween : tweens) {
            if (first)
                first = false;
            else
                builder.append(", ");
            builder.append(tween.toString());
        }
        return builder.toString();
    }
}
