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
 * Color spaces that colors can be interpolated in.
 */
public enum ColorSpace {
    /**
     * The way colors in LibGDX are stored and used by OpenGL.
     */
    Rgb,
    /**
     * Rgb with gamma correction removed. When interpolated this way, the transition is visually smoother. Moderate
     * computational cost.
     */
    LinearRgb,
    /**
     * Color represented by hue, saturation, and value. This can prevent desaturated color from appearing in the middle
     * when interpolating between two saturated colors, but has a tendency to introduce intermediate hues which can
     * produce a rainbow effect. Moderate computational cost.
     */
    Hsv,
    /**
     * CIELAB, which represents colors in a way that makes linear changes look visually linear to the human eye. This
     * creates an extremely smooth transition but may produce faint intermediate hues. High computational cost.
     */
    Lab
}
