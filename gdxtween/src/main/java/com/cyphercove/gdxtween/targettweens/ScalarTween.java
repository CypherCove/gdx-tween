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

import com.badlogic.gdx.utils.Pool;
import com.cyphercove.gdxtween.TargetTween;
import com.cyphercove.gdxtween.math.Scalar;

public class ScalarTween extends TargetTween<ScalarTween, Scalar> {

    private static final Pool<ScalarTween> POOL = new Pool<ScalarTween>() {
        @Override
        protected ScalarTween newObject() {
            return new ScalarTween();
        }
    };

    public static ScalarTween newInstance() {
        return POOL.obtain();
    }

    public ScalarTween() {
        super(1);
    }

    @Override
    public Class<Scalar> getTargetType() {
        return Scalar.class;
    }

    @Override
    protected void begin() {
        super.begin();
        setStartValue(0, target.x);
    }

    @Override
    protected void apply(int vectorIndex, float value) {
        target.x = value;
    }

    public ScalarTween end(float end) {
        setEndValue(0, end);
        return this;
    }

    public ScalarTween end(Scalar end) {
        setEndValue(0, end.x);
        return this;
    }

    public float getEnd() {
        return getEndValue(0);
    }

    @Override
    public void free() {
        super.free();
        POOL.free(this);
    }
}
