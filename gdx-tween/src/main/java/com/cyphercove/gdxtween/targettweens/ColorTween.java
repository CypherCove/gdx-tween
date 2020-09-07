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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;
import com.cyphercove.gdxtween.TargetTween;
import com.cyphercove.gdxtween.graphics.ColorSpace;
import com.cyphercove.gdxtween.graphics.GtColor;
import com.cyphercove.gdxtween.math.GtMathUtils;
import org.jetbrains.annotations.NotNull;

/**
 * A tween for interpolating the components of a {@linkplain Color}.
 * */
public class ColorTween extends TargetTween<Color, ColorTween> {

    private static final Pool<ColorTween> POOL = new Pool<ColorTween>() {
        @Override
        protected ColorTween newObject() {
            return new ColorTween();
        }
    };

    public static ColorTween newInstance() {
        return POOL.obtain();
    }

    private ColorSpace colorSpace = ColorSpace.LinearRgb;
    private float endR, endG, endB;

    private final float[] accumulator = new float[3];

    private static final float[] TMP_SHARED_1 = new float[3];
    private static final float[] TMP_SHARED_2 = new float[3];
    private static final Color TMP_SHARED_3 = new Color();

    public ColorTween(){
        super(3);
    }

    @Override
    public @NotNull Class<Color> getTargetType() {
        return Color.class;
    }

    @Override
    protected void begin () {
        super.begin();
        switch (colorSpace) {
            case Rgb:
                setStartValue(0, target.r);
                setStartValue(1, target.g);
                setStartValue(2, target.b);
                setEndValue(0, endR);
                setEndValue(1, endG);
                setEndValue(2, endB);
                break;
            case LinearRgb:
                setStartValue(0, GtColor.invertGammaCorrection(target.r));
                setStartValue(1, GtColor.invertGammaCorrection(target.g));
                setStartValue(2, GtColor.invertGammaCorrection(target.b));
                setEndValue(0, GtColor.invertGammaCorrection(endR));
                setEndValue(1, GtColor.invertGammaCorrection(endG));
                setEndValue(2, GtColor.invertGammaCorrection(endB));
                break;
            case Hsv:
                target.toHsv(TMP_SHARED_1);
                float startHue = TMP_SHARED_1[0];
                float startSaturation = TMP_SHARED_1[1];
                setStartValue(1, startSaturation);
                setStartValue(2, TMP_SHARED_1[2]);

                TMP_SHARED_3.set(endR, endG, endB, 1f).toHsv(TMP_SHARED_1);
                float endHue = TMP_SHARED_1[0];
                setEndValue(1, TMP_SHARED_1[1]);
                setEndValue(2, TMP_SHARED_1[2]);

                if (startSaturation < GtColor.SATURATION_THRESHOLD)
                    startHue = endHue;
                else if (TMP_SHARED_1[1] < GtColor.SATURATION_THRESHOLD)
                    endHue = startHue;
                else if (startHue - endHue > 180f)
                    endHue += 360f;
                else if (endHue - startHue > 180f)
                    startHue += 360f;
                setStartValue(0, startHue);
                setEndValue(0, endHue);
                break;
            case Lab:
                GtColor.toLab(target, TMP_SHARED_1);
                GtColor.toLab(endR, endG, endB, TMP_SHARED_2);
                for (int i = 0; i < 3; i++) {
                    setStartValue(i, TMP_SHARED_1[i]);
                    setEndValue(i, TMP_SHARED_2[i]);
                }
                break;
        }
    }

    @Override
    protected void apply (int vectorIndex, float value) {
        switch (colorSpace) {
            case LinearRgb:
                value = GtColor.applyGammaCorrection(value);
                // fall through
            case Rgb:
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
                break;

            case Hsv:
                if (vectorIndex == 0)
                    value = GtMathUtils.modulo(value, 360f);
                else
                    value = MathUtils.clamp(value, 0f, 1f);
                accumulator[vectorIndex] = value;
                break;
            case Lab:
                if (vectorIndex == 0)
                    value = Math.max(0f, value);
                accumulator[vectorIndex] = value;
                break;
        }
    }

    @Override
    protected void applyAfter() {
        switch (colorSpace){
            case Hsv:
                target.fromHsv(accumulator[0], accumulator[1], accumulator[2]);
                break;
            case Lab:
                GtColor.fromLab(target, accumulator[0], accumulator[1], accumulator[2]);
                break;
        }
    }

    @Override
    protected void applyAfterComplete() {
        switch (colorSpace) {
            case LinearRgb:
            case Hsv:
            case Lab:
                target.r = endR;
                target.g = endG;
                target.b = endB;
                break;
        }
    }

    @NotNull
    public ColorTween end (float r, float g, float b){
        endR = r;
        endG = g;
        endB = b;
        return this;
    }

    /**
     * Sets the type of color space the color is interpolated in. If using a BlendableEase and interrupting another
     * ColorTween, they can only blend if they use the same color space.
     * @param colorSpace The value to set.
     * @return This ColorTween for chaining.
     */
    public ColorTween colorSpace (ColorSpace colorSpace) {
        this.colorSpace = colorSpace;
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

    /** @return the type of color space the color is interpolated in. */
    public ColorSpace getColorSpace() {
        return colorSpace;
    }

    @Override
    public void free() {
        super.free();
        colorSpace = ColorSpace.LinearRgb;
        POOL.free(this);
    }

//    /**
//     * Adds another ColorTween to the end of this chain, set to use the same ColorSpace, and returns it.
//     * @param endR     Final red value.
//     * @param endG     Final green value.
//     * @param endB     Final blue value.
//     * @param duration Duration of the tween.
//     * @param ease     The Ease to use.
//     * @return An RgbColorTween that will automatically be returned to a pool when this chain is complete.
//     */
//    @NotNull
//    public ColorTween thenTo (float endR, float endG, float endB, float duration, @Nullable Ease ease){
//        ColorTween tween = Tweens.toRgb(target, endR, endG, endB, duration, ease)
//                .colorSpace(colorSpace);
//        setNext(tween);
//        return tween;
//    }
}
