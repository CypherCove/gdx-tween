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
import com.cyphercove.gdxtween.graphics.GtColor;
import com.cyphercove.gdxtween.math.Scalar;

/**
 * A tween for changing the alpha component of a {@linkplain Color}. It does not modify the
 * RGB components. Since there cannot be multiple Tweens targeting the same object, it will interrupt other types of
 * Color tweens. To treat RGB and Alpha independently, a second {@link com.cyphercove.gdxtween.TweenRunner TweenRunner}
 * dedicated to AlphaTweens may be used.
 */
public class AlphaTween extends TargetTween<AlphaTween, Color> {

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
    private boolean isDegamma;

    public AlphaTween() {
        super(1);
    }

    @Override
    public Class<Color> getTargetType() {
        return Color.class;
    }

    @Override
    protected void begin() {
        super.begin();
        if (isDegamma) {
            setStartValue(0, GtColor.gammaExpand(target.a));
            setEndValue(0, GtColor.gammaExpand(endA));
        } else {
            setStartValue(0, target.a);
            setEndValue(0, endA);
        }
    }

    @Override
    protected void apply(int vectorIndex, float value) {
        target.a = isDegamma ? GtColor.gammaCompress(value) : value;
    }

    public float getEnd() {
        return endA;
    }

    public AlphaTween end(float end) {
        endA = end;
        return this;
    }

    public AlphaTween end(Scalar end) {
        endA = end.x;
        return this;
    }

    /**
     * Whether this tween will treat the inputs as gamma-corrected, and perform the interpolation in linear space.
     * @return The current setting.
     */
    public boolean isDegamma() {
        return isDegamma;
    }

    /**
     * Sets whether the alpha channel should be converted to linear color space before interpolating. If so, the
     * interpolation is performed in linear space and the result is applied back in gamma-corrected space.
     * @param shouldDegamma true if alpha should be linearized before interpolating.
     * @return This tween for building.
     */
    public AlphaTween degamma(boolean shouldDegamma) {
        this.isDegamma = shouldDegamma;
        return this;
    }

    @Override
    public void free() {
        super.free();
        POOL.free(this);
    }

}
