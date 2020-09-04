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
import com.cyphercove.gdxtween.graphics.GtColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A tween for interpolating the components of a {@linkplain Color}, modifying the RGB in the CIELAB (aka Lab)
 * color space. If an alpha component is not set, alpha will not be modified on the target object by this Tween. */
public class LabColorTween extends Tween<Color, LabColorTween> {

    private float endR, endG, endB;
    private boolean modifyAlpha;
    private final float[] lab = new float[3];

    private static final float[] TMP_SHARED_1 = new float[3];
    private static final float[] TMP_SHARED_2 = new float[3];

    public LabColorTween(){
        super(4);
    }

    @Override
    protected void begin () {
        GtColor.toLab(target, TMP_SHARED_1);
        GtColor.toLab(endR, endG, endB, TMP_SHARED_2);
        for (int i = 0; i < 3; i++) {
            setStartValue(i, TMP_SHARED_1[i]);
            setEndValue(i, TMP_SHARED_2[i]);
        }

        if (modifyAlpha)
            setStartValue(3, target.a);
    }

    @Override
    protected void apply (int vectorIndex, float value) {
        if (vectorIndex == 3) {
            if (modifyAlpha)
                target.a = MathUtils.clamp(value, 0f, 1f);
            return;
        }
        if (vectorIndex != 2)
            value = Math.max(0f, value);
        lab[vectorIndex] = value;
    }

    @Override
    protected void applyAfter() {
        GtColor.fromLab(target, lab[0], lab[1], lab[2]);
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
        setEndValue(3, a);
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
        return modifyAlpha ? getEndValue(3) : -1f;
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
