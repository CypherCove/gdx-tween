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
package com.cyphercove.gdxtween.desktop.utilities;

import com.badlogic.gdx.graphics.Color;
import com.cyphercove.gdxtween.graphics.GtColor;

public class LchErrorCheck {

    private static final Color COLOR = new Color(1f, 1f, 1f, 1f);
    private static final float[] LCH = new float[3];

    public static void main(String[] args) {
        int maxError = 0;
        for (int r = 0; r < 255; r++) {
            for (int g = 0; g < 255; g++) {
                for (int b = 0; b < 255; b++) {
                    maxError = Math.max(maxError, checkRoundTrip(r, g, b));
                }
            }
        }
        System.out.println("Max error: " + maxError);
    }

    private static int checkRoundTrip(int r, int g, int b) {
        int rgba = (r << 24) | (g << 16) | (b << 8) | 255;
        Color.rgba8888ToColor(COLOR, rgba);
        GtColor.toLch(COLOR, LCH);
        GtColor.fromLch(COLOR, LCH);
        int out = Color.rgba8888(COLOR);
        int outR = (out & 0xff000000) >>> 24;
        int outG = (out & 0x00ff0000) >>> 16;
        int outB = (out & 0x0000ff00) >>> 8;
        return Math.abs(outR - r) + Math.abs(outG - g) + Math.abs(outB - b);
    }

}
