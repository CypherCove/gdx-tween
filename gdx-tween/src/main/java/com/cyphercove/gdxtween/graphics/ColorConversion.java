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
import org.jetbrains.annotations.NotNull;

/**
 * Utilities for modifying {@linkplain Color Colors} in color spaces other than RGB and HSV.
 * <p>
 * This is a port of parts of the Javascript library <a href="https://github.com/vinaypillai/ac-colors">ac-colors</a>,
 * with some modifications to the formulas to optimize use with LibGDX.
 */
public final class ColorConversion {
    private ColorConversion() {}

    private static final float X_D65 = .9505f;
    private static final float Y_D65 = 1f;
    private static final float Z_D65 = 1.089f;
    private static final float EPS_LAB = 216f / 24389f;
    private static final float KAP_LAB = 24389f / 27f;
    private static final float MAX_ZERO_TOLERANCE = (float)Math.pow(10.0, -12.0);

    /**
     * Sets the RGB of the color from the input cylindrical CIELAB (aka HCL) color space.
     * @param color Output color.
     * @param lchIn Input color array. Must be of at least length three.
     */
    public static void fromLch(@NotNull Color color, @NotNull float[] lchIn) {
        lchToLab(lchIn);
        labToXyz(lchIn);
        fromXyz(color, lchIn);
    }

    /**
     * Outputs the RGB of the color in the cylindrical CIELAB (aka HCL) color space.
     * @param color Input color.
     * @param lchOut Array the result will be placed in. Must have length of at least three.
     */
    public static void toLch(@NotNull Color color, @NotNull float[] lchOut) {
        toXyz(color, lchOut);
        xyzToLab(lchOut);
        labToLch(lchOut);
    }

    private static void toXyz(@NotNull Color color, @NotNull float[] xyzOut) {
        float r = invertGammaCorrection(color.r);
        float g = invertGammaCorrection(color.g);
        float b = invertGammaCorrection(color.b);
        xyzOut[0] = 0.4124f * r + 0.3576f * g + 0.1805f * b;
        xyzOut[1] = 0.2126f * r + 0.7152f * g + 0.0722f * b;
        xyzOut[2] = 0.0193f * r + 0.1192f * g + 0.9505f * b;
    }

    private static void fromXyz(@NotNull Color color, @NotNull float[] xyzIn) {
        float x = xyzIn[0];
        float y = xyzIn[1];
        float z = xyzIn[2];
        color.r = applyGammaCorrection(3.2406254773200533f * x - 1.5372079722103187f * y - 0.4986285986982479f * z);
        color.g = applyGammaCorrection(-0.9689307147293197f * x + 1.8757560608852415f * y + 0.041517523842953964f * z);
        color.b = applyGammaCorrection(0.055710120445510616f * x + -0.2040210505984867f * y + 1.0569959422543882f * z);
    }

    private static void xyzToLab(@NotNull float[] inOut) {
        float xR = inOut[0] / X_D65;
        float yR = inOut[1] / Y_D65;
        float zR = inOut[2] / Z_D65;
        float xF = forwardTransformXyz(xR);
        float yF = forwardTransformXyz(yR);
        float zF = forwardTransformXyz(zR);
        inOut[0] = 116f * yF - 16f;
        inOut[1] = 500f * (xF - yF);
        inOut[2] = 200f * (yF - zF);
    }

    private static void labToXyz(@NotNull float[] inOut) {
        float L = inOut[0];
        float a = inOut[1];
        float b = inOut[2];
        float yF = (L + 16f) / 116f;
        float zF = (yF - b / 200f);
        float xF = a / 500f + yF;
        float xR = (float)Math.pow(xF, 3.0);
        if (xR <= EPS_LAB) xR = (116f * xF - 16f) / KAP_LAB;
        float yR = L > KAP_LAB * EPS_LAB ? (float)Math.pow((L + 16f) / 116f, 3.0) : L / KAP_LAB;
        float zR = (float)Math.pow(zF, 3);
        if (zR <= EPS_LAB) zR = (116f * zF - 16f) / KAP_LAB;
        inOut[0] = xR * X_D65;
        inOut[1] = yR * Y_D65;
        inOut[2] = zR * Z_D65;
    }

    private static void labToLch(@NotNull float[] inOut) {
        float a = inOut[1];
        float b = inOut[2];
        if (b < MAX_ZERO_TOLERANCE) b = 0; // Round near zero to avoid weird atan2 behavior.
        inOut[1] = (float)Math.sqrt(a * a + b * b);
        inOut[2] = MathUtils.atan2(b, a) * MathUtils.radiansToDegrees;
        if (inOut[2] < 0) inOut[2] += 360f;
    }

    private static void lchToLab(@NotNull float[] inOut) {
        float c = inOut[1];
        float h = inOut[2];
        inOut[1] = c * MathUtils.cos(h * MathUtils.degreesToRadians);
        inOut[2] = c * MathUtils.sin(h * MathUtils.degreesToRadians);
    }

    private static float invertGammaCorrection(float component) {
        return (component <= 0.04045f) ? component / 12.92f : (float)Math.pow((component + 0.055f) / 1.055f, 2.4);
    }

    private static float applyGammaCorrection(float component) {
        return component <= 0.0031308f ? 12.92f * component : 1.055f * (float)Math.pow(component, 1.0 / 2.4) - 0.055f;
    }

    private static float forwardTransformXyz(float component) {
        return component > EPS_LAB ? (float)Math.pow(component, 1.0 / 3.0) : (KAP_LAB * component + 16f) / 116f;
    }
}
