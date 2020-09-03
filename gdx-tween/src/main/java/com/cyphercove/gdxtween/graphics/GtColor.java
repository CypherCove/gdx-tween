/* ******************************************************************************
 * MIT License
 *
 * Copyright (c) 2020 Vinay
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.cyphercove.gdxtween.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.cyphercove.gdxtween.math.GtMathUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Utilities for modifying {@linkplain Color Colors} in color spaces other than RGB and HSV.
 * <p>
 * This is a port of parts of the Javascript library <a href="https://github.com/vinaypillai/ac-colors">ac-colors</a>,
 * with some modifications to the formulas to optimize use with LibGDX.
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
     * Determines whether the color's RGB components are equivalent at RGB888 precision.
     *
     * @param color The color to analyze.
     * @return True if the color is desaturated (pure gray).
     */
    public boolean isDesaturated(Color color) {
        int r = (int) (color.r * 255);
        int g = (int) (color.g * 255);
        if (r != g)
            return false;
        int b = (int) (color.b * 255);
        return r == b;
    }

    /**
     * Determines whether the RGB components are equivalent at RGB888 precision.
     *
     * @param red   Red component in 0..1 range.
     * @param green Green component in 0..1 range.
     * @param blue  Blue component in 0..1 range.
     * @return True if the color is desaturated (pure gray).
     */
    public boolean isDesaturated(float red, float green, float blue) {
        int r = (int) (red * 255);
        int g = (int) (green * 255);
        if (r != g)
            return false;
        int b = (int) (blue * 255);
        return r == b;
    }

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
     * Linearly interpolates RGB between the two colors in gamma-corrected RGB color space. Alpha is not modified.
     *
     * @param startAndResult The starting color. The result is placed in this color object.
     * @param end            The target color.
     * @param t              The interpolation coefficient. Must be in the range 0..1.
     */
    public static void lerpRgbGammaCorrected(Color startAndResult, Color end, float t) {
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
    public static void lerpRgbAGammaCorrected(Color startAndResult, Color end, float t) {
        lerpRgbGammaCorrected(startAndResult, end, t);
        startAndResult.a = startAndResult.a + t * (end.a - startAndResult.a);
    }

    /**
     * Linearly interpolates RGB between the two colors using RGB color space, and then linearly interpolates the HSV
     * saturation and modifies the result's saturation in HSV color space to match. Alpha is not modified.
     *
     * @param startAndResult The starting color. The result is placed in this color object.
     * @param end            The target color.
     * @param t              The interpolation coefficient. Must be in the range 0..1.
     */
    public static void lerpRgbS(Color startAndResult, Color end, float t) {
        float startSaturation = getSaturation(startAndResult);
        startAndResult.r = startAndResult.r + t * (end.r - startAndResult.r);
        startAndResult.g = startAndResult.g + t * (end.g - startAndResult.g);
        startAndResult.b = startAndResult.b + t * (end.b - startAndResult.b);
        startAndResult.toHsv(FLOAT3_A);
        FLOAT3_A[1] = startSaturation + t * (getSaturation(end) - startSaturation);
        startAndResult.fromHsv(FLOAT3_A);
    }

    /**
     * Linearly interpolates RGB between the two colors using RGB color space, and then linearly interpolates the HSV
     * saturation and modifies the result's saturation in HSV color space to match. Alpha is also linearly interpolated.
     *
     * @param startAndResult The starting color. The result is placed in this color object.
     * @param end            The target color.
     * @param t              The interpolation coefficient. Must be in the range 0..1.
     */
    public static void lerpRgbSA(Color startAndResult, Color end, float t) {
        lerpRgbS(startAndResult, end, t);
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
     * @param l     Luminance.
     * @param c     Chroma.
     * @param h     Hue angle in radians.
     */
    public static void fromLch(@NotNull Color color, float l, float c, float h) {
        float a = c * MathUtils.cos(h);
        float b = c * MathUtils.sin(h);
        float yF = (l + 16f) / 116f;
        float zF = yF - b / 200f;
        float xF = yF + a / 500f;
        float x = X_D65 * (xF > SIGMA_LAB ? xF * xF * xF : (116f * xF - 16f) / KAP_LAB);
        float y = l > KAP_LAB * SIGMA_LAB3 ? yF * yF * yF : l / KAP_LAB;
        float z = Z_D65 * (zF > SIGMA_LAB ? zF * zF * zF : (116f * zF - 16f) / KAP_LAB);
        color.r = applyGammaCorrection(3.2406254773200533f * x - 1.5372079722103187f * y - 0.4986285986982479f * z);
        color.g = applyGammaCorrection(-0.9689307147293197f * x + 1.8757560608852415f * y + 0.041517523842953964f * z);
        color.b = applyGammaCorrection(0.055710120445510616f * x + -0.2040210505984867f * y + 1.0569959422543882f * z);
        clampRgb(color);
    }

    /**
     * Sets the RGB of the color from the input cylindrical CIELAB (aka HCL) color space.
     *
     * @param color Output color.
     * @param lchIn Input color array, with Luminance, chroma and hue angle in the first three indices respectively.
     *              Length must be at least 3. The hue angle is in radians.
     */
    public static void fromLch(@NotNull Color color, @NotNull float[] lchIn) {
        fromLch(color, lchIn[0], lchIn[1], lchIn[2]);
    }

    /**
     * Outputs the RGB of the color in the cylindrical CIELAB (aka HCL) color space.
     *
     * @param color  Input color.
     * @param lchOut Array the result will be placed in, with Luminance, chroma and hue angle in the first three indices
     *               respectively. Length must be at least 3. The hue angle is in radians to save the step of converting
     *               to degrees. The angle is in the range -PI..PI.
     */
    public static void toLch(@NotNull Color color, @NotNull float[] lchOut) {
        float red = invertGammaCorrection(color.r);
        float green = invertGammaCorrection(color.g);
        float blue = invertGammaCorrection(color.b);
        float x = 0.4124f * red + 0.3576f * green + 0.1805f * blue;
        float y = 0.2126f * red + 0.7152f * green + 0.0722f * blue;
        float z = 0.0193f * red + 0.1192f * green + 0.9505f * blue;
        float xF = forwardTransformXyz(x / X_D65);
        float yF = forwardTransformXyz(y);
        float zF = forwardTransformXyz(z / Z_D65);
        lchOut[0] = 116f * yF - 16f;
        float a = 500f * (xF - yF);
        float b = 200f * (yF - zF);
        lchOut[1] = (float) Math.sqrt(a * a + b * b);
        lchOut[2] = GtMathUtils.atan2(b, a);
    }

    public static void fromLab(@NotNull Color color, float l, float a, float b) {
        float yF = (l + 16f) / 116f;
        float zF = yF - b / 200f;
        float xF = yF + a / 500f;
        float x = X_D65 * (xF > SIGMA_LAB ? xF * xF * xF : (116f * xF - 16f) / KAP_LAB);
        float y = l > KAP_LAB * SIGMA_LAB3 ? yF * yF * yF : l / KAP_LAB;
        float z = Z_D65 * (zF > SIGMA_LAB ? zF * zF * zF : (116f * zF - 16f) / KAP_LAB);
        color.r = applyGammaCorrection(3.2406254773200533f * x - 1.5372079722103187f * y - 0.4986285986982479f * z);
        color.g = applyGammaCorrection(-0.9689307147293197f * x + 1.8757560608852415f * y + 0.041517523842953964f * z);
        color.b = applyGammaCorrection(0.055710120445510616f * x + -0.2040210505984867f * y + 1.0569959422543882f * z);
        clampRgb(color);
    }

    public static void fromLab(@NotNull Color color, @NotNull float[] labIn) {
        fromLab(color, labIn[0], labIn[1], labIn[2]);
    }

    public static void toLab(@NotNull Color color, @NotNull float[] labOut) {
        float red = invertGammaCorrection(color.r);
        float green = invertGammaCorrection(color.g);
        float blue = invertGammaCorrection(color.b);
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

    private static float invertGammaCorrection(float component) {
        return (component <= 0.04045f) ? component / 12.92f : (float) Math.pow((component + 0.055f) / 1.055f, 2.4);
    }

    private static float applyGammaCorrection(float component) {
        return component <= 0.0031308f ? 12.92f * component : 1.055f * (float) Math.pow(component, 1.0 / 2.4) - 0.055f;
    }

    private static float forwardTransformXyz(float component) {
        return component > SIGMA_LAB3 ? (float) Math.cbrt(component) : (KAP_LAB * component + 16f) / 116f;
    }

    private static void clampRgb(Color color) {
        if (color.r < 0) color.r = 0;
        else if (color.r > 1) color.r = 1;
        if (color.g < 0) color.g = 0;
        else if (color.g > 1) color.g = 1;
        if (color.b < 0) color.b = 0;
        else if (color.b > 1) color.b = 1;
    }

    private static float getSaturation(Color color) {
        float r = color.r, g = color.g, b = color.b;
        float max = Math.max(b, Math.max(r, g));
        float min = Math.min(b, Math.min(r, g));
        if (max == min)
            return 0;
        return (max - min) / max;
    }
}
