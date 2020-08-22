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
package com.cyphercove.gdxtween.tweens.accessors;

import com.badlogic.gdx.graphics.Color;

import com.cyphercove.gdxtween.tweens.AccessorTween;
import org.jetbrains.annotations.NotNull;

/** An accessor for use with AccessorTween that modifies the alpha channel of a color.*/
public class AlphaAccessor implements AccessorTween.Accessor {
    private final Color color;

    public AlphaAccessor (@NotNull Color color) {
        this.color = color;
    }

    public Color getTarget() {
        return color;
    }

    @Override
    public int getNumberOfValues() {
        return 1;
    }

    @Override
    public float getValue (int index) {
        return color.a;
    }

    @Override
    public void setValue (int index, float newValue) {
        color.a = newValue;
    }
}
