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
package com.cyphercove.gdxtween.graphics

import com.badlogic.gdx.graphics.Color

/**
 * Converts the color from linear RGB color space to sRGB by applying gamma compression.
 * @param includeAlpha If true, alpha is also gamma compressed. Default true.
 * @return This color.
 */
fun Color.gammaCompress(includeAlpha: Boolean = true): Color {
    return GtColor.gammaCompress(this, includeAlpha)
}

/**
 * Converts the color from sRGB to linear RGB color space by removing gamma compression (gamma expansion).
 * @param includeAlpha If true, alpha is also gamma expanded. Default true.
 * @return This color.
 */
fun Color.gammaExpand(includeAlpha: Boolean = true): Color {
    return GtColor.gammaExpand(this, includeAlpha)
}

/**
 * Interpolates toward the target color.
 * @param target The color to interpolate toward.
 * @param t The interpolation coefficient in the range [0,1].
 * @param colorSpace The color space to perform the interpolation in.
 * @param includeAlpha Whether the alpha channel should also be interpolated. Default true.
 * @return This color.
 */
fun Color.lerp(target: Color, t: Float, colorSpace: ColorSpace, includeAlpha: Boolean = true): Color {
    return GtColor.lerp(this, target, t, colorSpace, includeAlpha)
}