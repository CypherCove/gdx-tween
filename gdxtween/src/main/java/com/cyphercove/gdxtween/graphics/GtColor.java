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
 * The method {@link #fromLab(Color, float[])} contains an optimization from the Javascript library
 * <a href="https://github.com/vinaypillai/ac-colors">ac-colors</a>. See the license file LICENSE-AC-COLORS.md.
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
    private static final Color tempEnd = new Color(); // Used internally only in lerp()

    /**
     *
     * @param startAndResult
     * @param end
     * @param t
     * @param colorSpace
     * @return
     */
    public static Color lerp(Color startAndResult, Color end, float t, ColorSpace colorSpace, boolean includeAlpha) {
        Color endCorrected;
        if (colorSpace.isDegamma){
            gammaExpand(startAndResult, includeAlpha);
            gammaExpand(tempEnd.set(end), includeAlpha);
            endCorrected = tempEnd;
        } else {
            endCorrected = end;
        }
        switch (colorSpace){
            case Rgb:
            case DegammaRgb:
                startAndResult.r = startAndResult.r + t * (endCorrected.r - startAndResult.r);
                startAndResult.g = startAndResult.g + t * (endCorrected.g - startAndResult.g);
                startAndResult.b = startAndResult.b + t * (endCorrected.b - startAndResult.b);
                break;
            case Hsl:
            case DegammaHsl:
                lerpHsl(startAndResult, endCorrected, t);
                break;
            case Hcl:
            case DegammaHcl:
                lerpHcl(startAndResult, endCorrected, t);
                break;
            case EuclideanHcl:
            case DegammaEuclideanHcl:
                lerpEuclideanHcl(startAndResult, endCorrected, t);
                break;
            case Hsv:
            case DegammaHsv:
                lerpHsv(startAndResult, endCorrected, t);
                break;
            case Lab:
            case DegammaLab:
                lerpLab(startAndResult, endCorrected, t);
                break;
            case Lch:
            case DegammaLch:
                lerpLch(startAndResult, endCorrected, t);
                break;
            case LmsCompressed:
            case DegammaLmsCompressed:
                lerpLmsCompressed(startAndResult, endCorrected, t);
                break;
            case Ipt:
            case DegammaIpt:
                lerpIpt(startAndResult, endCorrected, t);
                break;
        }
        if (includeAlpha){
            startAndResult.a = startAndResult.a + t * (endCorrected.a - startAndResult.a);
        }
        if (colorSpace.isDegamma){
            gammaCompress(startAndResult, includeAlpha);
        }
        return startAndResult;
    }

    private static void lerpHsv(Color startAndResult, Color end, float t) {
        startAndResult.toHsv(FLOAT3_A);
        end.toHsv(FLOAT3_B);
        lerpHxx(FLOAT3_A, FLOAT3_B, t, true);
        startAndResult.fromHsv(FLOAT3_A);
    }

    private static void lerpHcl(Color startAndResult, Color end, float t) {
        toHcl(startAndResult, FLOAT3_A);
        toHcl(end, FLOAT3_B);
        lerpHxx(FLOAT3_A, FLOAT3_B, t, false);
        fromHcl(startAndResult, FLOAT3_A);
    }

    private static void lerpHsl(Color startAndResult, Color end, float t) {
        toHsl(startAndResult, FLOAT3_A);
        toHsl(end, FLOAT3_B);
        lerpHxx(FLOAT3_A, FLOAT3_B, t, true);
        fromHsl(startAndResult, FLOAT3_A);
    }

    private static void lerpEuclideanHcl(Color startAndResult, Color end, float t) {
        toHcl(startAndResult, FLOAT3_A);
        toHcl(end, FLOAT3_B);
        FLOAT3_A[0] = GtMathUtils.modulo(FLOAT3_A[0], 360f);
        FLOAT3_B[0] = GtMathUtils.modulo(FLOAT3_B[0], 360f);
        float xStart = MathUtils.cosDeg(FLOAT3_A[0]) * FLOAT3_A[1];
        float xEnd = MathUtils.cosDeg(FLOAT3_B[0]) * FLOAT3_B[1];
        float yStart = MathUtils.sinDeg(FLOAT3_A[0]) * FLOAT3_A[1];
        float yEnd = MathUtils.sinDeg(FLOAT3_B[0]) * FLOAT3_B[1];
        float x = MathUtils.lerp(xStart, xEnd, t);
        float y = MathUtils.lerp(yStart, yEnd, t);
        FLOAT3_A[0] = GtMathUtils.atan2(y, x) * MathUtils.radDeg;
        FLOAT3_A[1] = (float) Math.sqrt(x * x + y * y);
        FLOAT3_A[2] = FLOAT3_A[2] + t * (FLOAT3_B[2] - FLOAT3_A[2]);
        fromHcl(startAndResult, FLOAT3_A);
    }

    private static void lerpHxx(float[] h_cs_lv_A, float[] h_cs_lv_B, float t, boolean saturation){
        float startHue = h_cs_lv_A[0];
        float endHue = h_cs_lv_B[0];
        float startCOrS = h_cs_lv_A[1];
        float endCOrS = h_cs_lv_B[1];
        float startLOrV = h_cs_lv_A[2];
        float endLOrV = h_cs_lv_B[2];
        if (startCOrS < SATURATION_THRESHOLD)
            startHue = endHue;
        else if (endCOrS < SATURATION_THRESHOLD)
            endHue = startHue;
        else if (startHue - endHue > 180f)
            endHue += 360f;
        else if (endHue - startHue > 180f)
            startHue += 360f;
        if (saturation) {
            if (startLOrV == 0f)
                startCOrS = endCOrS;
            else if (endLOrV == 0f)
                endCOrS = startCOrS;
        }
        h_cs_lv_A[0] = (startHue + t * (endHue - startHue)) % 360f;
        h_cs_lv_A[1] = startCOrS + t * (endCOrS - startCOrS);
        h_cs_lv_A[2] = startLOrV + t * (endLOrV - startLOrV);
    }

    private static void lerpLab(Color startAndResult, Color end, float t) {
        toLab(startAndResult, FLOAT3_A);
        toLab(end, FLOAT3_B);
        for (int i = 0; i < 3; i++) {
            FLOAT3_A[i] = FLOAT3_A[i] + t * (FLOAT3_B[i] - FLOAT3_A[i]);
        }
        fromLab(startAndResult, FLOAT3_A);
    }

    private static void lerpLch(Color startAndResult, Color end, float t) {
        toLch(startAndResult, FLOAT3_A);
        toLch(end, FLOAT3_B);
        float startHue = FLOAT3_A[2];
        float endHue = FLOAT3_B[2];
        if (FLOAT3_A[1] < CHROMA_THRESHOLD)
            startHue = endHue;
        else if (FLOAT3_B[1] < CHROMA_THRESHOLD)
            endHue = startHue;
        else if (startHue - endHue > 180f)
            endHue += 360f;
        else if (endHue - startHue > 180f)
            startHue += 360f;
        FLOAT3_A[0] = FLOAT3_A[0] + t * (FLOAT3_B[0] - FLOAT3_A[0]);
        FLOAT3_A[1] = FLOAT3_A[1] + t * (FLOAT3_B[1] - FLOAT3_A[1]);
        FLOAT3_A[2] = startHue + t * (endHue - startHue);
        fromLch(startAndResult, FLOAT3_A);
    }

    private static void lerpLmsCompressed(Color startAndResult, Color end, float t) {
        toLmsCompressed(startAndResult, FLOAT3_A);
        toLmsCompressed(end, FLOAT3_B);
        for (int i = 0; i < 3; i++) {
            FLOAT3_A[i] = FLOAT3_A[i] + t * (FLOAT3_B[i] - FLOAT3_A[i]);
        }
        fromLmsCompressed(startAndResult, FLOAT3_A);
    }

    private static void lerpIpt(Color startAndResult, Color end, float t) {
        toIpt(startAndResult, FLOAT3_A);
        toIpt(end, FLOAT3_B);
        for (int i = 0; i < 3; i++) {
            FLOAT3_A[i] = FLOAT3_A[i] + t * (FLOAT3_B[i] - FLOAT3_A[i]);
        }
        fromIpt(startAndResult, FLOAT3_A);
    }

    /**
     * Converts the color to Hue-chroma-lightness color space. Assumes the input color is already in linear RGB (not
     * gamma corrected sRGB).
     * @param r Input red channel in linear space.
     * @param g Input green channel in linear space.
     * @param b Input blue channel in linear space.
     * @param hclOut The output HCL color. Must be of length at least 3.
     */
    public static void toHcl(float r, float g, float b, float[] hclOut) {
        toHxl(r, g, b, hclOut, false);
    }

    /**
     * Converts the color to Hue-chroma-lightness color space. Assumes the input color is already in linear RGB (not
     * gamma corrected sRGB).
     * @param color The input color in linear RGB.
     * @param hclOut The output HCL color. Must be of length at least 3.
     */
    public static void toHcl(Color color, float[] hclOut) {
        toHxl(color.r, color.g, color.b, hclOut, false);
    }

    /**
     * Converts the color to Hue-saturation-lightness color space. Assumes the input color is already in linear RGB (not
     * gamma corrected sRGB).
     * @param r Input red channel in linear space.
     * @param g Input green channel in linear space.
     * @param b Input blue channel in linear space.
     * @param hslOut The output HCL color. Must be of length at least 3.
     */
    public static void toHsl(float r, float g, float b, float[] hslOut) {
        toHxl(r, g, b, hslOut, true);
    }

    /**
     * Converts the color to Hue-saturation-lightness color space. Assumes the input color is already in linear RGB (not
     * gamma corrected sRGB).
     * @param color The input color in linear RGB.
     * @param hslOut The output HCL color. Must be of length at least 3.
     */
    public static void toHsl(Color color, float[] hslOut) {
        toHxl(color.r, color.g, color.b, hslOut, true);
    }

    private static void toHxl(float r, float g, float b, float[] hxlOut, boolean saturation) {
        float max = Math.max(Math.max(r, g), b);
        float min = Math.min(Math.min(r, g), b);
        float chroma = max - min;
        if (chroma == 0) {
            hxlOut[0] = 0;
        } else if (max == r) {
            hxlOut[0] = (60 * (g - b) / chroma + 360) % 360;
        } else if (max == g) {
            hxlOut[0] = 60 * (b - r) / chroma + 120;
        } else {
            hxlOut[0] = 60 * (r - g) / chroma + 240;
        }
        float doubleLightness = min + max;
        if (saturation){
            if (doubleLightness == 0f || doubleLightness > 2f * 254f / 255f){
                hxlOut[1] = 0f;
            } else {
                hxlOut[1] = chroma / (1f - Math.abs(doubleLightness - 1f));
            }
        } else {
            hxlOut[1] = chroma;
        }
        hxlOut[2] = 0.5f * doubleLightness;
    }

    /**
     * Sets the RGB of the color from the input HCL color space. The result will be in linear RGB color (not gamma
     * compressed sRGB).
     * @param color The color output.
     * @param h The input hue channel in degrees.
     * @param c The input chroma channel.
     * @param l The input lightness channel.
     */
    public static void fromHcl(Color color, float h, float c, float l) {
        fromHxl(color, h, c, l, false);
    }

    /**
     * Sets the RGB of the color from the input HCL color space. The result will be in linear RGB color (not gamma
     * compressed sRGB).
     * @param color The color output.
     * @param hclIn The input HCL values. Must have a size of at least 3. Hue is in degrees.
     */
    public static void fromHcl(Color color, float[] hclIn) {
        fromHxl(color, hclIn[0], hclIn[1], hclIn[2], false);
    }

    /**
     * Sets the RGB of the color from the input HSL color space. The result will be in linear RGB color (not gamma
     * compressed sRGB).
     * @param color The color output.
     * @param h The input hue channel in degrees.
     * @param s The input saturation channel.
     * @param l The input lightness channel.
     */
    public static void fromHsl(Color color, float h, float s, float l) {
        fromHxl(color, h, s, l, true);
    }

    /**
     * Sets the RGB of the color from the input HSL color space. The result will be in linear RGB color (not gamma
     * compressed sRGB).
     * @param color The color output.
     * @param hslIn The input HSL values. Must have a size of at least 3. Hue is in degrees.
     */
    public static void fromHsl(Color color, float[] hslIn) {
        fromHxl(color, hslIn[0], hslIn[1], hslIn[2], true);
    }

    private static void fromHxl(Color color, float h, float n, float l, boolean saturation) {
        float doubleLightness = l * 2;
        float chroma = saturation ? (1 - Math.abs(doubleLightness - 1f)) * n : n;
        float v = l + chroma / 2f;
        float s = l == 0f || l > 254f / 255f ? 0f : 2 * (1f - l / v);
        float x = (h / 60f + 6) % 6;
        int i = (int)x;
        float f = x - i;
        float p = v * (1 - s);
        float q = v * (1 - s * f);
        float t = v * (1 - s * (1 - f));
        switch (i) {
            case 0:
                color.r = v;
                color.g = t;
                color.b = p;
                break;
            case 1:
                color.r = q;
                color.g = v;
                color.b = p;
                break;
            case 2:
                color.r = p;
                color.g = v;
                color.b = t;
                break;
            case 3:
                color.r = p;
                color.g = q;
                color.b = v;
                break;
            case 4:
                color.r = t;
                color.g = p;
                color.b = v;
                break;
            default:
                color.r = v;
                color.g = p;
                color.b = q;
        }
        if (color.r < 0) color.r = 0;
        else if (color.r > 1) color.r = 1;
        if (color.g < 0) color.g = 0;
        else if (color.g > 1) color.g = 1;
        if (color.b < 0) color.b = 0;
        else if (color.b > 1) color.b = 1;
    }

    /**
     * Sets the RGB of the color from the input CIELAB color space. The result will be in linear RGB color (not gamma
     * compressed sRGB).
     * @param color The color output.
     * @param l The input L* luminance channel.
     * @param a The input a* channel.
     * @param b the input b* channel
     */
    public static void fromLab(Color color, float l, float a, float b) {
        float yF = (l + 16f) / 116f;
        float zF = yF - b / 200f;
        float xF = yF + a / 500f;
        float x = X_D65 * (xF > SIGMA_LAB ? xF * xF * xF : (116f * xF - 16f) / KAP_LAB);
        float y = l > KAP_LAB * SIGMA_LAB3 ? yF * yF * yF : l / KAP_LAB;
        float z = Z_D65 * (zF > SIGMA_LAB ? zF * zF * zF : (116f * zF - 16f) / KAP_LAB);
        color.r = 3.2404542f * x - 1.5371385f * y - 0.4985314f * z;
        color.g = -0.9692660f * x + 1.8760108f * y + 0.0415560f * z;
        color.b = 0.0556434f * x + -0.2040259f * y + 1.0572252f * z;
        if (color.r < 0) color.r = 0;
        else if (color.r > 1) color.r = 1;
        if (color.g < 0) color.g = 0;
        else if (color.g > 1) color.g = 1;
        if (color.b < 0) color.b = 0;
        else if (color.b > 1) color.b = 1;
    }

    /**
     * Sets the RGB of the color from the input CIELAB color space. The result will be in linear RGB color (not gamma
     * compressed sRGB).
     * @param color The color output.
     * @param labIn The input Lab color. Must be of length at least 3.
     */
    public static void fromLab(Color color, float[] labIn) {
        fromLab(color, labIn[0], labIn[1], labIn[2]);
    }

    /**
     * Converts the color to CIELAB color space. Assumes the input color is already in linear RGB (not gamma corrected
     * sRGB).
     * @param color The input color in linear RGB.
     * @param labOut The output Lab color. Must be of length at least 3.
     */
    public static void toLab(Color color, float[] labOut) {
        toLab(color.r, color.g, color.b, labOut);
    }


    /**
     * Converts the color to CIELAB color space. Assumes the input color is already in linear RGB (not gamma corrected
     * sRGB).
     * @param r Input red channel in linear space.
     * @param g Input green channel in linear space.
     * @param b Input blue channel in linear space.
     * @param labOut The output Lab color. Must be of length at least 3.
     */
    public static void toLab(float r, float g, float b, float[] labOut) {
        float xD65 = 0.4124564f * r + 0.3575761f * g + 0.1804375f * b; //TODO use more accurate values from IPT function
        float yD65 = 0.2126729f * r + 0.7151522f * g + 0.0721750f * b;
        float zD65 = 0.0193339f * r + 0.1191920f * g + 0.9503041f * b;
        float xF = forwardTransformXyzLab(xD65);
        float yF = forwardTransformXyzLab(yD65);
        float zF = forwardTransformXyzLab(zD65);
        labOut[0] = 116f * yF - 16f;
        labOut[1] = 500f * (xF - yF);
        labOut[2] = 200f * (yF - zF);
    }

    /**
     * Sets the RGB of the color from the input cylindrical CIELAB (aka HCL) color space. The result will be in linear
     * RGB color (not gamma compressed sRGB).
     *
     * @param color Output color.
     * @param lchIn Input color array, with Luminance, chroma and hue angle in the first three indices respectively.
     *              Length must be at least 3. The hue angle is in degrees.
     */
    public static void fromLch(Color color, float[] lchIn) {
        fromLch(color, lchIn[0], lchIn[1], lchIn[2]);
    }

    /**
     * Sets the RGB of the color from the input cylindrical CIELAB color space. The result will be in linear
     * RGB color (not gamma compressed sRGB).
     *
     * @param color Output color.
     * @param l The input L* luminance channel.
     * @param c The input chroma channel.
     * @param h The input hue channel in degrees.
     */
    public static void fromLch(Color color, float l, float c, float h) {
        h *= MathUtils.degRad;
        float a = c * MathUtils.cos(h);
        float b = c * MathUtils.sin(h);
        fromLab(color, l, a, b);
    }

    /**
     * Outputs the RGB of the color in the cylindrical CIELAB color space. Assumes the input color is already
     * in linear RGB (not gamma corrected sRGB).
     *
     * @param color  Input color.
     * @param lchOut Array the result will be placed in, with Luminance, chroma and hue angle in the first three indices
     *               respectively. Length must be at least 3. The hue angle is in degrees and is in the range -180..180.
     */
    public static void toLch(Color color, float[] lchOut) {
        toLch(color.r, color.g, color.b, lchOut);
    }

    /**
     * Outputs the RGB of the color in the cylindrical CIELAB color space. Assumes the input color is already
     * in linear RGB (not gamma corrected sRGB).
     *
     * @param r Input red channel in linear space.
     * @param g Input green channel in linear space.
     * @param b Input blue channel in linear space.
     * @param lchOut Array the result will be placed in, with Luminance, chroma and hue angle in the first three indices
     *               respectively. Length must be at least 3. The hue angle is in degrees and is in the range -180..180.
     */
    public static void toLch(float r, float g, float b, float[] lchOut) {
        toLab(r, g, b, lchOut);
        float aStar = lchOut[1];
        float bStar = lchOut[2];
        lchOut[1] = (float) Math.sqrt(aStar * aStar + bStar * bStar);
        lchOut[2] = GtMathUtils.atan2(bStar, aStar) * MathUtils.radDeg;
    }

    /**
     * Sets the RGB of the color from the input L'M'S' as defined by the IPT color space. The result will be in linear
     * RGB color (not gamma compressed sRGB).
     * <p>
     * IPT color space is described in "Derivation and modelling hue uniformity and development of the IPT color space"
     * (1998) by Fritz Ebner. Accessed from RIT Scholar Works.
     * @param color Output color.
     * @param lmsPrimeIn Input color array with L', M', and S' channels in the first three indices, respectively. Must
     *              be of length of at least 3.
     */
    public static void fromLmsCompressed(Color color, float[] lmsPrimeIn) {
        fromLmsCompressed(color, lmsPrimeIn[0], lmsPrimeIn[1], lmsPrimeIn[2]);
    }

    /**
     * Sets the RGB of the color from the input L'M'S' as defined by the IPT color space. The result will be in linear
     * RGB color (not gamma compressed sRGB).
     * <p>
     * IPT color space is described in "Derivation and modelling hue uniformity and development of the IPT color space"
     * (1998) by Fritz Ebner. Accessed from RIT Scholar Works.
     * @param color Output color.
     * @param lPrime Input L' channel.
     * @param mPrime Input M' channel.
     * @param sPrime Input S' channel.
     */
    public static void fromLmsCompressed(Color color, float lPrime, float mPrime, float sPrime) {
        float l = reverseTransformLmsIpt(lPrime);
        float m = reverseTransformLmsIpt(mPrime);
        float s = reverseTransformLmsIpt(sPrime);
        float xD65 = 1.850243f * l - 1.1383f * m + 0.238435f * s;
        float yD65 = 0.366831f * l + 0.643885f * m - 0.01067f * s;
        float zD65 = 1.08885f * s;
        color.r = 3.24097f * xD65 - 1.53738f * yD65 - 0.49861f * zD65;
        color.g = -0.96924f * xD65 + 1.875968f * yD65 + 0.041555f * zD65;
        color.b = 0.05563f * xD65 - 0.20398f * yD65 + 1.056972f * zD65;
        if (color.r < 0) color.r = 0;
        else if (color.r > 1) color.r = 1;
        if (color.g < 0) color.g = 0;
        else if (color.g > 1) color.g = 1;
        if (color.b < 0) color.b = 0;
        else if (color.b > 1) color.b = 1;
    }

    /**
     * Outputs the RGB of the color in L'M'S' as defined by the IPT color space. Assumes the input color is already in
     * linear RGB (not gamma corrected sRGB).
     * <p>
     * IPT color space is described in "Derivation and modelling hue uniformity and development of the IPT color space"
     * (1998) by Fritz Ebner. Accessed from RIT Scholar Works.
     *
     * @param color  Input color.
     * @param lmsPrimeOut Array the result will be placed in, with L'M'S' channels in the first three indices
     *               respectively. Length must be at least 3.
     */
    public static void toLmsCompressed(Color color, float[] lmsPrimeOut) {
        toLmsCompressed(color.r, color.g, color.b, lmsPrimeOut);
    }

    /**
     * Outputs the RGB of the color in L'M'S' as defined by the IPT color space. Assumes the input color is already in
     * linear RGB (not gamma corrected sRGB).
     * <p>
     * IPT color space is described in "Derivation and modelling hue uniformity and development of the IPT color space"
     * (1998) by Fritz Ebner. Accessed from RIT Scholar Works.
     *
     * @param r Input red channel in linear space.
     * @param g Input green channel in linear space.
     * @param b Input blue channel in linear space.
     * @param lmsPrimeOut Array the result will be placed in, with IPT channels in the first three indices
     *               respectively. Length must be at least 3.
     */
    public static void toLmsCompressed(float r, float g, float b, float[] lmsPrimeOut) {
        float xD65 = 0.412391f * r + 0.357584f * g + 0.180481f * b;
        float yD65 = 0.212639f * r + 0.715169f * g + 0.072192f * b;
        float zD65 = 0.019331f * r + 0.119195f * g + 0.950532f * b;
        float l = 0.4002f * xD65 + 0.7075f * yD65 - 0.0807f * zD65;
        float m = -0.2280f * xD65 + 1.1500f * yD65 + 0.0612f * zD65;
        float s = 0.9184f * zD65;
        lmsPrimeOut[0] = forwardTransformLmsIpt(l);
        lmsPrimeOut[1] = forwardTransformLmsIpt(m);
        lmsPrimeOut[2] = forwardTransformLmsIpt(s);
    }

    /**
     * Sets the RGB of the color from the input IPT color space. The result will be in linear RGB color (not gamma
     * compressed sRGB).
     * <p>
     * IPT color space is described in "Derivation and modelling hue uniformity and development of the IPT color space"
     * (1998) by Fritz Ebner. Accessed from RIT Scholar Works.
     * @param color Output color.
     * @param iptIn Input color array with I, P, and T channels in the first three indices, respectively. Must be of
     *              length of at least 3.
     */
    public static void fromIpt(Color color, float[] iptIn) {
        fromIpt(color, iptIn[0], iptIn[1], iptIn[2]);
    }

    /**
     * Sets the RGB of the color from the input IPT color space. The result will be in linear RGB color (not gamma
     * compressed sRGB).
     * <p>
     * IPT color space is described in "Derivation and modelling hue uniformity and development of the IPT color space"
     * (1998) by Fritz Ebner. Accessed from RIT Scholar Works.
     * @param color Output color.
     * @param i Input I channel.
     * @param p Input P channel.
     * @param t Input T channel.
     */
    public static void fromIpt(Color color, float i, float p, float t) {
        float lPrime = i + 0.097569f * p + 0.205226f * t;
        float mPrime = i - 0.11388f * p + 0.133217f * t;
        float sPrime = i + 0.032615f * p - 0.67689f * t;
        float l = reverseTransformLmsIpt(lPrime);
        float m = reverseTransformLmsIpt(mPrime);
        float s = reverseTransformLmsIpt(sPrime);
        float xD65 = 1.850243f * l - 1.1383f * m + 0.238435f * s;
        float yD65 = 0.366831f * l + 0.643885f * m - 0.01067f * s;
        float zD65 = 1.08885f * s;
        color.r = 3.24097f * xD65 - 1.53738f * yD65 - 0.49861f * zD65;
        color.g = -0.96924f * xD65 + 1.875968f * yD65 + 0.041555f * zD65;
        color.b = 0.05563f * xD65 - 0.20398f * yD65 + 1.056972f * zD65;
        if (color.r < 0) color.r = 0;
        else if (color.r > 1) color.r = 1;
        if (color.g < 0) color.g = 0;
        else if (color.g > 1) color.g = 1;
        if (color.b < 0) color.b = 0;
        else if (color.b > 1) color.b = 1;
    }

    /**
     * Outputs the RGB of the color in IPT color space. Assumes the input color is already in linear RGB (not gamma
     * corrected sRGB).
     * <p>
     * IPT color space is described in "Derivation and modelling hue uniformity and development of the IPT color space"
     * (1998) by Fritz Ebner. Accessed from RIT Scholar Works.
     *
     * @param color  Input color.
     * @param iptOut Array the result will be placed in, with IPT channels in the first three indices
     *               respectively. Length must be at least 3.
     */
    public static void toIpt(Color color, float[] iptOut) {
        toIpt(color.r, color.g, color.b, iptOut);
    }

    /**
     * Outputs the RGB of the color in IPT color space. Assumes the input color is already in linear RGB (not gamma
     * corrected sRGB).
     * <p>
     * IPT color space is described in "Derivation and modelling hue uniformity and development of the IPT color space"
     * (1998) by Fritz Ebner. Accessed from RIT Scholar Works.
     *
     * @param r Input red channel in linear space.
     * @param g Input green channel in linear space.
     * @param b Input blue channel in linear space.
     * @param iptOut Array the result will be placed in, with IPT channels in the first three indices
     *               respectively. Length must be at least 3.
     */
    public static void toIpt(float r, float g, float b, float[] iptOut) {
        float xD65 = 0.412391f * r + 0.357584f * g + 0.180481f * b;
        float yD65 = 0.212639f * r + 0.715169f * g + 0.072192f * b;
        float zD65 = 0.019331f * r + 0.119195f * g + 0.950532f * b;
        float l = 0.4002f * xD65 + 0.7075f * yD65 - 0.0807f * zD65;
        float m = -0.2280f * xD65 + 1.1500f * yD65 + 0.0612f * zD65;
        float s = 0.9184f * zD65;
        float lPrime = forwardTransformLmsIpt(l);
        float mPrime = forwardTransformLmsIpt(m);
        float sPrime = forwardTransformLmsIpt(s);
        iptOut[0] = 0.4000f * lPrime + 0.4000f * mPrime + 0.2000f * sPrime;
        iptOut[1] = 4.4550f * lPrime - 4.8510f * mPrime + 0.3960f * sPrime;
        iptOut[2] = 0.8056f * lPrime + 0.3572f * mPrime - 1.1628f * sPrime;
    }

    /**
     * Converts a color from sRGB to linear RGB color space by removing gamma compression (gamma expansion).
     * @param color The source color
     * @param includeAlpha If true, alpha is also gamma expanded.
     * @return The same source color object, now in linear RGB color space.
     */
    public static Color gammaExpand(Color color, boolean includeAlpha){
        color.r = gammaExpand(color.r);
        color.g = gammaExpand(color.g);
        color.b = gammaExpand(color.b);
        if (includeAlpha) color.a = gammaExpand(color.a);
        return color;
    }

    /**
     * Converts a color from linear RGB color space to sRGB by applying gamma compression.
     * @param color The source color
     * @param includeAlpha If true, alpha is also gamma compressed.
     * @return The same source color object, now in sRGB color space.
     */
    public static Color gammaCompress(Color color, boolean includeAlpha) {
        color.r = gammaCompress(color.r);
        color.g = gammaCompress(color.g);
        color.b = gammaCompress(color.b);
        if (includeAlpha) color.a = gammaCompress(color.a);
        return color;
    }

    /**
     * Converts a color channel from gamma-corrected to linear scale.
     *
     * @param component The color channel to make linear.
     * @return The color value in linear scale.
     */
    public static float gammaExpand(float component) {
        return component <= 0.04045f ? component / 12.92f : (float) Math.pow((component + 0.055f) / 1.055f, 2.4);
    }

    /**
     * Converts a color channel from linear to gamma-corrected scale.
     *
     * @param component The color channel to gamma-correct.
     * @return The color value with gamma correction.
     */
    public static float gammaCompress(float component) {
        return component <= 0.0031308f ? 12.92f * component : 1.055f * (float) Math.pow(component, 1.0 / 2.4) - 0.055f;
    }

    private static float forwardTransformXyzLab(float component) {
        return component > SIGMA_LAB3 ? (float) Math.cbrt(component) : (KAP_LAB * component + 16f) / 116f;
    }

    private static float forwardTransformLmsIpt(float component) {
        return component >= 0f ? (float)Math.pow(component, 0.43) : -(float)Math.pow(-component, 0.43);
    }

    private static float reverseTransformLmsIpt(float component) {
        return component >= 0f ? (float)Math.pow(component, 2.3256) : -(float)Math.pow(-component, 2.3256);
    }

}
