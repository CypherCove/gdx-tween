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
package com.cyphercove.gdxtween.targettweens;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Pool;
import com.cyphercove.gdxtween.TargetTween;

public class GridPoint2Tween extends TargetTween<GridPoint2Tween, GridPoint2> {

    private static final Pool<GridPoint2Tween> POOL = new Pool<GridPoint2Tween>() {
        @Override
        protected GridPoint2Tween newObject() {
            return new GridPoint2Tween();
        }
    };

    public static GridPoint2Tween newInstance() {
        return POOL.obtain();
    }

    public GridPoint2Tween() {
        super(2);
    }

    @Override
    public Class<GridPoint2> getTargetType() {
        return GridPoint2.class;
    }

    protected void begin() {
        super.begin();
        setStartValue(0, target.x);
        setStartValue(1, target.y);
    }

    protected void apply(int vectorIndex, float value) {
        int rounded = Math.round(value);
        switch (vectorIndex) {
            case 0:
                target.x = rounded;
                break;
            case 1:
                target.y = rounded;
                break;
        }
    }

    public GridPoint2Tween end(int endX, int endY) {
        setEndValue(0, endX);
        setEndValue(1, endY);
        return this;
    }

    public GridPoint2Tween end(GridPoint2 end) {
        setEndValue(0, end.x);
        setEndValue(1, end.y);
        return this;
    }

    public float getEndX() {
        return getEndValue(0);
    }

    public float getEndY() {
        return getEndValue(1);
    }

    @Override
    public void free() {
        super.free();
        POOL.free(this);
    }
}
