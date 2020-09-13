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
package com.cyphercove.gdxtween.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.cyphercove.gdxtween.math.GtMathUtils;

/**
 * Utilities for modifying {@linkplain Color Colors} in color spaces other than RGB and HSV.
 * <p>
 * The algorithms and equations for applyGammaCorrection, invertGammaCorrection, toLab, fromLab, toLch, and fromLch
 * were originally ported from the Javascript library  * <a href="https://github.com/vinaypillai/ac-colors">ac-colors</a>.
 * See the license file LICENSE-AC-COLORS.md.
 */
public final class GtColor {
    private GtColor() {
    }

    /**
     * If HSV saturation is lower than this value, the color is effectively grayscale for RGB888 precision.
     */
    public static final float SATURATION_THRESHOLD = 0.9f / 255f;
    /**
     * If LCH Chroma is lower than this value, the color is effectively grayscale for RGB888 precision.
     */
    public static final float CHROMA_THRESHOLD = 0.9f * 0.34850854f;

    private static final float X_D65 = 0.9505f;
    private static final float Z_D65 = 1.089f;
    private static final float SIGMA_LAB = 6f / 29F;
    private static final float SIGMA_LAB3 = SIGMA_LAB * SIGMA_LAB * SIGMA_LAB;
    private static final float KAP_LAB = 24389f / 27f;
    private static final float[] FLOAT3_A = new float[3];
    private static final float[] FLOAT3_B = new float[3];

    /**
     * Linearly interpolates RGB between the two colors using RGB color space. Alpha is not modified.
     *
     * @param startAndResult The starting color. The result is placed in this color object.
     * @param end            The target color.
     * @param t              The interpolation coefficient. Must be in the range 0..1.
     */
    public static void lerpRgb(Color startAndResult, Color end, float t) {
        startAndResult.r = startAndResult.r + t * (end.r - startAndResult.r);
        startAndResult.g = startAndResult.g + t * (end.g - startAndResult.g);
        startAndResult.b = startAndResult.b + t * (end.b - startAndResult.b);
    }

    /**
     * Linearly interpolates RGB between the two colors in linear RGB color space (gamma correction removed). Alpha is
     * not modified.
     *
     * @param startAndResult The starting color. The result is placed in this color object.
     * @param end            The target color.
     * @param t              The interpolation coefficient. Must be in the range 0..1.
     */
    public static void lerpLinearRgb(Color startAndResult, Color end, float t) {
        float red = invertGammaCorrection(startAndResult.r);
        float green = invertGammaCorrection(startAndResult.g);
        float blue = invertGammaCorrection(startAndResult.b);
        startAndResult.r = applyGammaCorrection(red + t * (invertGammaCorrection(end.r) - red));
        startAndResult.g = applyGammaCorrection(green + t * (invertGammaCorrection(end.g) - green));
        startAndResult.b = applyGammaCorrection(blue + t * (invertGammaCorrection(end.b) - blue));
    }

    /**
     * Linearly interpolates RGB between the two colors in gamma-corrected RGB color space. Alpha is also linearly
     * interpolated.
     *
     * @param startAndResult The starting color. The result is placed in this color object.
     * @param end            The target color.
     * @param t              The interpolation coefficient. Must be in the range 0..1.
     */
    public static void lerpRgbAGamma(Color startAndResult, Color end, float t) {
        lerpLinearRgb(startAndResult, end, t);
        startAndResult.a = startAndResult.a + t * (end.a - startAndResult.a);
    }

    /**
     * Linearly interpolates RGB between the two colors using HSV color space. Alpha is not modified.
     *
     * @param startAndResult The starting color. The result is placed in this color object.
     * @param end            The target color.
     * @param t              The interpolation coefficient. Must be in the range 0..1.
     */
    public static void lerpHsv(Color startAndResult, Color end, float t) {
        startAndResult.toHsv(FLOAT3_A);
        end.toHsv(FLOAT3_B);
        float startHue = FLOAT3_A[0];
        float endHue = FLOAT3_B[0];
        if (FLOAT3_A[1] < SATURATION_THRESHOLD)
            startHue = endHue;
        else if (FLOAT3_B[1] < SATURATION_THRESHOLD)
            endHue = startHue;
        else if (startHue - endHue > 180f)
            endHue += 360f;
        else if (endHue - startHue > 180f)
            startHue += 360f;
        FLOAT3_A[0] = (startHue + t * (endHue - startHue)) % 360f;
        FLOAT3_A[1] = FLOAT3_A[1] + t * (FLOAT3_B[1] - FLOAT3_A[1]);
        FLOAT3_A[2] = FLOAT3_A[2] + t * (FLOAT3_B[2] - FLOAT3_A[2]);
        startAndResult.fromHsv(FLOAT3_A);
    }

    /**
     * Linearly interpolates RGB between the two colors using HSV color space. Alpha is also linearly interpolated.
     *
     * @param startAndResult The starting color. The result is placed in this color object.
     * @param end            The target color.
     * @param t              The interpolation coefficient. Must be in the range 0..1.
     */
    public static void lerpHsvA(Color startAndResult, Color end, float t) {
        lerpHsv(startAndResult, end, t);
        startAndResult.a = startAndResult.a + t * (end.a - startAndResult.a);
    }

    /**
     * Linearly interpolates RGB between the two colors using cylindrical CIELAB (aka HCL) color space. Alpha is not modified.
     *
     * @param startAndResult The starting color. The result is placed in this color object.
     * @param end            The target color.
     * @param t              The interpolation coefficient. Must be in the range 0..1.
     */
    public static void lerpLch(Color startAndResult, Color end, float t) {
        toLch(startAndResult, FLOAT3_A);
        toLch(end, FLOAT3_B);
        float startHue = FLOAT3_A[2];
        float endHue = FLOAT3_B[2];
        if (FLOAT3_A[1] < CHROMA_THRESHOLD)
            startHue = endHue;
        else if (FLOAT3_B[1] < CHROMA_THRESHOLD)
            endHue = startHue;
        else if (startHue - endHue > MathUtils.PI)
            endHue += MathUtils.PI2;
        else if (endHue - startHue > MathUtils.PI)
            startHue += MathUtils.PI2;
        FLOAT3_A[0] = FLOAT3_A[0] + t * (FLOAT3_B[0] - FLOAT3_A[0]);
        FLOAT3_A[1] = FLOAT3_A[1] + t * (FLOAT3_B[1] - FLOAT3_A[1]);
        FLOAT3_A[2] = startHue + t * (endHue - startHue);
        fromLch(startAndResult, FLOAT3_A);
    }

    /**
     * Linearly interpolates RGB between the two colors using cylindrical CIELAB (aka HCL) color space.  Alpha is also
     * linearly interpolated.
     *
     * @param startAndResult The starting color. The result is placed in this color object.
     * @param end            The target color.
     * @param t              The interpolation coefficient. Must be in the range 0..1.
     */
    public static void lerpLchA(Color startAndResult, Color end, float t) {
        lerpLch(startAndResult, end, t);
        startAndResult.a = startAndResult.a + t * (end.a - startAndResult.a);
    }

    /**
     * Linearly interpolates RGB between the two colors using CIELAB (aka Lab) color space. Alpha is not modified.
     *
     * @param startAndResult The starting color. The result is placed in this color object.
     * @param end            The target color.
     * @param t              The interpolation coefficient. Must be in the range 0..1.
     */
    public static void lerpLab(Color startAndResult, Color end, float t) {
        toLab(startAndResult, FLOAT3_A);
        toLab(end, FLOAT3_B);
        FLOAT3_A[0] = FLOAT3_A[0] + t * (FLOAT3_B[0] - FLOAT3_A[0]);
        FLOAT3_A[1] = FLOAT3_A[1] + t * (FLOAT3_B[1] - FLOAT3_A[1]);
        FLOAT3_A[2] = FLOAT3_A[2] + t * (FLOAT3_B[2] - FLOAT3_A[2]);
        fromLab(startAndResult, FLOAT3_A);
    }

    /**
     * Linearly interpolates RGB between the two colors using CIELAB (aka Lab) color space.  Alpha is also linearly
     * interpolated.
     *
     * @param startAndResult The starting color. The result is placed in this color object.
     * @param end            The target color.
     * @param t              The interpolation coefficient. Must be in the range 0..1.
     */
    public static void lerpLabA(Color startAndResult, Color end, float t) {
        lerpLab(startAndResult, end, t);
        startAndResult.a = startAndResult.a + t * (end.a - startAndResult.a);
    }

    /**
     * Sets the RGB of the color from the input cylindrical CIELAB (aka HCL) color space.
     *
     * @param color Output color.
     * @param lchIn Input color array, with Luminance, chroma and hue angle in the first three indices respectively.
     *              Length must be at least 3. The hue angle is in radians.
     */
    public static void fromLch(Color color, float[] lchIn) {
        float c = lchIn[1];
        float h = lchIn[2];
        lchIn[1] = c * MathUtils.cos(h);
        lchIn[2] = c * MathUtils.sin(h);
        fromLab(color, lchIn[0], lchIn[1], lchIn[2]);
    }

    /**
     * Outputs the RGB of the color in the cylindrical CIELAB (aka HCL) color space.
     *
     * @param color  Input color.
     * @param lchOut Array the result will be placed in, with Luminance, chroma and hue angle in the first three indices
     *               respectively. Length must be at least 3. The hue angle is in radians to save the step of converting
     *               to degrees. The angle is in the range -PI..PI.
     */
    public static void toLch(Color color, float[] lchOut) {
        toLab(color, lchOut);
        float a = lchOut[1];
        float b = lchOut[2];
        lchOut[1] = (float) Math.sqrt(a * a + b * b);
        lchOut[2] = GtMathUtils.atan2(b, a);
    }

    public static void fromLab(Color color, float l, float a, float b) {
        float yF = (l + 16f) / 116f;
        float zF = yF - b / 200f;
        float xF = yF + a / 500f;
        float x = X_D65 * (xF > SIGMA_LAB ? xF * xF * xF : (116f * xF - 16f) / KAP_LAB);
        float y = l > KAP_LAB * SIGMA_LAB3 ? yF * yF * yF : l / KAP_LAB;
        float z = Z_D65 * (zF > SIGMA_LAB ? zF * zF * zF : (116f * zF - 16f) / KAP_LAB);
        color.r = applyGammaCorrection(3.2406254773200533f * x - 1.5372079722103187f * y - 0.4986285986982479f * z);
        color.g = applyGammaCorrection(-0.9689307147293197f * x + 1.8757560608852415f * y + 0.041517523842953964f * z);
        color.b = applyGammaCorrection(0.055710120445510616f * x + -0.2040210505984867f * y + 1.0569959422543882f * z);
        if (color.r < 0) color.r = 0;
        else if (color.r > 1) color.r = 1;
        if (color.g < 0) color.g = 0;
        else if (color.g > 1) color.g = 1;
        if (color.b < 0) color.b = 0;
        else if (color.b > 1) color.b = 1;
    }

    public static void fromLab(Color color, float[] labIn) {
        fromLab(color, labIn[0], labIn[1], labIn[2]);
    }

    public static void toLab(Color color, float[] labOut) {
        toLab(color.r, color.g, color.b, labOut);
    }

    public static void toLab(float r, float g, float b, float[] labOut) {
        float red = invertGammaCorrection(r);
        float green = invertGammaCorrection(g);
        float blue = invertGammaCorrection(b);
        float x = 0.4124f * red + 0.3576f * green + 0.1805f * blue;
        float y = 0.2126f * red + 0.7152f * green + 0.0722f * blue;
        float z = 0.0193f * red + 0.1192f * green + 0.9505f * blue;
        float xF = forwardTransformXyz(x / X_D65);
        float yF = forwardTransformXyz(y);
        float zF = forwardTransformXyz(z / Z_D65);
        labOut[0] = 116f * yF - 16f;
        labOut[1] = 500f * (xF - yF);
        labOut[2] = 200f * (yF - zF);
    }

    /**
     * Converts a color channel from gamma-corrected to linear scale.
     *
     * @param component The color channel to make linear.
     * @return The color value in linear scale.
     */
    public static float invertGammaCorrection(float component) {
        return (component <= 0.04045f) ? component / 12.92f : (float) Math.pow((component + 0.055f) / 1.055f, 2.4);
    }

    /**
     * Converts a color channel from linear to gamma-corrected scale.
     *
     * @param component The color channel to gamma-correct.
     * @return The color value with gamma correction.
     */
    public static float applyGammaCorrection(float component) {
        return component <= 0.0031308f ? 12.92f * component : 1.055f * (float) Math.pow(component, 1.0 / 2.4) - 0.055f;
    }

    private static float forwardTransformXyz(float component) {
        return component > SIGMA_LAB3 ? (float) Math.cbrt(component) : (KAP_LAB * component + 16f) / 116f;
    }
}
