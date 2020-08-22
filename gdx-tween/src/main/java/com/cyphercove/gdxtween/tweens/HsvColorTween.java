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

import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.math.MathUtils;
import com.cyphercove.gdxtween.Tween;
import org.jetbrains.annotations.NotNull;

/** A tween for interpolating the components of a {@linkplain Color}, modifying the RGB in the HSV color space. If an
 * alpha component is not set, alpha will not be modified on the target object by this Tween. */
public class HsvColorTween extends Tween<Color, HsvColorTween> {

    private float endR, endG, endB, endA;
    private boolean modifyAlpha;

    private static final float[] HSV = new float[3];

    public HsvColorTween(){
        super(3);
    }

    @Override
    protected void begin () {
        target.toHsv(HSV);
        float startHue = HSV[0];
        setStartValue(1, HSV[1]);
        setStartValue(2, HSV[2]);

        float r = target.r;
        float g = target.g;
        float b = target.b;
        target.r = endR;
        target.g = endG;
        target.b = endB;
        target.toHsv(HSV);
        float endHue = HSV[0];
        setEndValue(1, HSV[1]);
        setEndValue(2, HSV[2]);
        target.r = r;
        target.g = g;
        target.b = b;

        if (startHue - endHue > 180f)
            endHue += 360f;
        else if (endHue - startHue > 180f)
            startHue += 360f;
        setStartValue(0, startHue);
        setEndValue(0, endHue);
    }

    @Override
    protected void apply (int vectorIndex, float value) {
        if (vectorIndex == 0)
            vectorIndex %= 360f;
        else
            value = MathUtils.clamp(value, 0f, 1f);
        HSV[vectorIndex] = value;
    }

    @Override
    protected void applyAfter() {
        target.fromHsv(HSV[0] % 360f, HSV[1], HSV[2]);
    }

    @Override
    protected void applyAfterComplete() {
        target.r = endR;
        target.g = endG;
        target.b = endB;
    }

    @NotNull
    public HsvColorTween end (float r, float g, float b){
        endR = r;
        endG = g;
        endB = b;
        modifyAlpha = false;
        return this;
    }

    @NotNull
    public HsvColorTween end (float r, float g, float b, float a){
        endR = r;
        endG = g;
        endB = b;
        endA = a;
        modifyAlpha = true;
        return this;
    }

    /** @return the final red value set. */
    public float getEndR (){
        return endR;
    }

    /** @return the final green value set. */
    public float getEndG () {
        return endG;
    }

    /** @return the final blue value set. */
    public float getEndB () {
        return endB;
    }

    /** @return the final alpha value set, or -1 if an end alpha is not set. */
    public float getEndA () {
        return modifyAlpha ? endA : -1f;
    }
}
