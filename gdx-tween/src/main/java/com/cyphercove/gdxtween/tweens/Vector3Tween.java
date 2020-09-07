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

import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.utils.Pool;
import com.cyphercove.gdxtween.TargetTween;
import org.jetbrains.annotations.NotNull;

public class Vector3Tween extends TargetTween<Vector3, Vector3Tween> {

    private static final Pool<Vector3Tween> POOL = new Pool<Vector3Tween>() {
        @Override
        protected Vector3Tween newObject() {
            return new Vector3Tween();
        }
    };

    public static Vector3Tween newInstance() {
        return POOL.obtain();
    }

    public Vector3Tween (){
        super(3);
    }

    @Override
    public @NotNull Class<Vector3> getTargetType() {
        return Vector3.class;
    }

    protected void begin () {
        super.begin();
        setStartValue(0, target.x);
        setStartValue(1, target.y);
        setStartValue(2, target.z);
    }

    protected void apply (int vectorIndex, float value) {
        switch (vectorIndex){
            case 0:
                target.x = value;
                break;
            case 1:
                target.y = value;
                break;
            case 2:
                target.z = value;
                break;
        }
    }

    @NotNull
    public Vector3Tween end (float endX, float endY, float endZ){
        setEndValue(0, endX);
        setEndValue(1, endY);
        setEndValue(2, endZ);
        return this;
    }

    @NotNull
    public Vector3Tween end (@NotNull Vector3 end){
        setEndValue(0, end.x);
        setEndValue(1, end.y);
        setEndValue(2, end.z);
        return this;
    }

    public float getEndX (){
        return getEndValue(0);
    }

    public float getEndY (){
        return getEndValue(1);
    }

    public float getEndZ () {
        return getEndValue(2);
    }

    @Override
    public void free() {
        super.free();
        POOL.free(this);
    }
}
