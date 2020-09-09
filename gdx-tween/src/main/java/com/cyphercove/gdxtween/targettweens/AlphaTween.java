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
package com.cyphercove.gdxtween.targettweens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Pool;
import com.cyphercove.gdxtween.TargetTween;
import com.cyphercove.gdxtween.math.Scalar;
import org.jetbrains.annotations.NotNull;

/** A tween for changing the alpha component of a {@linkplain Color}. It does not modify the
 * RGB components. Since there cannot be multiple Tweens targeting the same object, it will interrupt other types of
 * Color tweens. To treat RGB and Alpha independently, a second {@link com.cyphercove.gdxtween.TweenRunner TweenRunner}
 * dedicated to AlphaTweens may be used.*/
public class AlphaTween extends TargetTween<Color, AlphaTween> {

    private static final Pool<AlphaTween> POOL = new Pool<AlphaTween>() {
        @Override
        protected AlphaTween newObject() {
            return new AlphaTween();
        }
    };

    public static AlphaTween newInstance() {
        return POOL.obtain();
    }

    private float endA;

    public AlphaTween(){
        super(1);
    }

    @Override
    public @NotNull Class<Color> getTargetType() {
        return Color.class;
    }

    @Override
    protected void begin () {
        super.begin();
        setStartValue(0, target.a);
    }

    @Override
    protected void apply (int vectorIndex, float value) {
        target.a = value;
    }

    @NotNull
    public AlphaTween end (float end){
        setEndValue(0, end);
        return this;
    }

    @NotNull
    public AlphaTween end (Scalar end){
        setEndValue(0, end.x);
        return this;
    }

    public float getEnd (){
        return getEndValue(0);
    }

    @Override
    public void free() {
        super.free();
        POOL.free(this);
    }

    //
//    /**
//     * Adds another AlphaTween to the end of this chain and returns it.
//     *
//     * @param endA     Final alpha value.
//     * @param duration Duration of the tween.
//     * @param ease     The Ease to use.
//     * @return An AlphaTween that will automatically be returned to a pool when complete.
//     */
//    public AlphaTween thenTo(float endA, float duration, @Nullable Ease ease) {
//        AlphaTween tween = Tweens.toAlpha(target, endA, duration, ease);
//        setNext(tween);
//        return tween;
//    }
}