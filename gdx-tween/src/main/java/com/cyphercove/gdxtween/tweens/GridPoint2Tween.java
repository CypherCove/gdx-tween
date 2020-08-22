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
package com.cyphercove.gdxtween.tweens;

import com.badlogic.gdx.math.GridPoint2;
import com.cyphercove.gdxtween.Tween;
import org.jetbrains.annotations.NotNull;

public class GridPoint2Tween extends Tween<GridPoint2, GridPoint2Tween> {

    public GridPoint2Tween(){
        super(2);
    }

    protected void begin () {
        setStartValue(0, target.x);
        setStartValue(1, target.y);
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
        }
    }

    @NotNull
    public GridPoint2Tween end (float endX, float endY){
        setEndValue(0, endX);
        setEndValue(1, endY);
        return this;
    }

    @NotNull
    public GridPoint2Tween end (@NotNull GridPoint2 end){
        setEndValue(0, end.x);
        setEndValue(1, end.y);
        return this;
    }

    public float getEndX (){
        return getEndValue(0);
    }

    public float getEndY (){
        return getEndValue(1);
    }
}
