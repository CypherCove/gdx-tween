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
import com.cyphercove.gdxtween.Ease;
import com.cyphercove.gdxtween.Tween;
import com.cyphercove.gdxtween.graphics.ColorConversion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A tween for interpolating the components of a {@linkplain Color}, modifying the RGB in the cylindrical CIELAB
 * (aka HCL) color space. If an alpha component is not set, alpha will not be modified on the target object by this Tween. */
public class LabColorTween extends Tween<Color, LabColorTween> {

    private float endR, endG, endB, endA;
    private boolean modifyAlpha;

    private static final float[] LCH = new float[3];

    public LabColorTween(){
        super(3);
    }

    @Override
    protected void begin () {
        ColorConversion.toLch(target, LCH);
        setStartValue(0, LCH[0]);
        setStartValue(1, LCH[1]);
        float startHue = LCH[2];

        float r = target.r;
        float g = target.g;
        float b = target.b;
        target.r = endR;
        target.g = endG;
        target.b = endB;
        ColorConversion.toLch(target, LCH);
        setEndValue(0, LCH[0]);
        setEndValue(1, LCH[1]);
        float endHue = LCH[2];
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
        if (vectorIndex == 3) {
            if (modifyAlpha)
                target.a = MathUtils.clamp(value, 0f, 1f);
            return;
        }
        if (vectorIndex == 2)
            value %= 360f;
        LCH[vectorIndex] = value;
    }

    @Override
    protected void applyAfter() {
        ColorConversion.fromLch(target, LCH);
        target.clamp();
    }

    @Override
    protected void applyAfterComplete() {
        target.r = endR;
        target.g = endG;
        target.b = endB;
    }

    @NotNull
    public LabColorTween end (float r, float g, float b){
        endR = r;
        endG = g;
        endB = b;
        modifyAlpha = false;
        return this;
    }

    @NotNull
    public LabColorTween end (float r, float g, float b, float a){
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

    /**
     * Adds another LabColorTween to the end of this chain and returns it. The tween that modifies the RGB channels of
     * the Color only.
     * @param endR     Final red value.
     * @param endG     Final green value.
     * @param endB     Final blue value.
     * @param duration Duration of the tween.
     * @param ease     The Ease to use.
     * @return An HsvColorTween that will automatically be returned to a pool when this chain is complete.
     */
    @NotNull
    public LabColorTween thenTo (float endR, float endG, float endB, float duration, @Nullable Ease ease){
        LabColorTween tween = Tweens.toViaLab(target, endR, endG, endB, duration, ease);
        setNext(tween);
        return tween;
    }

    /**
     * Adds another LabColorTween to the end of this chain and returns it. The tween modifies all channels of the Color,
     * including alpha.
     * @param endR     Final red value.
     * @param endG     Final green value.
     * @param endB     Final blue value.
     * @param endA     Final alpha value.
     * @param duration Duration of the tween.
     * @param ease     The Ease to use.
     * @return An HsvColorTween that will automatically be returned to a pool when this chain is complete.
     */
    @NotNull
    public LabColorTween thenTo (float endR, float endG, float endB, float endA, float duration, @Nullable Ease ease){
        LabColorTween tween = Tweens.toViaLab(target, endR, endG, endB, endA, duration, ease);
        setNext(tween);
        return tween;
    }
}