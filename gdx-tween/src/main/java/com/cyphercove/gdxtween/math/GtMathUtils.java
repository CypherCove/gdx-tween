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
package com.cyphercove.gdxtween.math;

/**
 * Math helper functions.
 */
public final class GtMathUtils {
    private GtMathUtils () {
    }

    /**
     * Computes the common definition mathematical modulo of the given dividend and divisor, that is, the one with the
     * least positive remainder. The result is always between 0 (inclusive) and the absolute value of the divisor
     * (exclusive). This is useful for converting any angle, even negative angles, to the range {@code 0..360} or
     * {@code 0..2Pi}.
     * @param dividend Dividend (top number) of the Euclidean division.
     * @param divisor Divisor (bottom number) of the Euclidean division.
     * @return The least positive remainder of the division operation.
     */
    public static int modulo (int dividend, int divisor) {
        int mod = dividend % divisor;
        if (mod < 0) {
            mod = (divisor < 0) ? mod - divisor : mod + divisor;
        }
        return mod;
    }

    /**
     * Computes the common definition mathematical modulo of the given dividend and divisor, that is, the one with the
     * least positive remainder. The result is always between 0 (inclusive) and the absolute value of the divisor
     * (exclusive). This is useful for converting any angle, even negative angles, to the range {@code 0..360} or
     * {@code 0..2Pi}.
     * @param dividend Dividend (top number) of the Euclidean division.
     * @param divisor Divisor (bottom number) of the Euclidean division.
     * @return The least positive remainder of the division operation.
     */
    public static float modulo (float dividend, float divisor) {
        float mod = dividend % divisor;
        if (mod < 0f) {
            mod = (divisor < 0f) ? mod - divisor : mod + divisor;
        }
        return mod;
    }
}
