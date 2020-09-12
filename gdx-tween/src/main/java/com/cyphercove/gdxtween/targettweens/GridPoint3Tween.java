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

import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.utils.Pool;
import com.cyphercove.gdxtween.TargetTween;
import org.jetbrains.annotations.NotNull;

public class GridPoint3Tween extends TargetTween<GridPoint3Tween, GridPoint3> {

    private static final Pool<GridPoint3Tween> POOL = new Pool<GridPoint3Tween>() {
        @Override
        protected GridPoint3Tween newObject() {
            return new GridPoint3Tween();
        }
    };

    public static GridPoint3Tween newInstance() {
        return POOL.obtain();
    }

    public GridPoint3Tween(){
        super(3);
    }

    @Override
    public @NotNull Class<GridPoint3> getTargetType() {
        return GridPoint3.class;
    }

    protected void begin () {
        super.begin();
        setStartValue(0, target.x);
        setStartValue(1, target.y);
        setStartValue(2, target.z);
    }

    protected void apply (int vectorIndex, float value) {
        int rounded = Math.round(value);
        switch (vectorIndex){
            case 0:
                target.x = rounded;
                break;
            case 1:
                target.y = rounded;
                break;
            case 2:
                target.z = rounded;
                break;
        }
    }

    @NotNull
    public GridPoint3Tween end (int endX, int endY, int endZ){
        setEndValue(0, endX);
        setEndValue(1, endY);
        setEndValue(2, endZ);
        return this;
    }

    @NotNull
    public GridPoint3Tween end (@NotNull GridPoint3 end){
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
