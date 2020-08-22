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
package com.cyphercove.gdxtween.tweens;

import com.cyphercove.gdxtween.Tween;
import com.cyphercove.gdxtween.math.ScalarInt;
import org.jetbrains.annotations.NotNull;

public class ScalarIntTween extends Tween<ScalarInt, ScalarIntTween> {

    public ScalarIntTween(){
        super(1);
    }

    @Override
    protected void begin () {
        setStartValue(0, target.x);
    }

    @Override
    protected void apply (int vectorIndex, float value) {
        target.x = Math.round(value);
    }

    @NotNull
    public ScalarIntTween end (float end){
        setEndValue(0, end);
        return this;
    }

    @NotNull
    public ScalarIntTween end (ScalarInt end){
        setEndValue(0, end.x);
        return this;
    }

    public float getEnd (){
        return getEndValue(0);
    }

}
