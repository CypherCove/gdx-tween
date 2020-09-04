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
import com.cyphercove.gdxtween.math.GtMathUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A tween for interpolating the components of a {@linkplain Color}, modifying the RGB in the HSV color space. If an
 * alpha component is not set, alpha will not be modified on the target object by this Tween. */
public class HsvColorTween extends Tween<Color, HsvColorTween> {

    private float endR, endG, endB;
    private boolean modifyAlpha;

    private final float[] hsv = new float[3];

    private static final float[] TMP_SHARED_1 = new float[3];
    private static final Color TMP_SHARED_2 = new Color();

    public HsvColorTween(){
        super(4);
    }

    @Override
    protected void begin () {
        target.toHsv(TMP_SHARED_1);
        float startHue = TMP_SHARED_1[0];
        float startSaturation = TMP_SHARED_1[1];
        setStartValue(1, TMP_SHARED_1[1]);
        setStartValue(2, TMP_SHARED_1[2]);

        TMP_SHARED_2.set(endR, endG, endB, 1f).toHsv(TMP_SHARED_1);
        float endHue = hsv[0];
        setEndValue(1, TMP_SHARED_1[1]);
        setEndValue(2, TMP_SHARED_1[2]);

        if (startSaturation < GtColor.SATURATION_THRESHOLD)
            startHue = endHue;
        else if (hsv[1] < GtColor.SATURATION_THRESHOLD)
            endHue = startHue;
        else if (startHue - endHue > 180f)
            endHue += 360f;
        else if (endHue - startHue > 180f)
            startHue += 360f;
        setStartValue(0, startHue);
        setEndValue(0, endHue);

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
        if (vectorIndex == 0)
            value = GtMathUtils.modulo(value, 360f);
        else
            value = MathUtils.clamp(value, 0f, 1f);
        hsv[vectorIndex] = value;
    }

    @Override
    protected void applyAfter() {
        target.fromHsv(hsv[0], hsv[1], hsv[2]);
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
     * Adds another HsvColorTween to the end of this chain and returns it. The tween that modifies the RGB channels of
     * the Color only.
     * @param endR     Final red value.
     * @param endG     Final green value.
     * @param endB     Final blue value.
     * @param duration Duration of the tween.
     * @param ease     The Ease to use.
     * @return An HsvColorTween that will automatically be returned to a pool when this chain is complete.
     */
    @NotNull
    public HsvColorTween thenTo (float endR, float endG, float endB, float duration, @Nullable Ease ease){
        HsvColorTween tween = Tweens.toViaHsv(target, endR, endG, endB, duration, ease);
        setNext(tween);
        return tween;
    }

    /**
     * Adds another HsvColorTween to the end of this chain and returns it. The tween modifies all channels of the Color,
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
    public HsvColorTween thenTo (float endR, float endG, float endB, float endA, float duration, @Nullable Ease ease){
        HsvColorTween tween = Tweens.toViaHsv(target, endR, endG, endB, endA, duration, ease);
        setNext(tween);
        return tween;
    }
}
