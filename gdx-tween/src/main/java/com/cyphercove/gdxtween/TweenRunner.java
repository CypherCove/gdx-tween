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
import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.ObjectMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/**
 * Runs submitted {@linkplain TargetingTween Tweens}.
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
    /* It is known Tweens will only ever interrupt a tween targeting the exact same instance of something, so
          1. It is safe to treat the target types as Object, because they will are only passed between Tweens and
             associated listeners targeting the same type.
          2. It is safe to cast all Tweens to Tween<Object, ?> because they will only ever interrupt another Tween
             of matching type.
     */
    private final Array<TargetingTween<Object, ?>> tweens = new Array<>();
    private final Array<TargetingTween<Object, ?>> delayedTweens = new Array<>();
    private final Array<TargetingTween<?, ?>> tmpTweens = new Array<>();
    private final ObjectMap<Object, TweenCompletionListener<?>> tmpListeners = new IdentityMap<>();

    /** Adds a tween or tween chain to the manager. Only one tween or tween chain can be running on
     * the same target. If the tween is marked {@linkplain TargetingTween#isShouldBlend()} and has a delay, it
     * will not interrupt any existing tween until its delay runs out but it will cancel any other
     * delayed tween in the same state. If {@linkplain TargetingTween#isShouldBlend()} is false, it will immediately
     * interrupt any existing tween on the same target before its delay starts.
     *
     * @param tween The tween or member of a tween chain to start.
     */
    public void start (@NotNull TargetingTween<?, ?> tween){
        @SuppressWarnings("unchecked")
        // Any tween in a chain can be submitted but always start at the head.
        TargetingTween<Object, TargetingTween<Object, ?>> headTween = (TargetingTween<Object, TargetingTween<Object, ?>>)tween.head;
        Object target = headTween.getTarget();
        TargetingTween<Object, ?> interruptedTween = null;
        for (TargetingTween<Object, ?> candidate : tweens){
            if (candidate.checkInterruption(tween, null)){
                interruptedTween = candidate;
            }
        }
        boolean shouldBlend = interruptedTween != null && //TODO move before checkInterruption and pass world speeds array if not delayed
                interruptedTween.getClass().equals(headTween.getClass()) &&
                headTween.isShouldBlend();

        if (shouldBlend && !headTween.isDelayComplete()){
            // Defer submission to allow existing tween to continue running until delay runs out.
            // but cancel any queued tween on same target
            TargetingTween<?, ?> cancelledTween = null;

            for (TargetingTween<Object, ?> candidate : delayedTweens){
                if (candidate.checkInterruption(tween.head, null)){
                    cancelledTween = candidate;
                }
            }
            if (cancelledTween != null)
                cancelledTween.free();
            //noinspection unchecked
            delayedTweens.removeValue((TargetingTween<Object, ?>) cancelledTween, true);
            delayedTweens.add(headTween);
            return;
        }

        TweenInterruptionListener<Object> interruptedTweenListener = null;
        if (interruptedTween != null){
            if (shouldBlend){
                headTween.interrupt(interruptedTween);
            }
            interruptedTweenListener = interruptedTween.getInterruptionListener();
            tweens.removeValue(interruptedTween, true);
            interruptedTween.free();
        }

        tweens.add(headTween);
        if (interruptedTweenListener != null){
            interruptedTweenListener.onTweenInterrupted(target);
        }
    }

    /**
     * Removes any running or pending (delayed) tweens for the target object immediately. No listener will be called.
     * @param target The target object whose tween or tween chain is to be removed.
     * @return Whether a tween or tween chain existed and was removed.
     */
    public boolean clearTweens (@Nullable Object target){
        boolean removed = false;
        Iterator<TargetingTween<Object, ?>> iterator = delayedTweens.iterator();
        while (iterator.hasNext()){
            TargetingTween<?, ?> tween = iterator.next();
            if (tween.target == target) {
                iterator.remove();
                tween.free();
                removed = true;
                break;
            }
        }
        iterator = tweens.iterator();
        while (iterator.hasNext()){
            TargetingTween<?, ?> tween = iterator.next();
            if (tween.target == target) {
                iterator.remove();
                tween.free();
                removed = true;
                break;
            }
        }
        return removed;
    }

    /** Must be called for every frame of animation to advance all of the tweens.
     * @param delta The time passed since the last step.*/
    public void step (float delta){

        Iterator<TargetingTween<Object, ?>> iterator = delayedTweens.iterator();
        while (iterator.hasNext()){
            TargetingTween<?, ?> tween = iterator.next();
            if(tween.stepDelay(delta)) {
                iterator.remove();
                tmpTweens.add(tween);
            }
        }
        for (TargetingTween<?, ?> tween : tmpTweens){
            start(tween);
        }
        tmpTweens.clear();

        iterator = tweens.iterator();
        while (iterator.hasNext()){
            TargetingTween<?, ?> tween = iterator.next();
            if (tween.step(delta)){
                if (tween.getCompletionListener() != null){
                    tmpListeners.put(tween.target, tween.getCompletionListener());
                }
                tmpTweens.add(tween);
            }
        }

        for (TargetingTween<?, ?> tween : tmpTweens){
            //noinspection unchecked
            tweens.removeValue((TargetingTween<Object, ?>) tween, true);
            tween.free();
        }
        tmpTweens.clear();

        for (ObjectMap.Entry<Object, TweenCompletionListener<?>> entry: tmpListeners){
            //noinspection unchecked
            ((TweenCompletionListener<Object>)entry.value).onTweenComplete(entry.key);
        }
        tmpListeners.clear();
    }
}
