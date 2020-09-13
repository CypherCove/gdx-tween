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

/**
 * A listener that fires for {@link TargetTween TargetTweens} when they are interrupted by other TargetTweens.
 * @param <T> The type of tween being interrupted. To specify a listener that can be shared by different types of
 *           TargetTweens, use {@code <TargetTween<?, ?>>}.
 */
public interface TweenInterruptionListener<T> {
    /** Called when the associated tween is interrupted by a tween with the same target, or one of its children was
     * interrupted. The associated tween will not be completed.
     * @param interruptedTween The tween that was interrupted.
     * @param interruptionSource The tween that interrupted the other tween.
     */
    void onTweenInterrupted (T interruptedTween, T interruptionSource);
}
