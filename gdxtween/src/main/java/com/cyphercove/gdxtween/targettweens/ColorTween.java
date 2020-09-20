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

/**
 * A tween for interpolating the components of a {@linkplain Color}.
 */
public class ColorTween extends TargetTween<ColorTween, Color> {

    private static final Pool<ColorTween> POOL = new Pool<ColorTween>() {
        @Override
        protected ColorTween newObject() {
            return new ColorTween();
        }
    };

    public static ColorTween newInstance() {
        return POOL.obtain();
    }

    private ColorSpace colorSpace = ColorSpace.DegammaRgb;
    private float endR, endG, endB;

    private final float[] accumulator = new float[3];

    private static final float[] TMP_SHARED_1 = new float[3];
    private static final float[] TMP_SHARED_2 = new float[3];
    private static final Color TMP_SHARED_3 = new Color();

    public ColorTween() {
        super(3);
    }

    @Override
    public Class<Color> getTargetType() {
        return Color.class;
    }

    @Override
    protected void begin() {
        super.begin();
        float startR, startG, startB, endR, endG, endB;
        if (colorSpace.isDegamma) {
            startR = GtColor.gammaExpand(target.r);
            startG = GtColor.gammaExpand(target.g);
            startB = GtColor.gammaExpand(target.b);
            endR = GtColor.gammaExpand(this.endR);
            endG = GtColor.gammaExpand(this.endG);
            endB = GtColor.gammaExpand(this.endB);
        } else {
            startR = target.r;
            startG = target.g;
            startB = target.b;
            endR = this.endR;
            endG = this.endG;
            endB = this.endB;
        }
        switch (colorSpace) {
            case Rgb:
            case DegammaRgb:
                setStartValue(0, startR);
                setStartValue(1, startG);
                setStartValue(2, startB);
                setEndValue(0, endR);
                setEndValue(1, endG);
                setEndValue(2, endB);
                return;
            case Hcl:
            case DegammaHcl:
                GtColor.toHcl(startR, startG, startB, TMP_SHARED_1);
                GtColor.toHcl(endR, endG, endB, TMP_SHARED_2);
                fixHxxStartEnd();
                break;
            case Hsl:
            case DegammaHsl:
                GtColor.toHsl(startR, startG, startB, TMP_SHARED_1);
                GtColor.toHsl(endR, endG, endB, TMP_SHARED_2);
                fixHxxStartEnd();
                break;
            case Hsv:
            case DegammaHsv:
                target.toHsv(TMP_SHARED_1);
                TMP_SHARED_3.set(endR, endG, endB, 1f).toHsv(TMP_SHARED_2);
                fixHxxStartEnd();
                break;
            case Lab:
            case DegammaLab:
                GtColor.toLab(startR, startG, startB, TMP_SHARED_1);
                GtColor.toLab(endR, endG, endB, TMP_SHARED_2);
                break;
            case Lch:
            case DegammaLch:
                GtColor.toLch(startR, startG, startB, TMP_SHARED_1);
                GtColor.toLch(endR, endG, endB, TMP_SHARED_2);
                float startHue = TMP_SHARED_1[2];
                float endHue = TMP_SHARED_2[2];
                if (TMP_SHARED_1[1] < GtColor.CHROMA_THRESHOLD)
                    TMP_SHARED_1[2] = endHue;
                else if (TMP_SHARED_2[1] < GtColor.CHROMA_THRESHOLD)
                    TMP_SHARED_2[2] = startHue;
                else if (startHue - endHue > MathUtils.PI)
                    TMP_SHARED_2[2] += MathUtils.PI2;
                else if (endHue - startHue > MathUtils.PI)
                    TMP_SHARED_1[2] += MathUtils.PI2;
                break;
            case LmsCompressed:
            case DegammaLmsCompressed:
                GtColor.toLmsCompressed(startR, startG, startB, TMP_SHARED_1);
                GtColor.toLmsCompressed(endR, endG, endB, TMP_SHARED_2);
                break;
            case Ipt:
            case DegammaIpt:
                GtColor.toIpt(startR, startG, startB, TMP_SHARED_1);
                GtColor.toIpt(endR, endG, endB, TMP_SHARED_2);
                break;
        }
        for (int i = 0; i < 3; i++) {
            setStartValue(i, TMP_SHARED_1[i]);
            setEndValue(i, TMP_SHARED_2[i]);
        }
    }

    private static void fixHxxStartEnd(){
        float startHue = TMP_SHARED_1[0];
        float endHue = TMP_SHARED_2[0];
        if (TMP_SHARED_1[1] < GtColor.SATURATION_THRESHOLD)
            TMP_SHARED_1[0] = endHue;
        else if (TMP_SHARED_2[1] < GtColor.SATURATION_THRESHOLD)
            TMP_SHARED_2[0] = startHue;
        else if (startHue - endHue > 180f)
            TMP_SHARED_2[0] += 360f;
        else if (endHue - startHue > 180f)
            TMP_SHARED_1[0] += 360f;
        if (TMP_SHARED_1[2] == 0f)
            TMP_SHARED_1[1] = TMP_SHARED_2[1];
        else if (TMP_SHARED_2[2] == 0f)
            TMP_SHARED_2[1] = TMP_SHARED_1[1];
    }

    @Override
    protected void apply(int vectorIndex, float value) {
        switch (colorSpace) {
            case DegammaRgb:
                value = GtColor.gammaCompress(value);
                // fall through
            case Rgb:
                value = MathUtils.clamp(value, 0f, 1f);
                switch (vectorIndex) {
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
            case Hcl:
            case DegammaHcl:
            case Hsl:
            case DegammaHsl:
            case Hsv:
            case DegammaHsv:
                if (vectorIndex == 0)
                    value = GtMathUtils.modulo(value, 360f);
                accumulator[vectorIndex] = value;
                break;
            case Lab:
            case DegammaLab:
                if (vectorIndex == 0)
                    value = Math.max(0f, value);
                accumulator[vectorIndex] = value;
                break;
            case Lch:
            case DegammaLch:
                if (vectorIndex == 2)
                    value = GtMathUtils.modulo(value, 360f);
                accumulator[vectorIndex] = value;
                break;
            case LmsCompressed:
            case DegammaLmsCompressed:
            case Ipt:
            case DegammaIpt:
                accumulator[vectorIndex] = value;
                break;
        }
    }

    @Override
    protected void applyAfter() {
        if (colorSpace == ColorSpace.Rgb || colorSpace == ColorSpace.DegammaRgb)
            return;
        switch (colorSpace) {
            case Hsl:
            case DegammaHsl:
                GtColor.fromHsl(target, accumulator[0], accumulator[1], accumulator[2]);
                break;
            case Hcl:
            case DegammaHcl:
                GtColor.fromHcl(target, accumulator[0], accumulator[1], accumulator[2]);
                break;
            case Hsv:
            case DegammaHsv:
                target.fromHsv(accumulator[0], accumulator[1], accumulator[2]);
                break;
            case Lab:
            case DegammaLab:
                GtColor.fromLab(target, accumulator[0], accumulator[1], accumulator[2]);
                break;
            case Lch:
            case DegammaLch:
                GtColor.fromLch(target, accumulator[0], accumulator[1], accumulator[2]);
                break;
            case LmsCompressed:
            case DegammaLmsCompressed:
                GtColor.fromLmsCompressed(target, accumulator[0], accumulator[1], accumulator[2]);
            case Ipt:
            case DegammaIpt:
                GtColor.fromIpt(target, accumulator[0], accumulator[1], accumulator[2]);
                break;
        }
        if (colorSpace.isDegamma) {
            target.r = GtColor.gammaCompress(target.r);
            target.g = GtColor.gammaCompress(target.g);
            target.b = GtColor.gammaCompress(target.b);
        }
    }

    @Override
    protected void applyAfterComplete() {
        if (colorSpace != ColorSpace.Rgb) {
            target.r = endR;
            target.g = endG;
            target.b = endB;
        }
    }

    public ColorTween end(float r, float g, float b) {
        endR = r;
        endG = g;
        endB = b;
        return this;
    }

    /**
     * Sets the type of color space the color is interpolated in. If using a BlendableEase and interrupting another
     * ColorTween, they can only blend if they use the same color space.
     *
     * @param colorSpace The value to set.
     * @return This ColorTween for chaining.
     */
    public ColorTween colorSpace(ColorSpace colorSpace) {
        this.colorSpace = colorSpace;
        return this;
    }

    /**
     * @return the final red value set.
     */
    public float getEndR() {
        return endR;
    }

    /**
     * @return the final green value set.
     */
    public float getEndG() {
        return endG;
    }

    /**
     * @return the final blue value set.
     */
    public float getEndB() {
        return endB;
    }

    /**
     * @return the type of color space the color is interpolated in.
     */
    public ColorSpace getColorSpace() {
        return colorSpace;
    }

    @Override
    public void free() {
        super.free();
        colorSpace = ColorSpace.DegammaRgb;
        POOL.free(this);
    }

}
