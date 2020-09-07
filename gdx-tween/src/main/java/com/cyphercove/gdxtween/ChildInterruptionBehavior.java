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
 * Available behaviors for how to react to a child tween being interrupted by another tween.
 */
public enum ChildInterruptionBehavior {
    /**
     * If any child is interrupted, the entire hierarchy of tweens is canceled.
     */
    CancelHierarchy,
    /**
     * If a child is interrupted, the rest of the hierarchy continues running, with the cancelled child acting as a delay
     * without modifying its target.
     */
    MuteChild
}
