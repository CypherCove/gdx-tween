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
 *
 * @param <T> Tween type. To specify a listener that can be shared by different types of Tweens, use {@code <Tween<?, ?>>}.
 */
public interface TweenCompletionListener<T> {
    /** Called when the associated tween completes. If a Tween repeats, this is only called when repeating is finished.
     * @param completedTween The completed tween.
     */
    void onTweenComplete (T completedTween);
}
