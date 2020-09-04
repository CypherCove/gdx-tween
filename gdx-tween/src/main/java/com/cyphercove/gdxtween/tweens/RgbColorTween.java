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

/** A tween for interpolating the components of a {@linkplain Color}, modifying the RGB in the RGB color space. If an
 * alpha component is not set, alpha will not be modified on the target object by this Tween. */
public class RgbColorTween extends Tween<Color, RgbColorTween> {

    private static boolean useGammaCorrection = true;

    /**
     * Whether RgbColorTweens interpolate in linear (gamma correction removed) color space, which yields smoother results.
     * @return The current value of the setting. True if using linear color space.
     */
    public static boolean isUseGammaCorrection() {
        return useGammaCorrection;
    }

    /**
     * Sets whether RgbColorTweens interpolate in linear (gamma correction removed) color space, which yields smoother
     * results. Default true.
     * <p>
     * This setting should not be changed while tweens are in progress.
     * @param useGammaCorrection Whether to interpolate in linear color space.
     */
    public static void setUseGammaCorrection(boolean useGammaCorrection) {
        RgbColorTween.useGammaCorrection = useGammaCorrection;
    }

    private float endR, endG, endB;
    private boolean modifyAlpha;

    public RgbColorTween(){
        super(4);
    }

    @Override
    protected void begin () {
        if (useGammaCorrection) {
            setStartValue(0, GtColor.invertGammaCorrection(target.r));
            setStartValue(1, GtColor.invertGammaCorrection(target.g));
            setStartValue(2, GtColor.invertGammaCorrection(target.b));
            setEndValue(0, GtColor.invertGammaCorrection(endR));
            setEndValue(1, GtColor.invertGammaCorrection(endG));
            setEndValue(2, GtColor.invertGammaCorrection(endB));
        } else {
            setStartValue(0, target.r);
            setStartValue(1, target.g);
            setStartValue(2, target.b);
            setEndValue(0, endR);
            setEndValue(1, endG);
            setEndValue(2, endB);
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
        if (useGammaCorrection)
            value = MathUtils.clamp(GtColor.applyGammaCorrection(value), 0f, 1f);
        else
            value = MathUtils.clamp(value, 0f, 1f);
        switch (vectorIndex){
            case 0:
                target.r = value;
                break;
            case 1:
                target.g = value;
                break;
            case 2:
                target.b = value;
                break;
        }
    }

    @Override
    protected void applyAfterComplete() {
        if (useGammaCorrection) {
            target.r = endR;
            target.g = endG;
            target.b = endB;
        }
    }

    @NotNull
    public RgbColorTween end (float r, float g, float b){
        endR = r;
        endG = g;
        endB = b;
        modifyAlpha = false;
        return this;
    }

    @NotNull
    public RgbColorTween end (float r, float g, float b, float a){
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
    public RgbColorTween thenTo (float endR, float endG, float endB, float duration, @Nullable Ease ease){
        RgbColorTween tween = Tweens.toViaRgb(target, endR, endG, endB, duration, ease);
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
    public RgbColorTween thenTo (float endR, float endG, float endB, float endA, float duration, @Nullable Ease ease){
        RgbColorTween tween = Tweens.toViaRgb(target, endR, endG, endB, endA, duration, ease);
        setNext(tween);
        return tween;
    }
}
