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
package com.cyphercove.gdxtween.graphics;

/**
 * Color spaces that colors can be interpolated in. It is assumed that source colors can either be in sRGB or linear color
 * space. If they are in sRGB space, the Degamma spaces are the mathematically correct way to interpolate. The result
 * is returned in the original format.
 */
public enum ColorSpace {
    /**
     * Directly interpolate the RGB channels of a libGDX Color object.
     */
    Rgb(false),
    /**
     * Rgb with gamma correction removed, assuming the source Color object is stored in sRGB space. If it is already in
     * linear space, use Rgb instead.
     */
    DegammaRgb(true),
    /**
     * Color represented by hue, saturation, and lightness. This can prevent desaturated color from appearing in the middle
     * when interpolating between two saturated colors, but has a tendency to introduce intermediate hues which can
     * produce a rainbow effect. The shortest path around the hue circle is taken. Moderate computational cost.
     */
    Hsl(false),
    /**
     * Hsl with gamma correction removed, assuming the source Color object is stored in sRGB space. If it is already in
     * linear space, use Hsv instead.
     */
    DegammaHsl(true),
    /**
     * Color represented by hue, chroma, and lightness. This can prevent desaturated color from appearing in the middle
     * when interpolating between two saturated colors, but has a tendency to introduce intermediate hues which can
     * produce a rainbow effect. The shortest path around the hue circle is taken. Moderate computational cost.
     */
    Hcl(false),
    /**
     * Hcl with gamma correction removed, assuming the source Color object is stored in sRGB space. If it is already in
     * linear space, use Hsv instead.
     */
    DegammaHcl(true),
    /**
     * Color represented by hue, chroma, and lightness, but interpolated by shortest path in the 3D bicone. Moderate
     * computational cost.
     */
    EuclideanHcl(false),
    /**
     * EuclideanHcl with gamma correction removed, assuming the source Color object is stored in sRGB space. If it is
     * already in linear space, use Hsv instead.
     */
    DegammaEuclideanHcl(true),
    /**
     * Color represented by hue, saturation, and value. This can prevent desaturated color from appearing in the middle
     * when interpolating between two saturated colors, but has a tendency to introduce intermediate hues which can
     * produce a rainbow effect. The shortest path around the hue circle is taken. Moderate computational cost.
     */
    Hsv(false),
    /**
     * Hsv with gamma correction removed, assuming the source Color object is stored in sRGB space. If it is already in
     * linear space, use Hsv instead.
     */
    DegammaHsv(true),
    /**
     * CIELAB, which represents colors in a way that makes linear changes look visually linear to the human eye. This
     * creates an extremely smooth transition but may produce faint intermediate hues. If the
     * source Color object is in sRGB space, use DegammaLab instead, or the changes will not look visually linear.
     */
    Lab(false),
    /**
     * Lab with gamma correction removed, assuming the source Color object is stored in sRGB space. If it is already in
     * linear space, use Lab instead.
     */
    DegammaLab(true),
    /**
     * CIELAB converted to cylyndrical coordinates, which may be more intuitive to work with. This has a tendency to
     * produce intermediate hues which may have a rainbow effect.
     */
    Lch(false),
    /**
     * Lch with gamma correction removed, assuming the source Color object is stored in sRGB space. If it is already in
     * linear space, use Lch instead.
     */
    DegammaLch(true),
    /**
     * One transformation short of IPT color space
     * <p>
     * IPT color space is described in "Derivation and modelling hue uniformity and development of the IPT color space"
     * (1998) by Fritz Ebner. Accessed from RIT Scholar Works.
     */
    PartialIpt(false),
    /**
     * PartialIpt with gamma correction removed, assuming the source Color object is stored in sRGB space. If it is
     * already in linear space, use PartialIpt instead.
     */
    DegammaPartialIpt(true),
    /**
     * IPT color space, which represents colors in a way that makes linear changes look visually linear to the human eye,
     * and has better hue stability than CIELAB.  High computational cost.
     * <p>
     * IPT color space is described in "Derivation and modelling hue uniformity and development of the IPT color space"
     * (1998) by Fritz Ebner. Accessed from RIT Scholar Works.
     */
    Ipt(false),
    /**
     * Ipt with gamma correction removed, assuming the source Color object is stored in sRGB space. If it is already in
     * linear space, use Ipt instead.
     */
    DegammaIpt(true);

    /**
     * Whether the color will be gamma expanded before interpolating.
     */
    public final boolean isDegamma;

    ColorSpace(boolean isDegamma) {
        this.isDegamma = isDegamma;
    }
}
