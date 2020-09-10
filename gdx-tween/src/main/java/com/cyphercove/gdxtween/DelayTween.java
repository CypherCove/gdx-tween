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

public final class DelayTween extends Tween<DelayTween> {

    private static final Pool<DelayTween> POOL = new Pool<DelayTween>() {
        @Override
        protected DelayTween newObject() {
            return new DelayTween();
        }
    };

    @NotNull
    public static DelayTween newInstance() {
        return POOL.obtain();
    }

    private float duration = 0f;

    @Override
    protected void update() {

    }

    @Override
    protected void begin() {

    }

    @Override
    public float getDuration() {
        return duration;
    }

    /**
     * Set the length of this delay, not accounting for repeats.
     * @param duration The DelayTween's length.
     * @return This tween for building.
     */
    @NotNull
    public final DelayTween duration(float duration) {
        this.duration = duration;
        return this;
    }

    @Override
    protected void collectInterrupters(Array<? super TargetTween<?, ?>> collection) {

    }

    @Override
    protected boolean checkInterruption(TargetTween<?, ?> sourceTween, @Nullable float[] requestedWorldSpeeds) {
        return false;
    }

    @Override
    public void free() {
        super.free();
        duration = 0f;
        POOL.free(this);
    }

    @Override
    public String toString() {
        return getName() + "(Delay of " + duration + ")";
    }
}
