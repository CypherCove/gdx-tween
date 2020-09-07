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

import org.jetbrains.annotations.NotNull;

/**
 * The placeholder target of Tweens that have no target.
 */
public final class Targetless {
    private Targetless() {}

    /** The Targetless instance passed to callbacks for targetless Tweens. */
    public static final @NotNull Targetless INSTANCE = new Targetless();

    @Override
    public String toString() {
        return "Targetless";
    }
}
